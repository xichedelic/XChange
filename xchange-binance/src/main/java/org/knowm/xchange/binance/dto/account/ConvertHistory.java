package org.knowm.xchange.binance.dto.account;

import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.Data;

@Data
public class ConvertHistory {
  public String quoteId;
  public BigInteger orderId;
  public String orderStatus;
  public String fromAsset;
  public BigDecimal fromAmount;
  public String toAsset;
  public BigDecimal toAmount;
  public BigDecimal ratio;
  public BigDecimal inverseRatio;
  public long createTime;
}
