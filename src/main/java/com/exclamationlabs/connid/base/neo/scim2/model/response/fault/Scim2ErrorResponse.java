package com.exclamationlabs.connid.base.neo.scim2.model.response.fault;

import lombok.Data;

import java.util.List;

@Data
public class Scim2ErrorResponse
{
  private String detail;
  private List<String> schemas;
  private String scimType;
  private Integer status;

}
