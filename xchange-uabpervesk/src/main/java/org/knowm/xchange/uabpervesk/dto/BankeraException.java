package org.knowm.xchange.uabpervesk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import si.mazi.rescu.HttpStatusExceptionSupport;

@Data
public class BankeraException extends HttpStatusExceptionSupport {

  private final String error;
  private final Integer status;
  private final String errorCode;
  private final String errorDescription;
  private final String errorUri;

  public BankeraException(
      @JsonProperty("status") Integer status,
      @JsonProperty("error") String error,
      @JsonProperty("error_code") String errorCode,
      @JsonProperty("error_description") String errorDescription,
      @JsonProperty("error_uri") String errorUri) {

    super(error);
    this.error = error;
    this.status = status;
    this.errorCode = errorCode;
    this.errorDescription = errorDescription;
    this.errorUri = errorUri;

  }

}
