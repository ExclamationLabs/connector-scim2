package com.exclamationlabs.connid.base.neo.scim2.model;

import com.exclamationlabs.connid.base.edition.neo.model.JsonDeserializable;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Scim2ComplexType implements JsonDeserializable {
  private String value;
  private String display;
  private String type;
  private boolean primary;

  @Override
  public String toString() {
    var gson = new Gson();
    return gson.toJson(this);
  }

  @Override
  public void fromString(String input) {
    var gson = new Gson();
    Scim2ComplexType complexType = gson.fromJson(input, Scim2ComplexType.class);
    this.value = complexType.getValue();
    this.display = complexType.getDisplay();
    this.type = complexType.getType();
    this.primary = complexType.isPrimary();
  }
}
