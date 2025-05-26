package com.exclamationlabs.connid.base.neo.scim2.model;

import com.exclamationlabs.connid.base.edition.neo.model.JsonDeserializable;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Scim2ValueDisplayType implements JsonDeserializable {
  private String value;
  private String display;

  @Override
  public String toString() {
    var gson = new Gson();
    return gson.toJson(this);
  }

  @Override
  public void fromString(String input) {
    var gson = new Gson();
    Scim2ValueDisplayType complexType = gson.fromJson(input, Scim2ValueDisplayType.class);
    this.value = complexType.getValue();
    this.display = complexType.getDisplay();
  }
}
