package org.knowm.xchange.binance;

import java.io.IOException;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.FundingRecord;

public class MainTest {

  public static void main(String[] args) throws IOException {
    Exchange e = new BinanceExchange();
    ExchangeSpecification exchangeSpec = e.getDefaultExchangeSpecification();
    //exchangeSpec.setUserName(apiAccount.getCryptoUserName());
    //exchangeSpec.setPassword(apiAccount.getCryptoPassword());

    exchangeSpec.setApiKey("mBf83y2d05MJ0MKIh8LPlmu8rZG1JbABPu5EOSQZbrHYUH1JQoMhgDfgDLogT07w");
    exchangeSpec.setSecretKey("8Tl9QX1SmAGebRlLt9vLjbHPQ8IWejMcR6q0Jxadbt43L26JeU8Qge63dCzz0jvu");

    Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpec);

    List<FundingRecord> xxx = exchange.getAccountService()
        .getFundingHistory(null);


  }

}
