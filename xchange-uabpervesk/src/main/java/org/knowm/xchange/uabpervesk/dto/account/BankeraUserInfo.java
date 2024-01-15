package org.knowm.xchange.uabpervesk.dto.account;

import java.util.List;
import lombok.Data;

@Data
public class BankeraUserInfo {
  private List<AccountInfoResponse> results;
}
