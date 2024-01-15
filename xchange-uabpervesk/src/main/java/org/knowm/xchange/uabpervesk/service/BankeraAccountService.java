package org.knowm.xchange.uabpervesk.service;

import java.io.IOException;
import java.sql.Date;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.Status;
import org.knowm.xchange.dto.account.FundingRecord.Type;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.uabpervesk.BankeraAdapters;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.uabpervesk.MxCamtConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankeraAccountService extends BankeraAccountServiceRaw implements AccountService {
  protected final Logger LOG = LoggerFactory.getLogger(getClass());
  MxCamtConverter mxCamtConverter;
  public BankeraAccountService(Exchange exchange) {
    super(exchange);
    mxCamtConverter = new MxCamtConverter();
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {
    return BankeraAdapters.adaptAccountInfo(getUserInfo());
  }

  @Override
  public List<FundingRecord> getFundingHistory(TradeHistoryParams params) throws IOException {

    return getPaymentEntries()
        .getResults()
        .stream()
        .map(pe ->
            new FundingRecord.Builder()
                .setInternalId(pe.getServiceId())
                .setDate(pe.getDate())
                .setType("CREDIT".equals(pe.getDirection()) ? Type.DEPOSIT : Type.WITHDRAWAL)
                .setStatus("COMPLETED".equals(pe.getStatus()) ? Status.COMPLETE : Status.PROCESSING)
                .setDescription(pe.getServiceType() + " " + pe.getPaymentCode())
                .setAmount(pe.getAmount())
                .setCurrency(Currency.getInstance(pe.getCurrency()))
                .setFee(pe.getFeeAmount())
                .build()
        )
        .collect(Collectors.toList());

  }

  @Override
  public TradeHistoryParams createFundingHistoryParams() {
    return new BankeraTradeHistoryParams();
  }

  //  @Override
//  public List<FundingRecord> getFundingHistory(TradeHistoryParams params) throws IOException {
//    List<FundingRecord> fundingRecords = new ArrayList<>();
//    getAccountInfo().getWallets().forEach((curr, wallet) ->
//      wallet.getBalances().forEach((c,b) -> {
//
//        try {
//          String ret = getFR(wallet.getId(), c.getCurrencyCode());
//          List<FundingRecord> records = mxCamtConverter.convertFundingRecords(ret);
//          fundingRecords.addAll(records);
//        } catch (Exception e) {
//          LOG.error("error fetching transactions for " + wallet.getId() + " " + c.getCurrencyCode(), e);
//        }
//      })
//    );
//
//    return fundingRecords;
//  }

  public static class BankeraTradeHistoryParams implements TradeHistoryParams {

  }
}
