package com.exclamationlabs.connid.base.scim2.adapter;

import com.exclamationlabs.connid.base.connector.adapter.AdapterValueTypeConverter;
import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.connector.util.GuardedStringUtil;
import com.exclamationlabs.connid.base.scim2.adapter.slack.Scim2SlackUserAdapter;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import com.exclamationlabs.connid.base.scim2.model.*;
import java.util.*;

import com.google.gson.Gson;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.Attribute;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.*;
import static com.exclamationlabs.connid.base.scim2.attribute.Scim2UserAttribute.*;
import static com.exclamationlabs.connid.base.scim2.attribute.Scim2EnterpriseUserAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_RETURNED_BY_DEFAULT;

public class Scim2UserAdapter extends BaseAdapter<Scim2User, Scim2Configuration> {
  private static final Log LOG = Log.getLog(Scim2UserAdapter.class);
  public static final String SCIM2_USER ="Scim2User";
  public static final String SCIM2_CORE_USER_SCHEMA ="urn:ietf:params:scim:schemas:core:2.0:User";
  public static final String SCIM2_ENTERPRISE_USER_SCHEMA = "urn:ietf:params:scim:schemas:extension:slack:profile:2.0:User";
  @Override
  public ObjectClass getType() {
    return new ObjectClass(SCIM2_USER);
  }

  @Override
  public Class<Scim2User> getIdentityModelClass() {
    return Scim2User.class;
  }

  /**
   * Populates the connector attributes for the core scim2 user
   * schema:urn:ietf:params:scim:schemas:core:2.0:User
   * @return Set of Connector attributes
   */
  public Set<ConnectorAttribute> getCoreConnectorAttributes(){
    Set<ConnectorAttribute> result = new HashSet<>();
    result.add(new ConnectorAttribute(Uid.NAME, id.name(), STRING, NOT_UPDATEABLE, REQUIRED));
    result.add(new ConnectorAttribute(Name.NAME, userName.name(), STRING, REQUIRED));
    result.add(new ConnectorAttribute(externalId.name(), STRING));
    result.add(new ConnectorAttribute(name_formatted.name(), STRING));
    result.add(new ConnectorAttribute(name_familyName.name(), STRING));
    result.add(new ConnectorAttribute(name_givenName.name(), STRING));
    result.add(new ConnectorAttribute(name_middleName.name(), STRING));
    result.add(new ConnectorAttribute(name_honorificPrefix.name(), STRING));
    result.add(new ConnectorAttribute(name_honorificSuffix.name(), STRING));
    result.add(new ConnectorAttribute(displayName.name(), STRING));
    result.add(new ConnectorAttribute(nickName.name(), STRING));
    result.add(new ConnectorAttribute(profileUrl.name(), STRING));
    result.add(new ConnectorAttribute(title.name(), STRING));
    result.add(new ConnectorAttribute(userType.name(), STRING));
    result.add(new ConnectorAttribute(preferredLanguage.name(), STRING));
    result.add(new ConnectorAttribute(locale.name(), STRING));
    result.add(new ConnectorAttribute(timezone.name(), STRING));
    result.add(new ConnectorAttribute(OperationalAttributes.ENABLE_NAME,active.name(), BOOLEAN));
    result.add(new ConnectorAttribute(active.name(), BOOLEAN));
    result.add(new ConnectorAttribute(password.name(), GUARDED_STRING, NOT_READABLE, NOT_RETURNED_BY_DEFAULT));
    result.add(new ConnectorAttribute(addresses.name(), STRING, MULTIVALUED));
    result.add(new ConnectorAttribute(emails.name(), STRING, MULTIVALUED, REQUIRED));
    result.add(new ConnectorAttribute(phoneNumbers.name(), STRING, MULTIVALUED));
    result.add(new ConnectorAttribute(ims.name(), STRING, MULTIVALUED));
    result.add(new ConnectorAttribute(photos.name(), STRING, MULTIVALUED));
    result.add(new ConnectorAttribute(groups.name(), STRING, MULTIVALUED, NOT_UPDATEABLE, NOT_CREATABLE));
    result.add(new ConnectorAttribute(entitlements.name(), STRING, MULTIVALUED));
    result.add(new ConnectorAttribute(roles.name(), STRING, MULTIVALUED));
    result.add(new ConnectorAttribute(x509Certificates.name(), STRING, MULTIVALUED));
    if ( configuration.getSelfRef() != null && configuration.getSelfRef() ) {
      result.add(new ConnectorAttribute(selfRef.name(), STRING, NOT_CREATABLE, NOT_UPDATEABLE));
    }
    return result;
  }

  /**
   * Populates the connector attributes for the enterprise scim2 user
   * "schema": "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User"
   * @return Set of connector attributes for the Enterprise User
   */
  public Set<ConnectorAttribute> getEnterpriseConnectorAttributes(){
    Set<ConnectorAttribute> result = new HashSet<>();
    result.add(new ConnectorAttribute(employeeNumber.name(), STRING));
    result.add(new ConnectorAttribute(costCenter.name(), STRING));
    result.add(new ConnectorAttribute(organization.name(), STRING));
    result.add(new ConnectorAttribute(division.name(), STRING));
    result.add(new ConnectorAttribute(department.name(), STRING));
    result.add(new ConnectorAttribute(manager_value.name(), STRING));
    result.add(new ConnectorAttribute(manager_displayName.name(), STRING));
    result.add(new ConnectorAttribute(manager_ref.name(), STRING));
    result.add(new ConnectorAttribute(manager_managerId.name(), STRING));
    return result;
  }

  /**
   * Converts Set of JSON strings to SCIM2 Addresses for outbound.
   * @param addresses Addresses in JSON format
   * @return List of Scim2Address
   */
  public List<Scim2Address> getAddresses(Set<String> addresses)
  {
    List<Scim2Address> list = null;
    if ( addresses != null && !addresses.isEmpty() ) {
      try {
        list = new ArrayList<>();
        Gson gson = new Gson();
        for( String jsonAddress : addresses ) {
          Scim2Address address = gson.fromJson(jsonAddress, Scim2Address.class);
          list.add(address);
        }
      }
      catch (Exception e) {
        Logger.error(this, "Converting to List of Addresses ", e);
      }
    }
    return list;
  }

  /**
   * Converts Set of JSON strings to List of SCIM2 Complex types for outbound.
   * @param complexTypes SCIM2 Complex Type in JSON format
   * @return List of Scim2ComplexType
   */
  public List<Scim2ComplexType> getComplexTypes(Set<String> complexTypes)
  {
    List<Scim2ComplexType> list = null;
    if ( complexTypes != null && !complexTypes.isEmpty() ) {
      try {
        list = new ArrayList<>();
        Gson gson = new Gson();
        for( String jsonItem : complexTypes) {
          Scim2ComplexType complexType = gson.fromJson(jsonItem, Scim2ComplexType.class);
          list.add(complexType);
        }
      }
      catch (Exception e) {
        Logger.error(this, "Converting to List of ComplexType ", e);
      }
      finally
      {
        // do not return an empty list
        if (list != null && list.isEmpty()) {
          list = null;
        }
      }
    }
    return list;
  }

  /**
   * Converts a Set of JSON formatted Strings into a list of Map
   * @param json
   * @return List of Map containing name value pairs.
   */
  public List<Map<String, String>> getListOfMapFromJSON(Set<String> json)
  {
    List<Map<String, String>> list = null;
    if ( json != null && !json.isEmpty() ) {
      list = new ArrayList<>();
      for (String item : json) {
        Map<String, String> map = new HashMap<>();
        Gson gson = new Gson();
        Map<String, Object> mapObject = gson.fromJson(item, Map.class);
        for (Map.Entry<String, Object> entry : mapObject.entrySet()) {
          map.put(entry.getKey(), entry.getValue().toString());
        }
        list.add(map);
      }
    }
    return list;
  }

  /**
   * Construct a Set of JSON formatted strings
   * from of list of maps containing name values pairs
   * @param list List of maps
   * @return Set of JSON formatted strings
   */
  public Set<String> getSetOfJSONFromListOfMap(List<Map<String, String>> list){
    Set<String> set = null;
    if ( list != null && list.size() > 0 )
    {
      String json = null;
      set = new HashSet<>();
      for(Map<String, String> item: list) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        item.forEach((key, value) -> {
          joiner.add(String.format("\"%s\":\"%s\"", key, value));
        });
        json = joiner.toString();
        set.add(json);
      }
    }
    return set;
  }
  /**
   * Convert Collection of SCIM2 Complex Type to Set of JSON Strings
   * @param complexTypes Collection of complex types to be converted
   * @return Set of String
   */
  public Set<String> putComplexTypes(Collection<Scim2ComplexType> complexTypes) {
    Set<String> result = new HashSet<>();
    if ( complexTypes == null || complexTypes.isEmpty() ) {
      return null;
    }
    Gson gson = new Gson();
    complexTypes.forEach( complexType -> {
      String json = gson.toJson(complexType);
      if ( json != null && !json.isEmpty() ) {
        result.add(json);
      }
    });
    return result;
  }

  /**
   * Convert Collection of SCIM2 Addresses to Set of JSON Strings
   * @param addresses Collection of Addresses
   * @return Set of String
   */
  public Set<String> putAddresses(Collection<Scim2Address> addresses) {
    Set<String> result = new HashSet<>();
    if ( addresses == null || addresses.isEmpty() ) {
      return null;
    }
    Gson gson = new Gson();
    addresses.forEach( address -> {
      String json = gson.toJson(address);
      if ( json != null && !json.isEmpty() ) {
        result.add(json);
      }
    });
    return result;
  }

  /**
   * Convert Collection of SCIM2 Groups to Set of JSON Strings
   * @param groups Collection of User Groups
   * @return Set of String
   */
  public Set<String> putGroups(Collection<Scim2GroupType> groups) {
    Set<String> result = new HashSet<>();
    if ( groups == null || groups.isEmpty() ) {
      return null;
    }
    Gson gson = new Gson();
    groups.forEach( group -> {
      String json = gson.toJson(group);
      if ( json != null && !json.isEmpty() ) {
        result.add(json);
      }
    });
    return result;
  }
  /**
   * Converts Set of JSON strings to List of SCIM2 Group types for outbound.
   * When the returned value is an empty List null is returned instead
   * @param groupTypes Set of SCIM2 GroupType in JSON format
   * @return List of Scim2GroupType
   */
  public List<Scim2GroupType> getGroupTypes(Set<String> groupTypes)
  {
    List<Scim2GroupType> list = null;
    if ( groupTypes != null && !groupTypes.isEmpty() ) {
      try {
        list = new ArrayList<>();
        Gson gson = new Gson();
        for( String jsonItem : groupTypes) {
          Scim2GroupType group = gson.fromJson(jsonItem, Scim2GroupType.class);
          list.add(group);
        }
      }
      catch (Exception e) {
        Logger.error(this, "Converting to List of Group ", e);
      }
      finally
      {
        // do not return an empty list
        if (list != null && list.isEmpty()) {
          list = null;
        }
      }
    }
    return list;
  }

  protected Set<Attribute> populateEnterpriseAttributes(Scim2User user)
  {
    Set<Attribute> attributes = new HashSet<>();
    if ( user != null ) {
      Scim2EnterpriseUser enterpriseUser = user.getEnterpriseInfo();
      if ( enterpriseUser != null ) {
        attributes.add(AttributeBuilder.build(costCenter.name(), enterpriseUser.getCostCenter()));
        attributes.add(AttributeBuilder.build(organization.name(), enterpriseUser.getOrganization()));
        attributes.add(AttributeBuilder.build(division.name(), enterpriseUser.getDivision()));
        attributes.add(AttributeBuilder.build(department.name(), enterpriseUser.getDepartment()));
        attributes.add(AttributeBuilder.build(employeeNumber.name(), enterpriseUser.getEmployeeNumber()));
        if ( enterpriseUser.getManager() != null)
        {
          attributes.add(AttributeBuilder.build(manager_value.name(), enterpriseUser.getManager().getValue()));
          attributes.add(AttributeBuilder.build(manager_displayName.name(), enterpriseUser.getManager().getDisplayName()));
          attributes.add(AttributeBuilder.build(manager_ref.name(), enterpriseUser.getManager().getRef()));
          attributes.add(AttributeBuilder.build(manager_managerId.name(), enterpriseUser.getManager().getManagerId()));
        }
      }
    }
    return attributes;
  }
  /**
   * populates the Scim2User with enterprise user values.
   * The enterprise user attribute is not set unless one of the values are not null
   * @param user The User object to be populated
   * @param attributes attributes
   * @param addedMultiValueAttributes multivalued attributes to add
   * @param removedMultiValueAttributes multivalued attributes to remove
   * @param isCreate is the user to be created
   * @return The updated Scim2User
   */
  protected Scim2User populateEnterpriseUser (Scim2User user,
                          Set<Attribute> attributes,
                          Set<Attribute> addedMultiValueAttributes,
                          Set<Attribute> removedMultiValueAttributes,
                          boolean isCreate){

    Scim2EnterpriseUser enterprise = new Scim2EnterpriseUser();
    Scim2ManagerType managerType = new Scim2ManagerType();
    boolean exists = false;
    boolean managerExists = false;
    String costCenterValue = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, costCenter);
    if ( costCenterValue != null )
    {
      enterprise.setCostCenter(costCenterValue);
      exists = true;
    }
    String departmentValue = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, department);
    if ( departmentValue != null )
    {
      enterprise.setDepartment(departmentValue);
      exists = true;
    }
    String empValue = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, employeeNumber);
    if ( empValue != null )
    {
      enterprise.setEmployeeNumber(empValue);
      exists = true;
    }
    String divisionValue = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, division);
    if ( divisionValue != null )
    {
      enterprise.setDivision(divisionValue);
      exists = true;
    }
    String organizationValue = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, organization);
    if ( organizationValue != null )
    {
      enterprise.setOrganization(organizationValue);
      exists = true;
    }
    // Set Manager attributes
    String managerValue = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, manager_value);
    if ( managerValue != null )
    {
      managerType.setValue(managerValue);
      managerExists = true;
    }
    String managerDisplay = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, manager_displayName);
    if ( managerDisplay != null )
    {
      managerType.setDisplayName(managerDisplay);
      managerExists = true;
    }
    String managerRef = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, manager_ref);
    if ( managerRef != null )
    {
      managerType.setRef(managerRef);
      managerExists = true;
    }
    String managerId = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, manager_managerId);
    if ( managerId != null )
    {
      managerType.setManagerId(managerId);
      managerExists = true;
    }

    if ( managerExists )
    {
      enterprise.setManager(managerType);
      exists = true;
    }
    if ( exists )
    {
      user.setEnterpriseInfo(enterprise);
      if ( user.getSchemas() == null || user.getSchemas().isEmpty() ) {
        List<String> schemaList = new ArrayList<>();
        schemaList.add(SCIM2_ENTERPRISE_USER_SCHEMA);
        user.setSchemas(schemaList);
      }
      else if (!user.getSchemas().contains(SCIM2_ENTERPRISE_USER_SCHEMA) )
      {
        user.getSchemas().add(SCIM2_ENTERPRISE_USER_SCHEMA);
      }
    }
    return user;
  }

  protected Set<Attribute> populateCoreAttributes(Scim2User user) {
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(AttributeBuilder.build(active.name(), user.getActive()));
    attributes.add(AttributeBuilder.build(OperationalAttributes.ENABLE_NAME, user.getActive()));
    attributes.add(AttributeBuilder.build(displayName.name(), user.getUserName()));
    attributes.add(AttributeBuilder.build(externalId.name(), user.getExternalId()));
    attributes.add(AttributeBuilder.build(id.name(), user.getIdentityIdValue()));
    attributes.add(AttributeBuilder.build(Uid.NAME, user.getIdentityIdValue()));
    attributes.add(AttributeBuilder.build(locale.name(), user.getLocale()));
    if ( user.getName() != null )
    {
      attributes.add(AttributeBuilder.build(name_familyName.name(), user.getName().getFamilyName()));
      attributes.add(AttributeBuilder.build(name_givenName.name(), user.getName().getGivenName()));
      attributes.add(AttributeBuilder.build(name_middleName.name(), user.getName().getMiddleName()));
      attributes.add(AttributeBuilder.build(name_formatted.name(), user.getName().getFormatted()));
      attributes.add(AttributeBuilder.build(name_honorificPrefix.name(), user.getName().getHonorificPrefix()));
      attributes.add(AttributeBuilder.build(name_honorificSuffix.name(), user.getName().getHonorificSuffix()));
    }
    attributes.add(AttributeBuilder.build(nickName.name(), user.getNickName()));
    attributes.add(AttributeBuilder.build(preferredLanguage.name(), user.getPreferredLanguage()));
    attributes.add(AttributeBuilder.build(profileUrl.name(), user.getProfileUrl()));
    attributes.add(AttributeBuilder.build(timezone.name(), user.getTimezone()));
    attributes.add(AttributeBuilder.build(title.name(), user.getTitle()));
    // attributes.add(AttributeBuilder.build(userName.name(), user.getIdentityNameValue()));
    attributes.add(AttributeBuilder.build(Name.NAME, user.getUserName()));
    attributes.add(AttributeBuilder.build(userType.name(), user.getUserType()));
    if ( configuration.getSelfRef() != null && configuration.getSelfRef() ) {
      attributes.add(AttributeBuilder.build(selfRef.name(), builderSelfRef(user)));
    }

    Set<String> addressSet = putAddresses(user.getAddresses());
    if ( addressSet != null )
    {
      attributes.add(AttributeBuilder.build(addresses.name(), addressSet));
    }

    Set<String> emailSet = putComplexTypes(user.getEmails());
    if ( emailSet != null )
    {
      attributes.add(AttributeBuilder.build(emails.name(), emailSet));
    }

    Set<String> entitlementsSet = putComplexTypes(user.getEntitlements());
    if ( entitlementsSet != null )
    {
      attributes.add(AttributeBuilder.build(entitlements.name(), entitlementsSet));
    }

    Set<String> groupSet = getSetOfJSONFromListOfMap(user.getGroups());
    if ( groupSet != null )
    {
      attributes.add(AttributeBuilder.build(groups.name(), groupSet));
    }

    Set<String> imsSet = putComplexTypes(user.getIms());
    if ( imsSet != null )
    {
      attributes.add(AttributeBuilder.build(ims.name(), imsSet));
    }

    Set<String> phoneSet = putComplexTypes(user.getPhoneNumbers());
    if ( phoneSet != null )
    {
      attributes.add(AttributeBuilder.build(phoneNumbers.name(), phoneSet));
    }

    Set<String> photoSet = putComplexTypes(user.getPhotos());
    if ( photoSet != null ) {
      attributes.add(AttributeBuilder.build(photos.name(), photoSet));
    }

    Set<String> rolesSet = putComplexTypes(user.getRoles());
    if ( rolesSet != null )
    {
      attributes.add(AttributeBuilder.build(roles.name(), rolesSet));
    }

    Set<String> x509CertificatesSet = putComplexTypes(user.getX509Certificates());
    if ( x509CertificatesSet != null )
    {
      attributes.add(AttributeBuilder.build(x509Certificates.name(), x509CertificatesSet));
    }
    return attributes;
  }

  /**
   * Generate the structured information containing the self reference (e.g. for the comparing of the objects)
   *
   * @param user
   * @return json structured Self reference
   */
  private String builderSelfRef(Scim2User user) {
    String result;
    Gson gson = new Gson();
    Map<String, String> retValue = new LinkedHashMap<>();

    if (user != null) {
      retValue.put("value", user.getId());
      retValue.put("$ref",
              configuration.getServiceUrl() +
                      Objects.toString(configuration.getUsersEndpointUrl(), "") +
                      "/" + user.getId()
      );
      retValue.put("display", Objects.toString(user.getUserName(), ""));
      result = gson.toJson(retValue);
      if (result != null) {
        if (!result.isEmpty()) {
          return result;
        }
      }
    }
    return "";
  }

  /**
   * Populates the SCIM2 Core user values from outbound attributes
   * @param user the User Model to be populated
   * @param attributes
   * @param addedMultiValueAttributes
   * @param removedMultiValueAttributes
   * @param isCreate
   * @return The user model is filled in with the appropriate values
   */
  protected Scim2User populateCoreUser( Scim2User user,
                                     Set<Attribute> attributes,
                                     Set<Attribute> addedMultiValueAttributes,
                                     Set<Attribute> removedMultiValueAttributes,
                                     boolean isCreate) {
    if ( user == null )
    {
      return null;
    }
    // Set Identity attributes
    user.setId(AdapterValueTypeConverter.getIdentityIdAttributeValue(attributes));
    if ( user.getId() == null || user.getId().isEmpty() )
    {
      user.setId(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, id));
    }
    user.setUserName(AdapterValueTypeConverter.getIdentityNameAttributeValue(attributes));
    if ( user.getUserName() == null || user.getUserName().isEmpty() )
    {
      user.setUserName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, userName));
    }
    // Set single value attributes
    Attribute enabled = AttributeUtil.find(OperationalAttributes.ENABLE_NAME, attributes);
    Boolean activeValue = null;
    if ( enabled != null )
    {
      activeValue = AttributeUtil.getBooleanValue(enabled);
    }

    if ( activeValue != null  )
    {
      user.setActive(activeValue);
    } else {
      user.setActive(AdapterValueTypeConverter.getSingleAttributeValue(Boolean.class, attributes, active));
    }

    user.setDisplayName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, displayName));
    user.setLocale(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, locale));
    user.setNickName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, nickName));

    Attribute pwd = AttributeUtil.find(OperationalAttributes.PASSWORD_NAME, attributes);
    GuardedString gsValue = null;
    if ( pwd != null )
    {
      gsValue = AttributeUtil.getGuardedStringValue(pwd);
    }
    if ( gsValue == null  )
    {
      gsValue = AdapterValueTypeConverter.getSingleAttributeValue(GuardedString.class, attributes, password);
    }
    user.setPassword(GuardedStringUtil.read(gsValue));

    user.setPreferredLanguage(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, preferredLanguage));
    user.setProfileUrl(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, profileUrl));
    user.setTimezone(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, timezone));
    user.setTitle(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, title));
    user.setUserType(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, userType));
    // Populate name
    Scim2Name name = new Scim2Name();
    boolean nameExist = false;
    String fn = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, name_familyName);
    if ( fn != null && !fn.isEmpty())
    {
      name.setFamilyName(fn);
      nameExist = true;
    }
    String gn = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, name_givenName);
    if ( gn != null && !gn.isEmpty()){
      name.setGivenName(gn);
      nameExist = true;
    }

    String mn = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, name_middleName);
    if ( mn != null && !mn.isEmpty()){
      name.setMiddleName(mn);
      nameExist = true;
    }
    String nf = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, name_formatted);
    if ( nf != null && !nf.isEmpty()){
      name.setFormatted(nf);
      nameExist = true;
    }

    String hp = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, name_honorificPrefix);
    if ( hp != null && !hp.isEmpty()){
      name.setHonorificPrefix(hp);
      nameExist = true;
    }

    String hs = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, name_honorificSuffix);
    if ( hs != null && !hs.isEmpty()){
      name.setHonorificSuffix(hs);
      nameExist = true;
    }
    if ( nameExist )
    {
      user.setName(name);
    }
    // Populate Addresses
    Set<String> addressList = readAssignments(attributes, addresses);
    user.setAddresses(getAddresses(addressList));
    addressList = readAssignments(addedMultiValueAttributes, addresses);
    user.setAddressesAdded(getListOfMapFromJSON(addressList));
    addressList = readAssignments(removedMultiValueAttributes, addresses);
    user.setAddressesRemoved(getListOfMapFromJSON(addressList));
    // Populate emails
    Set<String> emailList = readAssignments(attributes, emails);
    user.setEmails(getComplexTypes(emailList));
    emailList = readAssignments(addedMultiValueAttributes, emails);
    user.setEmailsAdded(getListOfMapFromJSON(emailList));
    emailList = readAssignments(removedMultiValueAttributes, emails);
    user.setEmailsRemoved(getListOfMapFromJSON(emailList));

    // Populate Entitlements
    Set<String> entitlementsList = readAssignments(attributes, entitlements);
    user.setEntitlements(getComplexTypes(entitlementsList));
    entitlementsList = readAssignments(addedMultiValueAttributes, entitlements);
    user.setEntitlementsAdded(getListOfMapFromJSON(entitlementsList));
    entitlementsList = readAssignments(removedMultiValueAttributes, entitlements);
    user.setEntitlementsRemoved(getListOfMapFromJSON(entitlementsList));

    // Populate instant Messages ims
    Set<String> imList = readAssignments(attributes, ims);
    user.setIms(getComplexTypes(imList));
    imList = readAssignments(addedMultiValueAttributes, ims);
    user.setImsAdded(getListOfMapFromJSON(imList));
    imList = readAssignments(removedMultiValueAttributes, ims);
    user.setImsRemoved(getListOfMapFromJSON(imList));

    // Populate Phone Numbers
    Set<String> phoneNumberList = readAssignments(attributes, phoneNumbers);
    user.setPhoneNumbers(getComplexTypes(phoneNumberList));
    phoneNumberList = readAssignments(addedMultiValueAttributes, phoneNumbers);
    user.setPhoneNumbersAdded(getListOfMapFromJSON(phoneNumberList));
    phoneNumberList = readAssignments(removedMultiValueAttributes, phoneNumbers);
    user.setPhoneNumbersRemoved(getListOfMapFromJSON(phoneNumberList));

    // Populate Photos
    Set<String> photoList = readAssignments(attributes, photos);
    user.setPhotos(getComplexTypes(photoList));
    photoList = readAssignments(addedMultiValueAttributes, photos);
    user.setPhotosAdded(getListOfMapFromJSON(photoList));
    photoList = readAssignments(removedMultiValueAttributes, photos);
    user.setPhotosRemoved(getListOfMapFromJSON(photoList));

    // Populate roles
    Set<String> roleList = readAssignments(attributes, roles);
    user.setRoles(getComplexTypes(roleList));
    roleList = readAssignments(addedMultiValueAttributes, roles);
    user.setRolesAdded(getListOfMapFromJSON(roleList));
    roleList = readAssignments(removedMultiValueAttributes, roles);
    user.setRolesRemoved(getListOfMapFromJSON(roleList));

    // Populate x509Certificates
    Set<String> x509CertificateList = readAssignments(attributes, x509Certificates);
    user.setX509Certificates(getComplexTypes(x509CertificateList));
    x509CertificateList = readAssignments(addedMultiValueAttributes, x509Certificates);
    user.setX509CertificatesAdded(getListOfMapFromJSON(x509CertificateList));
    x509CertificateList = readAssignments(removedMultiValueAttributes, x509Certificates);
    user.setX509CertificatesRemoved(getListOfMapFromJSON(x509CertificateList));

    // Populate Groups
    Set<String> groupList = readAssignments(attributes, groups);
    user.setGroups(getListOfMapFromJSON(groupList));
    groupList = readAssignments(addedMultiValueAttributes, groups);
    user.setGroupsAdded(getListOfMapFromJSON(groupList));
    groupList = readAssignments(removedMultiValueAttributes, groups);
    user.setGroupsRemoved(getListOfMapFromJSON(groupList));

    // Set Schemas
    if ( user.getSchemas() == null || user.getSchemas().isEmpty()) {
      List<String> schemaList = new ArrayList<>();
      schemaList.add(SCIM2_CORE_USER_SCHEMA);
      user.setSchemas(schemaList);
    }
    else if (!user.getSchemas().contains(SCIM2_CORE_USER_SCHEMA) ) {
      user.getSchemas().add(SCIM2_CORE_USER_SCHEMA);
    }

    return user;
  }
  @Override
  public Set<ConnectorAttribute> getConnectorAttributes() {
    // Enterprise user is a super set of User
    Boolean isSlack = getConfiguration().getEnableSlackSchema();
    Boolean isAWS = getConfiguration().getEnableAWSSchema();
    Boolean isStandard = getConfiguration().getEnableStandardSchema();
    Boolean isDynamic = getConfiguration().getEnableDynamicSchema();

    Set<ConnectorAttribute> result = null;

    if (isSlack) {
      Scim2SlackUserAdapter adapter = new Scim2SlackUserAdapter();
      adapter.setConfiguration(getConfiguration());
      adapter.setDriver(getDriver());
      result = adapter.getConnectorAttributes();
    } else {
      // call Standard Attributes method
      result = getCoreConnectorAttributes();
      // call enterprise attributes method
      result.addAll(getEnterpriseConnectorAttributes());
    }
    return result;
  }

  @Override
  protected Set<Attribute> constructAttributes(Scim2User user) {
    Set<Attribute> attributes = null;
    Scim2Configuration config = getConfiguration();

    if (config.getEnableSlackSchema())
    {
      attributes = new Scim2SlackUserAdapter().constructAttributes(user);
    } else if ( config.getEnableStandardSchema()  )
    {
      attributes = populateCoreAttributes(user);
      if ( config.getEnableEnterpriseUser() ) {
        attributes.addAll(populateEnterpriseAttributes(user));
      }
    }
    else if (config.getEnableAWSSchema() )
    {
      attributes = populateCoreAttributes(user);
      attributes.addAll(populateEnterpriseAttributes(user));
    }
    else if ( config.getEnableDynamicSchema() )
    {
      attributes = populateCoreAttributes(user);
    }
    return attributes;
  }

  @Override
  protected Scim2User constructModel(
      Set<Attribute> attributes,
      Set<Attribute> addedMultiValueAttributes,
      Set<Attribute> removedMultiValueAttributes,
      boolean isCreate) {
    // Set<ConnectorAttribute> ss = getConnectorAttributes();
    Scim2User user = new Scim2User();

    Scim2Configuration config = getConfiguration();

    if (config.getEnableStandardSchema())    {
      // Standard
      user = new Scim2User();
      populateCoreUser(user,
              attributes,
              addedMultiValueAttributes,
              removedMultiValueAttributes,
              isCreate );
      if ( config.getEnableEnterpriseUser())
      {
        populateEnterpriseUser(user,
                attributes,
                addedMultiValueAttributes,
                removedMultiValueAttributes,
                isCreate);
      }
    }
    else if (config.getEnableAWSSchema())
    {
      // AWS
      user = new Scim2User();
      populateCoreUser(user,
              attributes,
              addedMultiValueAttributes,
              removedMultiValueAttributes,
              isCreate );
      populateEnterpriseUser(user,
              attributes,
              addedMultiValueAttributes,
              removedMultiValueAttributes,
              isCreate );
    }
    else if (config.getEnableSlackSchema()) {
      Scim2SlackUserAdapter adapter = new Scim2SlackUserAdapter();
      user = adapter.constructModel(
              attributes,
              addedMultiValueAttributes,
              removedMultiValueAttributes,
              isCreate);
    } else if (config.getEnableDynamicSchema()) {
      // Handle Dynamic Schema
      user = new Scim2User();
      populateCoreUser(user,
              attributes,
              addedMultiValueAttributes,
              removedMultiValueAttributes,
              isCreate );
    }
    return user;
  }
}
