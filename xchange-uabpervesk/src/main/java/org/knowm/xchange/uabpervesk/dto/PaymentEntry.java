package org.knowm.xchange.uabpervesk.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class PaymentEntry {
  Date date;
  String serviceId;
  String direction;
  String serviceType;

  BigDecimal amount;
  String currency;

  BigDecimal feeAmount;
  String feeCurrency;

  String feeAccountNumber;
  String status;
  String paymentCode;
}
