package org.knowm.xchange.uabpervesk.service;

import java.io.IOException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.uabpervesk.BankeraAdapters;
import org.knowm.xchange.uabpervesk.dto.BankeraException;
import org.knowm.xchange.uabpervesk.dto.marketdata.BankeraMarketInfo;
import org.knowm.xchange.uabpervesk.dto.marketdata.BankeraOrderBook;
import org.knowm.xchange.uabpervesk.dto.marketdata.BankeraTickerResponse;
import org.knowm.xchange.uabpervesk.dto.marketdata.BankeraTradesResponse;
import org.knowm.xchange.currency.CurrencyPair;

public class BankeraMarketDataServiceRaw extends BankeraBaseService {

  public BankeraMarketDataServiceRaw(Exchange exchange) {
    super(exchange);
  }

  public BankeraTickerResponse getBankeraTicker(CurrencyPair currencyPair) throws IOException {

    try {
      return bankera.getMarketTicker(getMarketNameFromPair(currencyPair));
    } catch (BankeraException e) {
      throw BankeraAdapters.adaptError(e);
    }
  }

  public BankeraOrderBook getOrderbook(CurrencyPair currencyPair) throws IOException {

    try {
      return bankera.getOrderbook(getMarketNameFromPair(currencyPair));
    } catch (BankeraException e) {
      throw BankeraAdapters.adaptError(e);
    }
  }

  public BankeraMarketInfo getMarketInfo() throws IOException {

    try {
      return bankera.getMarketInfo();
    } catch (BankeraException e) {
      throw BankeraAdapters.adaptError(e);
    }
  }

  public BankeraTradesResponse getRecentTrades(CurrencyPair currencyPair) throws IOException {

    try {
      return bankera.getRecentTrades(getMarketNameFromPair(currencyPair));
    } catch (BankeraException e) {
      throw BankeraAdapters.adaptError(e);
    }
  }

  private static String getMarketNameFromPair(CurrencyPair pair) {

    return pair == null
        ? null
        : new StringBuilder()
            .append(pair.base.getCurrencyCode())
            .append("-")
            .append(pair.counter.getCurrencyCode())
            .toString();
  }
}
