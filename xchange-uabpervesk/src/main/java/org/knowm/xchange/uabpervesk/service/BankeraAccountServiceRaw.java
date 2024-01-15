package org.knowm.xchange.uabpervesk.service;

import java.io.IOException;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.uabpervesk.BankeraAdapters;
import org.knowm.xchange.uabpervesk.BankeraExchange;
import org.knowm.xchange.uabpervesk.dto.BankeraException;
import org.knowm.xchange.uabpervesk.dto.PaymentEntriesResponse;
import org.knowm.xchange.uabpervesk.dto.PaymentEntry;
import org.knowm.xchange.uabpervesk.dto.account.BankeraUserInfo;

public class BankeraAccountServiceRaw extends BankeraBaseService {

  public BankeraAccountServiceRaw(Exchange exchange) {
    super(exchange);
  }

  public BankeraUserInfo getUserInfo() throws IOException {
    try {
      BankeraExchange bankeraExchange = (BankeraExchange) exchange;
      String auth = "Bearer " + bankeraExchange.getToken().getAccessToken();
      return bankeraAuthenticated.getUserInfo(auth);
    } catch (BankeraException e) {
      throw BankeraAdapters.adaptError(e);
    }
  }

  public PaymentEntriesResponse getPaymentEntries() throws IOException {
    try {
      BankeraExchange bankeraExchange = (BankeraExchange) exchange;
      String auth = "Bearer " + bankeraExchange.getToken().getAccessToken();
      return bankeraAuthenticated.getPaymentEntries(auth);
    } catch (BankeraException e) {
      throw BankeraAdapters.adaptError(e);
    }
  }
}
