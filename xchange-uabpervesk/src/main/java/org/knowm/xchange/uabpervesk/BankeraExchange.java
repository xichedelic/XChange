package org.knowm.xchange.uabpervesk;

import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.uabpervesk.dto.BankeraException;
import org.knowm.xchange.uabpervesk.dto.BankeraToken;
import org.knowm.xchange.uabpervesk.service.BankeraAccountService;
import org.knowm.xchange.uabpervesk.service.BankeraBaseService;
import org.knowm.xchange.uabpervesk.service.BankeraMarketDataService;
import org.knowm.xchange.uabpervesk.service.BankeraTradeService;
import org.knowm.xchange.utils.nonce.TimestampIncrementingNonceFactory;
import si.mazi.rescu.SynchronizedValueFactory;

public class BankeraExchange extends BaseExchange implements Exchange {

  private SynchronizedValueFactory<Long> nonceFactory = new TimestampIncrementingNonceFactory();
  protected BankeraToken token;

  @Override
  protected void initServices() {
    this.accountService = new BankeraAccountService(this);
    this.marketDataService = new BankeraMarketDataService(this);
    this.tradeService = new BankeraTradeService(this);
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {

    ExchangeSpecification exchangeSpecification = new ExchangeSpecification(this.getClass());
    exchangeSpecification.setSslUri("https://ib.bankera.com/api/public/");
    exchangeSpecification.setHost("ib.bankera.com");
    exchangeSpecification.setPort(443);
    exchangeSpecification.setExchangeName("Bankera");
    exchangeSpecification.setExchangeDescription("The Bankera exchange.");

    return exchangeSpecification;
  }

  @Override
  public SynchronizedValueFactory<Long> getNonceFactory() {
    return nonceFactory;
  }

  public BankeraToken getToken() {
    if (token == null || token.getExpirationTime() < System.currentTimeMillis() + 30000L) {
      try {
        token = ((BankeraBaseService) accountService).createToken();
      } catch (BankeraException e) {
        throw BankeraAdapters.adaptError(e);
      }
    }
    return token;
  }
}
