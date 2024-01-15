package org.knowm.xchange.uabpervesk;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.io.IOException;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.FundingRecord;

public class MainTest {

  public static void main(String[] args) throws IOException {

    Exchange e = new BankeraExchange();
    ExchangeSpecification exchangeSpec = e.getDefaultExchangeSpecification();
    //exchangeSpec.setUserName(apiAccount.getCryptoUserName());
    //exchangeSpec.setPassword(apiAccount.getCryptoPassword());

    exchangeSpec.setApiKey("1bc8a48c9964821b4eb316c1121e3e0e42ce302157b46fcc4d1130f09976adaf9abbccd574e61036");
    exchangeSpec.setSecretKey("VEiytGmObDJpJJB4g95fhG0LDxN7DwVIQ8ZKNP2Afkkv56IF4U0rkzf2vCUU5TLl");

    Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpec);

    AccountInfo bla = exchange.getAccountService().getAccountInfo();
    List<FundingRecord> xxx = exchange.getAccountService()
        .getFundingHistory(null);


  }
}
