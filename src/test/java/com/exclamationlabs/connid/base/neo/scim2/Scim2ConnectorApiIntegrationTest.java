package com.exclamationlabs.connid.base.neo.scim2;

import com.exclamationlabs.connid.base.connector.configuration.ConfigurationReader;
import com.exclamationlabs.connid.base.connector.test.ApiIntegrationTest;
import com.exclamationlabs.connid.base.scim2.attribute.Scim2GroupAttribute;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeDelta;
import org.identityconnectors.framework.common.objects.AttributeDeltaBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.test.common.ToListResultsHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Scim2ConnectorApiIntegrationTest
    extends ApiIntegrationTest<Scim2Configuration, Scim2Connector> {

  private static final String generatedGroupName = "Test Group";
  private static String generatedUserId;
  private static String generatedGroupId;
  private static final String existingEntUser  = "U06SRNW2AS0";
  private static final String existingGroupName = "Test Group";
  private static final String existingUserName = "gwashington";
  private static final String idGeorgeWashington = "U07H3EQPYJV";
  private static final String idThomasJefferson  = "U07GJHF2BGD";
  private static final String idJeffersonDavis = "U07DUFCB78X";
  private static final String idMarkTwain = "U07EESUURPE";
  private static final String idTestGroup      = "S07H5SNR7U5";
  private static final String idScim2Group     = "S07H28BETDL";

  static String composeComplexType(String value, String type, String display, Boolean primary){
    StringJoiner joiner = new StringJoiner(",", "{", "}");
    if ( value != null && !value.isEmpty()) {
      joiner.add(String.format("\"value\":\"%s\"", value));
    }
    if ( type != null && !type.isEmpty()) {
      joiner.add(String.format("\"type\":\"%s\"", type));
    }
    if ( display != null && !display.isEmpty()) {
      joiner.add(String.format("\"display\":\"%s\"", display));
    }
    if ( primary != null ) {
      joiner.add(String.format("\"primary\": %s", primary));
    }
    return joiner.toString();
  }


  static String composeValueDisplayType(String value, String display){
    StringJoiner joiner = new StringJoiner(",", "{", "}");
    if ( value != null && !value.isEmpty()) {
      joiner.add(String.format("\"value\":\"%s\"", value));
    }
    if ( display != null && !display.isEmpty()) {
      joiner.add(String.format("\"display\":\"%s\"", display));
    }
    return joiner.toString();
  }

  @Override
  protected Scim2Configuration getConfiguration() {
    return new Scim2Configuration("slack_scim2");
  }

  @Override
  protected Class<Scim2Connector> getConnectorClass() {
    return Scim2Connector.class;
  }

  @Override
  protected void readConfiguration(Scim2Configuration scim2Configuration) {
    ConfigurationReader.setupTestConfiguration(scim2Configuration);
  }

  @BeforeEach
  public void setup() {
    super.setup();
  }

  @Test
  @Order(50)
  // @Disabled
  void test050Test() {
    getConnectorFacade().test();
  }

  @Test
  @Order(60)
  void test060Schema() {
    Schema ss = getConnectorFacade().schema();
    assertNotNull(ss);
  }

  @Test
  @Order(100)
  void testProfileUserCreate() {
    ObjectClass objClazz = new ObjectClass("Scim2User");
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(new AttributeBuilder().setName("userName").addValue("hwashington").build());
    attributes.add(new AttributeBuilder().setName("externalId").addValue("hwashington").build());
    attributes.add(new AttributeBuilder().setName("name_familyName").addValue("Washington").build());
    attributes.add(new AttributeBuilder().setName("name_givenName").addValue("Hank").build());
    attributes.add(new AttributeBuilder().setName("name_middleName").addValue("Founder").build());
    attributes.add(new AttributeBuilder().setName("name_honorificPrefix").addValue("General").build());
    attributes.add(new AttributeBuilder().setName("name_honorificSuffix").addValue("Senior").build());
    attributes.add(new AttributeBuilder().setName("displayName").addValue("Hank Washington").build());
    attributes.add(new AttributeBuilder().setName("locale").addValue("en_US.UTF-8").build());
    attributes.add(new AttributeBuilder().setName("preferredLanguage").addValue("en").build());
    attributes.add(new AttributeBuilder().setName("profileUrl").addValue("http://www.exclamationlabs.com/").build());
    attributes.add(new AttributeBuilder().setName("title").addValue("Secretary").build());
    attributes.add(new AttributeBuilder().setName("timezone").addValue("America/New_York").build());
    attributes.add(new AttributeBuilder().setName("userType").addValue("Employee").build());

    Set<String> phones = new HashSet<>();
    phones.add(composeComplexType("954-555-1492", "work", null, true));
    phones.add(composeComplexType("954-555-2300", "mobile", null, false));
    attributes.add(new AttributeBuilder().setName("phoneNumbers").addValue(phones).build());

    Set<String> emails = new HashSet<>();
    emails.add(composeComplexType("services-dev+hwwork@provisioniam.com", "work", null, true));
    emails.add(composeComplexType("services-dev+hwhome@provisioniam.com", "home", null, null));
    attributes.add(new AttributeBuilder().setName("emails").addValue(emails).build());

    Set<String> photos = new HashSet<>();
    photos.add(composeComplexType("https://gravatar.com/avatar/abdf31385275b3157dbb610b80041a44?size=256", "photo", null, true));
    attributes.add(new AttributeBuilder().setName("photos").addValue(photos).build());

    // Slack profile field
    attributes.add(new AttributeBuilder().setName("startDate").addValue("2023-09-15T00:00:00+0000").build());

    // Enterprise fields
    attributes.add(new AttributeBuilder().setName("department").addValue("Some Dept").build());
    attributes.add(new AttributeBuilder().setName("division").addValue("Acme Division").build());
    attributes.add(new AttributeBuilder().setName("employeeNumber").addValue("H12345678").build());
    attributes.add(new AttributeBuilder().setName("organization").addValue("Organized").build());
    attributes.add(new AttributeBuilder().setName("manager").addValue(composeValueDisplayType("M123456", "Hanks Mgr")).build());

    Uid newId = getConnectorFacade().create(
            objClazz, attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    generatedUserId = newId.getUidValue();
  }

  @Test
  @Disabled // HTTP 400 invalid_request_payload even when POST matches documented examples exactly
  @Order(101)
  void testGuestUserCreate() {
    ObjectClass objClazz = new ObjectClass("Scim2User");
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(new AttributeBuilder().setName("userName").addValue("dwashington").build());
    attributes.add(new AttributeBuilder().setName("displayName").addValue("Doug Washington").build());

    Set<String> emails = new HashSet<>();
    emails.add(composeValueDisplayType("dwash@provisioniam.com", "Doug Washington"));
    attributes.add(new AttributeBuilder().setName("emails").addValue(emails).build());

    // Slack Guest fields
    attributes.add(new AttributeBuilder().setName("guestType").addValue("multi").build());
    Set<String> channels = new HashSet<>();
    channels.add(composeValueDisplayType("ABC", "American Broadcasting Channel"));
    channels.add(composeValueDisplayType("NBC", "National Broadcasting Channel"));
    attributes.add(new AttributeBuilder().setName("channels").addValue(channels).build());

    Uid newId = getConnectorFacade().create(
            objClazz, attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    generatedUserId = newId.getUidValue();
  }

  @Test
  @Order(102)
  void testProfileUserWithGroupsCreate() {
    ObjectClass objClazz = new ObjectClass("Scim2User");
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(new AttributeBuilder().setName("userName").addValue("jwashington").build());
    attributes.add(new AttributeBuilder().setName("externalId").addValue("jwashington").build());
    attributes.add(new AttributeBuilder().setName("name_familyName").addValue("Washington").build());
    attributes.add(new AttributeBuilder().setName("name_givenName").addValue("Jack").build());
    attributes.add(new AttributeBuilder().setName("name_middleName").addValue("Founder").build());
    attributes.add(new AttributeBuilder().setName("name_honorificPrefix").addValue("General").build());
    attributes.add(new AttributeBuilder().setName("name_honorificSuffix").addValue("Senior").build());
    attributes.add(new AttributeBuilder().setName("displayName").addValue("Jack Washington").build());
    attributes.add(new AttributeBuilder().setName("locale").addValue("en_US.UTF-8").build());
    attributes.add(new AttributeBuilder().setName("preferredLanguage").addValue("en").build());
    attributes.add(new AttributeBuilder().setName("profileUrl").addValue("http://www.exclamationlabs.com/").build());
    attributes.add(new AttributeBuilder().setName("title").addValue("Secretary").build());
    attributes.add(new AttributeBuilder().setName("timezone").addValue("America/New_York").build());
    attributes.add(new AttributeBuilder().setName("userType").addValue("Employee").build());

    Set<String> phones = new HashSet<>();
    phones.add(composeComplexType("954-555-1492", "work", null, true));
    phones.add(composeComplexType("954-555-2300", "mobile", null, false));
    attributes.add(new AttributeBuilder().setName("phoneNumbers").addValue(phones).build());

    Set<String> emails = new HashSet<>();
    emails.add(composeComplexType("services-dev+jwwork@provisioniam.com", "work", null, true));
    emails.add(composeComplexType("services-dev+jwhome@provisioniam.com", "home", null, null));
    attributes.add(new AttributeBuilder().setName("emails").addValue(emails).build());

    Set<String> groups = new HashSet<>();
    groups.add(composeValueDisplayType(idScim2Group, "SCIM2" ));
    groups.add(composeValueDisplayType(idTestGroup, "Test Group" ));
    attributes.add(new AttributeBuilder().setName("groups").addValue(groups).build());

    // Slack profile field
    attributes.add(new AttributeBuilder().setName("startDate").addValue("2023-09-15T00:00:00+0000").build());

    // Enterprise fields
    attributes.add(new AttributeBuilder().setName("department").addValue("Some Dept").build());
    attributes.add(new AttributeBuilder().setName("division").addValue("Acme Division").build());
    attributes.add(new AttributeBuilder().setName("employeeNumber").addValue("H12345678").build());
    attributes.add(new AttributeBuilder().setName("organization").addValue("Organized").build());

    Uid newId = getConnectorFacade().create(
            objClazz, attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    generatedUserId = newId.getUidValue();
  }

  @Test
  @Order(105)
  void testUserUpdateGuest()
  {
    ToListResultsHandler listHandler = new ToListResultsHandler();
    ObjectClass objClazz = new ObjectClass("Scim2User");
    OperationOptions options = new OperationOptionsBuilder().build();
    Uid uid = new Uid(idMarkTwain);
    Set<AttributeDelta> delta = new HashSet<>();
    delta.add(new AttributeDeltaBuilder().setName("userName").addValueToReplace("mtwain").build());
    //delta.add(new AttributeDeltaBuilder().setName("guest_type").addValueToReplace("multi").build());
    Set<String> emails = new HashSet<>();
    emails.add(composeComplexType("services-dev+mtwain@provisioniam.com", "work", null, true));
    delta.add(new AttributeDeltaBuilder().setName("emails").addValueToReplace(emails).build());
    Set<String> groups = new HashSet<>();
    Set<AttributeDelta> output = getConnectorFacade().updateDelta(objClazz, uid, delta, options);
    assertNotNull(output);
  }

  @Test
  @Order(110)
  void testUserUpdate()
  {
    ObjectClass objClazz = new ObjectClass("Scim2User");
    OperationOptions options = new OperationOptionsBuilder().build();
    Uid uid = new Uid(idThomasJefferson);
    Set<AttributeDelta> delta = new HashSet<>();
    delta.add(new AttributeDeltaBuilder().setName("displayName").addValueToReplace("t-jeff").build());
//    delta.add(new AttributeDeltaBuilder().setName("userName").addValueToReplace("tjefferson").build());
//    delta.add(new AttributeDeltaBuilder().setName("honorificPrefix").addValueToReplace("Heneral").build());

//    Set<String> emails = new HashSet<>();
//    emails.add(composeComplexType("services-dev+tjwork@provisioniam.com", "work", null, true));
//    delta.add(new AttributeDeltaBuilder().setName("emails").addValueToReplace(emails).build());
//    Set<String> phones = new HashSet<>();
//    phones.add(composeComplexType("954-555-1776", "work", null, true));
//    phones.add(composeComplexType("954-555-1800", "mobile", null, false));
//    delta.add(new AttributeDeltaBuilder().setName("phoneNumbers").addValueToReplace(phones).build());
    // Groups are Read Only
//    Set<String> groups = new HashSet<>();
//    groups.add(composeComplexType("S07H5SNR7U5", null, "Test Group", null));
//    delta.add(new AttributeDeltaBuilder().setName("groups").addValueToAdd(groups).build());

//    delta.add(new AttributeDeltaBuilder().setName(OperationalAttributes.ENABLE_NAME).addValueToReplace(true).build());

    delta.add(new AttributeDeltaBuilder().setName("employeeNumber").addValueToReplace("61234").build());
    delta.add(new AttributeDeltaBuilder().setName("costCenter").addValueToReplace("Research2").build());
    delta.add(new AttributeDeltaBuilder().setName("organization").addValueToReplace("ProvisionIAM2").build());
    delta.add(new AttributeDeltaBuilder().setName("division").addValueToReplace("Services2").build());
    delta.add(new AttributeDeltaBuilder().setName("department").addValueToReplace("Development2").build());
    Set<AttributeDelta> output = getConnectorFacade().updateDelta(objClazz, uid, delta, options);
    assertNotNull(output);
  }

  @Test
  @Order(120)
  void testUserGet() {
    var idTest = "U08TR5B0014"; // U08TVLK9MQQ; // idThomasJefferson
    ObjectClass objClazz = new ObjectClass("Scim2User");
    Attribute id =
        new AttributeBuilder().setName(Uid.NAME).addValue(idTest).build();

    getConnectorFacade()
        .search(
            objClazz,
            new EqualsFilter(id),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(1, results.size());
    String userId = results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString();
    assertTrue(StringUtils.isNotBlank(userId));
    assertEquals(idTest, userId);
  }

  @Test
  @Order(122)
  void testUserGet_unknownId() {
    ObjectClass objClazz = new ObjectClass("Scim2User");
    Attribute id =
            new AttributeBuilder().setName(Uid.NAME).addValue("unknown_id").build();

    getConnectorFacade()
            .search(
                    objClazz,
                    new EqualsFilter(id),
                    handler,
                    new OperationOptionsBuilder().build());
    assertTrue(results.isEmpty());
  }

  @Test
  @Order(125)
  void testUserGetByName() {
    results = new ArrayList<>();
    ObjectClass objClazz = new ObjectClass("Scim2User");
    Attribute name =
            new AttributeBuilder().setName(Name.NAME).addValue(existingUserName).build();

    getConnectorFacade().search(
                    objClazz,
                    new EqualsFilter(name),
                    handler,
                    new OperationOptionsBuilder().build());
    assertEquals(1, results.size());
    String userName = results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString();
    assertEquals(existingUserName, userName);
  }

  @Test
  @Order(130)
  void testUserGetEnterprise() {
    ToListResultsHandler listHandler = new ToListResultsHandler();
    ObjectClass objClazz = new ObjectClass("Scim2User");
    Attribute id =
            new AttributeBuilder().setName(Uid.NAME).addValue(existingEntUser).build();

    getConnectorFacade().search(
            objClazz,
            new EqualsFilter(id),
            listHandler,
            new OperationOptionsBuilder().build());
    List<ConnectorObject> users = listHandler.getObjects();
    assertEquals(1, users.size());
    String userId = users.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString();
    assertEquals(existingEntUser, userId);
  }

  @Test
  @Order(150)
  void testUserList() {
    results = new ArrayList<>();
    ObjectClass objClazz = new ObjectClass("Scim2User");
    getConnectorFacade()
        .search(
            objClazz,null,
            handler,
            new OperationOptionsBuilder().build());
    assertFalse(results.isEmpty());
    assertTrue(results.size() > 1);
  }

  @Test
  @Order(200)
  void testGroupList() {
    results = new ArrayList<>();
    ObjectClass objClazz = new ObjectClass("Scim2Group");
    OperationOptions options = new OperationOptionsBuilder().build();
    getConnectorFacade().search(objClazz,null, handler, options);
    assertFalse(results.isEmpty());
  }

  @Test
  @Order(202)
  void testGroupGetOneById() {
    results = new ArrayList<>();
    ObjectClass objClazz = new ObjectClass("Scim2Group");
    Attribute id =
            new AttributeBuilder().setName(Uid.NAME).addValue(idScim2Group).build();
    getConnectorFacade()
            .search(
                    objClazz,
                    new EqualsFilter(id),
                    handler,
                    new OperationOptionsBuilder().build());
    assertEquals(1, results.size());
    String groupId = results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString();
    assertTrue(StringUtils.isNotBlank(groupId));
    assertEquals(idScim2Group, groupId);
  }

  @Test
  @Order(210)
  void testGroupCreate() {
    Set<Attribute> attributes = new HashSet<>();
    ObjectClass objGroup = new ObjectClass("Scim2Group");
    OperationOptions options = new OperationOptionsBuilder().build();
    attributes.add(new AttributeBuilder().setName("displayName").addValue("ProvisionTestSAT").build());
    Set<String> members = new HashSet<>();
    members.add(composeComplexType(idGeorgeWashington, "User", null, null));
    members.add(composeComplexType(idJeffersonDavis, "User", null, null));
    attributes.add(new AttributeBuilder().setName("members").addValue(members).build());
    generatedGroupId = getConnectorFacade().create(objGroup, attributes, options).getUidValue();
    assertNotNull(generatedGroupId);
  }

  @Test
  @Order(220)
  void testGroupGetByName()
  {
    results = new ArrayList<>();
    ObjectClass objGroup = new ObjectClass("Scim2Group");
    OperationOptions options = new OperationOptionsBuilder().build();
    Attribute name =
            new AttributeBuilder().setName(Name.NAME).addValue(existingGroupName).build();
    getConnectorFacade().search(objGroup,new EqualsFilter(name), handler, options);
    assertEquals(1, results.size());
    String groupName = results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString();
  }

  @Test
  @Order(230)
  void testGroupUpdateName()
  {
    ObjectClass objGroup = new ObjectClass("Scim2Group");
    OperationOptions options = new OperationOptionsBuilder().build();
    Uid uid = new Uid(idScim2Group);
    Set<AttributeDelta> delta = new HashSet<>();
    delta.add(new AttributeDeltaBuilder().setName(Name.NAME).addValueToReplace("SCIM2").build());
    Set<AttributeDelta> output = getConnectorFacade().updateDelta(objGroup, uid, delta, options);
    assertNotNull(output);
  }

  @Test
  @Order(230)
  void testGroupUpdateMembers()
  {
    ObjectClass objGroup = new ObjectClass("Scim2Group");
    OperationOptions options = new OperationOptionsBuilder().build();
    Uid uid = new Uid(idScim2Group);
    Set<AttributeDelta> delta = new HashSet<>();
    Set<String> added = new HashSet<>();
    added.add(composeComplexType(idThomasJefferson, "User", null, null));
    added.add(composeComplexType(idJeffersonDavis, "User", null, null));
    AttributeDeltaBuilder builder;
    builder = new AttributeDeltaBuilder().setName(Scim2GroupAttribute.members.name()).addValueToAdd(added);
    delta.add(builder.build());
    Set<String> removed = new HashSet<>();
    removed.add(composeComplexType(idGeorgeWashington, "User", null, null));
    builder = new AttributeDeltaBuilder().setName(Scim2GroupAttribute.members.name()).addValueToRemove(removed);
    delta.add(builder.build());
    Set<AttributeDelta> output = getConnectorFacade().updateDelta(objGroup, uid, delta, options);
    assertNotNull(output);
  }

  @Test
  //@Disabled
  @Order(310)
  void testUserDelete() {
    ObjectClass objClazz = new ObjectClass("Scim2User");
    getConnectorFacade().delete(objClazz, new Uid("U08TVLK9MQQ"), new OperationOptionsBuilder().build());
  }
  @Test
  //@Disabled
  @Order(320)
  void testGroupDelete() {
    ObjectClass objClazz = new ObjectClass("Scim2Group");
    getConnectorFacade().delete(objClazz, new Uid("S08TGGX1YBH"), new OperationOptionsBuilder().build());
  }
}
