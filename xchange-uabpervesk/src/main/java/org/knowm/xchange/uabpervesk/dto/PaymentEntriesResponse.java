package org.knowm.xchange.uabpervesk.dto;

import java.util.List;
import lombok.Data;

@Data
public class PaymentEntriesResponse {
  List<PaymentEntry> results;
}
