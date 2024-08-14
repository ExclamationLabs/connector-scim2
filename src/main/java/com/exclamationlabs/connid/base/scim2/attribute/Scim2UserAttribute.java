package com.exclamationlabs.connid.base.scim2.attribute;

/**
 * field names in the scim2 core user schema
 * urn:ietf:params:scim:schemas:core:2.0:User
 */

public enum Scim2UserAttribute {
  id,
  externalId,
  schemas,
  userName,
  name,
  name_formatted,
  name_familyName,
  name_givenName,
  name_middleName,
  name_honorificPrefix,
  name_honorificSuffix,
  displayName,
  nickName,
  profileUrl,
  title,
  userType,
  preferredLanguage,
  locale,
  timezone,
  active,
  password,
  emails,
  emails_value,
  emails_display,
  emails_type,
  emails_primary,
  phoneNumbers,
  phoneNumbers_value,
  phoneNumbers_display,
  phoneNumbers_type,
  phoneNumbers_primary,
  ims,
  ims_value,
  ims_display,
  ims_type,
  ims_primary,
  photos,
  photos_value,
  photos_display,
  photos_type,
  photos_primary,
  addresses,
  addresses_formatted,
  addresses_streetAddress,
  addresses_locality,
  addresses_region,
  addresses_postalCode,
  addresses_country,
  addresses_type,
  groups,
  groups_value,
  groups_$ref,
  groups_display,
  groups_type,
  entitlements,
  entitlements_value,
  entitlements_display,
  entitlements_type,
  entitlements_primary,
  roles,
  roles_value,
  roles_display,
  roles_type,
  roles_primary,
  x509Certificates,
  x509Certificates_value,
  x509Certificates_display,
  x509Certificates_type,
  x509Certificates_primary;


  Scim2UserAttribute(String s) {
  }
  Scim2UserAttribute() {
  }
}
