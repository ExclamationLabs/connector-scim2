package com.exclamationlabs.connid.base.neo.scim2.model.response.fault;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorData {
  private String field;
  private String message;
}
