package org.knowm.xchange.coinbase;

import java.io.IOException;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coinbase.v2.CoinbaseExchange;
import org.knowm.xchange.dto.account.FundingRecord;

public class MainTest {

  public static void main(String[] args) throws IOException {
    Exchange e = new CoinbaseExchange();
    ExchangeSpecification exchangeSpec = e.getDefaultExchangeSpecification();

    exchangeSpec.setApiKey("0aKBWdZiA0Dqx5Eb");
    exchangeSpec.setSecretKey("5g6pp6MsrAF3hrTjc3skoV94YIx2nG0o");

    Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpec);

    List<FundingRecord> xxx = exchange.getAccountService()
        .getFundingHistory(null);


  }

}
