package org.knowm.xchange.uabpervesk.dto.account;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;
@Data
public class AccountInfoResponse {
  String number;
  String status;
  String holderPartyId;
  Map<String, BigDecimal> availableFunds;
  Map<String, BigDecimal> balances;
  Map<String, BigDecimal> reservations;
}
