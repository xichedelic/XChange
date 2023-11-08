package org.knowm.xchange.coinbase.v2.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinbase.v2.dto.CoinbaseAmount;
import org.knowm.xchange.coinbase.v2.dto.account.CoinbaseAccountData;
import org.knowm.xchange.coinbase.v2.dto.account.CoinbaseAccountData.CoinbaseAccount;
import org.knowm.xchange.coinbase.v2.dto.account.CoinbaseTransactionsResponse;
import org.knowm.xchange.coinbase.v2.dto.account.transactions.CoinbaseTransactionV2;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.Status;
import org.knowm.xchange.dto.account.FundingRecord.Type;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.DefaultWithdrawFundsParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.WithdrawFundsParams;

public final class CoinbaseAccountService extends CoinbaseAccountServiceRaw
    implements AccountService {

  public CoinbaseAccountService(Exchange exchange) {

    super(exchange);
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {
    List<Wallet> wallets = new ArrayList<>();

    List<CoinbaseAccountData.CoinbaseAccount> coinbaseAccounts = getCoinbaseAccounts();
    for (CoinbaseAccountData.CoinbaseAccount coinbaseAccount : coinbaseAccounts) {
      CoinbaseAmount balance = coinbaseAccount.getBalance();
      Wallet wallet =
          Wallet.Builder.from(
                  Arrays.asList(
                      new Balance(
                          Currency.getInstance(balance.getCurrency()), balance.getAmount())))
              .id(coinbaseAccount.getId())
              .build();
      wallets.add(wallet);
    }

    return new AccountInfo(wallets);
  }

  @Override
  public String withdrawFunds(WithdrawFundsParams params)
      throws ExchangeException, NotAvailableFromExchangeException,
          NotYetImplementedForExchangeException, IOException {
    if (params instanceof DefaultWithdrawFundsParams) {
      DefaultWithdrawFundsParams defaultParams = (DefaultWithdrawFundsParams) params;
      return withdrawFunds(
          defaultParams.getCurrency(), defaultParams.getAmount(), defaultParams.getAddress());
    }
    throw new IllegalStateException("Don't know how to withdraw: " + params);
  }

  @Override
  public TradeHistoryParams createFundingHistoryParams() {
    return new TradeHistoryParams() {
      @Override
      public int hashCode() {
        return super.hashCode();
      }
    };
  }

  @Override
  public List<FundingRecord> getFundingHistory(TradeHistoryParams params) throws IOException {
    List<FundingRecord> fundingRecords = new LinkedList<>();
    List<CoinbaseAccount> accounts = getCoinbaseAccounts();

    accounts.forEach(account -> {
      try {
        Map deposits = super.getDeposits(account.getId());
        List<Map> depositsData = (List) deposits.get("data");
        depositsData.forEach(deposit -> {
          Map amount = (Map)deposit.get("amount");
          Map fee = (Map)deposit.get("fee");

          fundingRecords.add(
              new FundingRecord.Builder()
                  .setInternalId((String)deposit.get("id"))
                  .setDate(Date.from(ZonedDateTime.parse((String)deposit.get("created_at")).toInstant()))
                  .setAmount(new BigDecimal((String)amount.get("amount")))
                  .setFee(new BigDecimal((String)fee.get("amount")))
                  .setCurrency(Currency.getInstance((String)amount.get("currency")))
                  .setType(Type.DEPOSIT)
                  .setStatus(Status.COMPLETE)
                  .build()
          );
        });

        Map withdrawls = super.getWithdrawals(account.getId());
        List<Map> withdrawlsData = (List) withdrawls.get("data");
        withdrawlsData.forEach(withdrawal -> {
          Map amount = (Map)withdrawal.get("amount");
          Map fee = (Map)withdrawal.get("fee");

          fundingRecords.add(
              new FundingRecord.Builder()
                  .setInternalId((String)withdrawal.get("id"))
                  .setDate(Date.from(ZonedDateTime.parse((String)withdrawal.get("created_at")).toInstant()))
                  .setAmount(new BigDecimal((String)amount.get("amount")))
                  .setFee(new BigDecimal((String)fee.get("amount")))
                  .setCurrency(Currency.getInstance((String)amount.get("currency")))
                  .setType(Type.WITHDRAWAL)
                  .setStatus(Status.COMPLETE)
                  .build()
          );
        });

        CoinbaseTradeHistoryParams cthp = new CoinbaseTradeHistoryParams();
        cthp.setLimit(300);

        CoinbaseTradeService ts = (CoinbaseTradeService) exchange.getTradeService();
        UserTrades buys = ts.getBuyTradeHistory(cthp, account.getId());
        UserTrades sells = ts.getSellTradeHistory(cthp, account.getId());

        buys.getUserTrades().forEach(ut -> {
          fundingRecords.add(
              new FundingRecord.Builder()
                  .setInternalId(ut.getOrderId())
                  .setDate(ut.getTimestamp())
                  .setAmount(ut.getOriginalAmount())
                  .setFee(BigDecimal.ZERO)
                  .setCurrency(ut.getInstrument().getBase())
                  .setType(Type.DEPOSIT)
                  .setStatus(Status.COMPLETE)
                  .build()
          );

          if (!BigDecimal.ZERO.equals(ut.getFeeAmount())) {
            fundingRecords.add(
                new FundingRecord.Builder()
                    .setInternalId(ut.getOrderId() + "-fee")
                    .setDate(ut.getTimestamp())
                    .setAmount(ut.getFeeAmount().multiply(BigDecimal.valueOf(-1)))
                    .setFee(BigDecimal.ZERO)
                    .setCurrency(ut.getFeeCurrency())
                    .setType(Type.DEPOSIT)
                    .setStatus(Status.COMPLETE)
                    .build()
            );
          }
        });


        sells.getUserTrades().forEach(ut -> {
          fundingRecords.add(
              new FundingRecord.Builder()
                  .setInternalId(ut.getOrderId())
                  .setDate(ut.getTimestamp())
                  .setAmount(ut.getOriginalAmount())
                  .setFee(BigDecimal.ZERO)
                  .setCurrency(ut.getInstrument().getBase())
                  .setType(Type.WITHDRAWAL)
                  .setStatus(Status.COMPLETE)
                  .build()
          );

          if (!BigDecimal.ZERO.equals(ut.getFeeAmount())) {
            fundingRecords.add(
                new FundingRecord.Builder()
                    .setInternalId(ut.getOrderId() + "-fee")
                    .setDate(ut.getTimestamp())
                    .setAmount(ut.getFeeAmount().multiply(BigDecimal.valueOf(-1)))
                    .setFee(BigDecimal.ZERO)
                    .setCurrency(ut.getFeeCurrency())
                    .setType(Type.WITHDRAWAL)
                    .setStatus(Status.COMPLETE)
                    .build()
            );
          }
        });

        CoinbaseTransactionsResponse transactions = super.getTransactions(
            account.getId());

        transactions
            .getData()
            .stream()
            .filter(f -> f.getType().equals("send"))
            .forEach(transaction -> {
          fundingRecords.add(
              new FundingRecord.Builder()
                  .setInternalId(transaction.getId())
                  .setDate(java.sql.Timestamp.valueOf(transaction.getCreatedAt().toLocalDateTime()))
                  .setAmount(transaction.getAmount().getAmount())
                  .setFee(BigDecimal.ZERO)
                  .setCurrency(Currency.getInstance(transaction.getAmount().getCurrency()))
                  .setType(transaction.getAmount().getAmount().signum() > 0 ? Type.DEPOSIT : Type.WITHDRAWAL)
                  .setStatus(Status.COMPLETE)
                  .build()
          );
        });


        System.out.println("bla");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    });

    return fundingRecords;
  }

}
