package com.exclamationlabs.connid.base.neo.scim2.model;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Scim2ComplexType {
  private String value;
  private String display;
  private String type;
  private boolean primary;

  @Override
  public String toString() {
    var gson = new Gson();
    return gson.toJson(this);
  }
}
