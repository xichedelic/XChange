package org.knowm.xchange.binance.dto.account;

import java.util.List;
import lombok.Data;

@Data
public class ConvertHistoryResponse {
  public List<ConvertHistory> list;
  public long startTime;
  public long endTime;
  public int limit;
  public boolean moreData;
}
