package com.exclamationlabs.connid.base.scim2.model;

import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class Scim2User implements IdentityModel {

  // private LinkedTreeMap<?, ?> data;

  private String userName;
  private List<String> schemas;

  @JsonProperty("name")
  private Scim2Name name;

  @JsonProperty("addresses")
  private List<Scim2Addresses> addresses;

  private String displayName;
  private String nickName;
  private String profileUrl;
  private String title;
  private String userType;
  private String preferredLanguage;
  private String locale;
  private String timezone;
  private boolean active;
  private String password;
  // private List<Scim2CustomType> scim2CustomType; //covers for
  // emails/phonenumbers/ims/photos/entitlements/roles/x509Certificates

  private List<Scim2Emails> emails;
  private Scim2PhoneNumbers scim2PhoneNumbers;
  private Scim2Ims scim2Ims;
  private List<Scim2Photos> photos;
  private Scim2Entitlements scim2Entitlements;
  private Scim2Roles scim2Roles;
  private Scim2X509Certificates scim2X509Certificates;

  private String id;
  private String email;

  @Override
  public String getIdentityIdValue() {
    return id;
  }

  @Override
  public String getIdentityNameValue() {
    return email;
  }
}
