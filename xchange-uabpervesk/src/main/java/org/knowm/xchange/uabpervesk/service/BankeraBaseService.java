package org.knowm.xchange.uabpervesk.service;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.uabpervesk.Bankera;
import org.knowm.xchange.uabpervesk.BankeraAuthenticated;
import org.knowm.xchange.uabpervesk.dto.BankeraException;
import org.knowm.xchange.uabpervesk.dto.BankeraToken;
import org.knowm.xchange.client.ExchangeRestProxyBuilder;
import org.knowm.xchange.service.BaseExchangeService;
import org.knowm.xchange.service.BaseService;

public class BankeraBaseService extends BaseExchangeService implements BaseService {

  protected final Bankera bankera;
  protected final BankeraAuthenticated bankeraAuthenticated;

  public BankeraBaseService(Exchange exchange) {
    super(exchange);
    bankera =
        ExchangeRestProxyBuilder.forInterface(Bankera.class, exchange.getExchangeSpecification())
            .build();
    bankeraAuthenticated =
        ExchangeRestProxyBuilder.forInterface(
                BankeraAuthenticated.class, exchange.getExchangeSpecification())
            .build();
  }

  public BankeraToken createToken() throws BankeraException {
    String clientId = exchange.getExchangeSpecification().getApiKey();
    String clientSecret = exchange.getExchangeSpecification().getSecretKey();
    return bankera.getToken(clientId, clientSecret, "client_credentials", "PARTIES ACCOUNTS PAYMENTS");
  }
}
