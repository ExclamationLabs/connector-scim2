package com.exclamationlabs.connid.base.neo.scim2.model;


import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import lombok.Data;

@Data
public class Scim2Name {
  @ModelAttribute(value="formattedName")
  public String formatted;

  @ModelAttribute
  public String familyName;

  @ModelAttribute
  public String givenName;

  @ModelAttribute
  public String middleName;

  @ModelAttribute
  public String honorificPrefix;

  @ModelAttribute
  public String honorificSuffix;
}
