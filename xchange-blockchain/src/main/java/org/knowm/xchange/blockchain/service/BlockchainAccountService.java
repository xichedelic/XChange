package org.knowm.xchange.blockchain.service;

import org.knowm.xchange.blockchain.BlockchainAdapters;
import org.knowm.xchange.blockchain.BlockchainAuthenticated;
import org.knowm.xchange.blockchain.BlockchainErrorAdapter;
import org.knowm.xchange.blockchain.BlockchainExchange;
import org.knowm.xchange.blockchain.dto.BlockchainException;
import org.knowm.xchange.blockchain.dto.account.BlockchainAccountInformation;
import org.knowm.xchange.blockchain.dto.account.BlockchainFees;
import org.knowm.xchange.blockchain.params.BlockchainFundingHistoryParams;
import org.knowm.xchange.client.ResilienceRegistries;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.*;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.knowm.xchange.blockchain.BlockchainConstants.FUNDING_RECORD_TYPE_UNSUPPORTED;
import static org.knowm.xchange.blockchain.BlockchainConstants.WITHDRAWAL_EXCEPTION;

public class BlockchainAccountService extends BlockchainAccountServiceRaw implements AccountService {

    public BlockchainAccountService(BlockchainExchange exchange, BlockchainAuthenticated blockchainApi, ResilienceRegistries resilienceRegistries) {
        super(exchange, blockchainApi, resilienceRegistries);
    }

    @Override
    public AccountInfo getAccountInfo() throws IOException {
        try {

            Map<String, List<BlockchainAccountInformation>> account = this.getAccountInformation();
            List<Wallet> wallets = new ArrayList<>();

            for (Map.Entry<String, List<BlockchainAccountInformation>> entry : account.entrySet()) {
                List<Balance> balances = entry.getValue().stream().map(bal -> new Balance(bal.getCurrency(),
                                bal.getTotal(),
                                bal.getAvailable()))
                        .collect(Collectors.toList());
                wallets.add(Wallet.Builder.from(balances).id(entry.getKey()).build());
            }
            return new AccountInfo(wallets);
        } catch (BlockchainException e) {
            throw BlockchainErrorAdapter.adapt(e);
        }
    }

    @Override
    public String withdrawFunds(Currency currency, BigDecimal amount, String address) throws IOException {
        try {
            return this.postWithdrawFunds(BlockchainAdapters.toWithdrawalRequest(currency, amount, address)).getWithdrawalId();
        } catch (BlockchainException e) {
            throw BlockchainErrorAdapter.adapt(e);
        }
    }

    @Override
    public String withdrawFunds(Currency currency, BigDecimal amount, AddressWithTag address) throws IOException {
        return withdrawFunds(new DefaultWithdrawFundsParams(address, currency, amount));
    }

    @Override
    public String withdrawFunds(WithdrawFundsParams params) throws IOException {
        if (params instanceof DefaultWithdrawFundsParams) {
            DefaultWithdrawFundsParams defaultParams = (DefaultWithdrawFundsParams) params;
            return postWithdrawFunds(BlockchainAdapters.toWithdrawalRequest(
                    defaultParams.getCurrency(),
                    defaultParams.getAmount(),
                    defaultParams.getAddress()
            )).getWithdrawalId();
        }

        throw new IllegalStateException(WITHDRAWAL_EXCEPTION + params);
    }

    @Override
    public String requestDepositAddress(Currency currency, String... args) throws IOException {
        return this.getDepositAddress(currency).getAddress();
    }

    @Override
    public AddressWithTag requestDepositAddressData(Currency currency, String... args) throws IOException {
        return BlockchainAdapters.toAddressWithTag(this.getDepositAddress(currency));
    }

    @Override
    public TradeHistoryParams createFundingHistoryParams() {
        return BlockchainFundingHistoryParams.builder().build();
    }

    @Override
    public List<FundingRecord> getFundingHistory(TradeHistoryParams params) throws IOException {
        try {
            Long startTime = null;
            Long endTime = null;

            if (params instanceof TradeHistoryParamsTimeSpan) {
                TradeHistoryParamsTimeSpan tp = (TradeHistoryParamsTimeSpan) params;
                if (tp.getStartTime() != null) {
                    startTime = tp.getStartTime().getTime();
                }
                if (tp.getEndTime() != null) {
                    endTime = tp.getEndTime().getTime();
                }
            }

            List<FundingRecord> result = new ArrayList<>();
            if (params instanceof HistoryParamsFundingType) {
                if (((HistoryParamsFundingType) params).getType() != null) {
                    switch (((HistoryParamsFundingType) params).getType()) {
                        case WITHDRAWAL:
                            this.withdrawHistory(startTime, endTime)
                                    .forEach(w -> result.add(BlockchainAdapters.toFundingWithdrawal(w)));
                            break;
                        case DEPOSIT:
                            this.depositHistory(startTime, endTime)
                                    .forEach(d -> result.add(BlockchainAdapters.toFundingDeposit(d)));
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    FUNDING_RECORD_TYPE_UNSUPPORTED
                                            + ((HistoryParamsFundingType) params).getType());
                    }
                }
            }

            return result;
        } catch (BlockchainException e) {
            throw BlockchainErrorAdapter.adapt(e);
        }
    }

    @Override
    public Map<CurrencyPair, Fee> getDynamicTradingFees() throws IOException {
        try {
            BlockchainFees fees = this.getFees();

            Map<CurrencyPair, Fee> tradingFees = new HashMap<>();
            List<CurrencyPair> pairs = this.getExchangeSymbols();

            pairs.forEach(pair -> tradingFees.put(pair, new Fee(fees.getMakerRate(), fees.getTakerRate())));
            return tradingFees;
        } catch (BlockchainException e) {
            throw BlockchainErrorAdapter.adapt(e);
        }
    }

    @Override
    public Map<Instrument, Fee> getDynamicTradingFeesByInstrument() throws IOException {
        Map<CurrencyPair, Fee> dynamicTradingFees = getDynamicTradingFees();
        return dynamicTradingFees.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
