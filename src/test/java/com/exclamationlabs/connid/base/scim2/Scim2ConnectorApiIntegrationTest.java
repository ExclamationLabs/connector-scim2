package com.exclamationlabs.connid.base.scim2;

import static org.junit.jupiter.api.Assertions.*;

import com.exclamationlabs.connid.base.connector.configuration.ConfigurationReader;
import com.exclamationlabs.connid.base.connector.test.ApiIntegrationTest;
import com.exclamationlabs.connid.base.scim2.attribute.Scim2GroupAttribute;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.test.common.ToListResultsHandler;
import org.junit.jupiter.api.*;

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
    if ( value != null && value.length() > 0 ) {
      joiner.add(String.format("\"value\":\"%s\"", value));
    }
    if ( type != null && type.length() > 0 ) {
      joiner.add(String.format("\"type\":\"%s\"", type));
    }
    if ( display != null && display.length() > 0 ) {
      joiner.add(String.format("\"display\":\"%s\"", display));
    }
    if ( primary != null ) {
      joiner.add(String.format("\"primary\": %s", primary));
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
  public void test050Test() {
    getConnectorFacade().test();
  }

  @Test
  @Order(60)
  public void test060Schema() {
    Schema ss = getConnectorFacade().schema();
    assertNotNull(ss);
  }

  @Test
  @Order(100)
  public void testUserCreate() {
    // Creates a user that will be deleted at the end
    ObjectClass objClazz = new ObjectClass("Scim2User");
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(new AttributeBuilder().setName("userName").addValue("gwashington").build());
    attributes.add(new AttributeBuilder().setName("externalId").addValue("gwashington").build());
    attributes.add(new AttributeBuilder().setName("name_familyName").addValue("Washington").build());
    attributes.add(new AttributeBuilder().setName("name_givenName").addValue("George").build());
    attributes.add(new AttributeBuilder().setName("name_middleName").addValue("Founder").build());
    attributes.add(new AttributeBuilder().setName("name_honorificPrefix").addValue("General").build());
    attributes.add(new AttributeBuilder().setName("name_honorificSuffix").addValue("Senior").build());
    attributes.add(new AttributeBuilder().setName("displayName").addValue("George Washington").build());
    attributes.add(new AttributeBuilder().setName("locale").addValue("en_US.UTF-8").build());
    // attributes.add(new AttributeBuilder().setName("nickName").addValue("Tommy").build());
    attributes.add(new AttributeBuilder().setName("preferredLanguage").addValue("en").build());
    attributes.add(new AttributeBuilder().setName("profileUrl").addValue("http://www.exclamationlabs.com/").build());
    attributes.add(new AttributeBuilder().setName("title").addValue("President").build());
    attributes.add(new AttributeBuilder().setName("timezone").addValue("America/New_York").build());
    attributes.add(new AttributeBuilder().setName("userType").addValue("Employee").build());

    Set<String> phones = new HashSet<>();
    phones.add(composeComplexType("954-555-1776", "work", null, true));
    phones.add(composeComplexType("954-555-1800", "mobile", null, false));
    attributes.add(new AttributeBuilder().setName("phoneNumbers").addValue(phones).build());

    Set<String> emails = new HashSet<>();
    emails.add(composeComplexType("services-dev+gwwork@provisioniam.com", "work", null, true));
    emails.add(composeComplexType("services-dev+gwhome@provisioniam.com", "home", null, null));
    attributes.add(new AttributeBuilder().setName("emails").addValue(emails).build());

    Set<String> photos = new HashSet<>();
    photos.add(composeComplexType("https://gravatar.com/avatar/abdf31385275b3157dbb610b80041a44?size=256", "photo", null, true));
    attributes.add(new AttributeBuilder().setName("photos").addValue(photos).build());


    Uid newId = getConnectorFacade().create(
            objClazz, attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    generatedUserId = newId.getUidValue();
  }

  @Test
  @Order(105)
  public void testUserUpdateGuest()
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
  public void testUserUpdate()
  {
    ToListResultsHandler listHandler = new ToListResultsHandler();
    ObjectClass objClazz = new ObjectClass("Scim2User");
    OperationOptions options = new OperationOptionsBuilder().build();
    Uid uid = new Uid(idThomasJefferson);
    Set<AttributeDelta> delta = new HashSet<>();
    delta.add(new AttributeDeltaBuilder().setName("userName").addValueToReplace("tjefferson").build());
    delta.add(new AttributeDeltaBuilder().setName("name_honorificPrefix").addValueToReplace("General").build());
    Set<String> emails = new HashSet<>();
    emails.add(composeComplexType("services-dev+tjwork@provisioniam.com", "work", null, true));
    // emails.add(composeComplexType("services-dev+tjhome@provisioniam.com", "home", null, null));
    delta.add(new AttributeDeltaBuilder().setName("emails").addValueToReplace(emails).build());
    Set<String> phones = new HashSet<>();
    phones.add(composeComplexType("954-555-1776", "work", null, true));
    phones.add(composeComplexType("954-555-1800", "mobile", null, false));
    delta.add(new AttributeDeltaBuilder().setName("phoneNumbers").addValueToReplace(phones).build());
    // Groups are Read Only
    Set<String> groups = new HashSet<>();
    groups.add(composeComplexType("S07H5SNR7U5", null, "Test Group", null));
    delta.add(new AttributeDeltaBuilder().setName("groups").addValueToAdd(groups).build());

    delta.add(new AttributeDeltaBuilder().setName(OperationalAttributes.ENABLE_NAME).addValueToReplace(true).build());
    delta.add(new AttributeDeltaBuilder().setName("employeeNumber").addValueToReplace("51234").build());
    delta.add(new AttributeDeltaBuilder().setName("costCenter").addValueToReplace("Research").build());
    delta.add(new AttributeDeltaBuilder().setName("organization").addValueToReplace("ProvisionIAM").build());
    delta.add(new AttributeDeltaBuilder().setName("division").addValueToReplace("Services").build());
    delta.add(new AttributeDeltaBuilder().setName("department").addValueToReplace("Development").build());
    Set<AttributeDelta> output = getConnectorFacade().updateDelta(objClazz, uid, delta, options);
    assertNotNull(output);
  }

  @Test
  @Order(120)
  public void testUserGet() {
    ToListResultsHandler listHandler = new ToListResultsHandler();
    ObjectClass objClazz = new ObjectClass("Scim2User");
    Attribute id =
        new AttributeBuilder().setName(Uid.NAME).addValue(idThomasJefferson).build();

    getConnectorFacade()
        .search(
            objClazz,
            new EqualsFilter(id),
            listHandler,
            new OperationOptionsBuilder().build());
    List<ConnectorObject> users = listHandler.getObjects();
    assertEquals(1, users.size());
    String userId = users.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString();
    assertTrue(StringUtils.isNotBlank(userId));
    assertEquals(idThomasJefferson, userId);
  }

  @Test
  @Order(125)
  public void testUserGetByName() {
    ToListResultsHandler listHandler = new ToListResultsHandler();
    ObjectClass objClazz = new ObjectClass("Scim2User");
    Attribute name =
            new AttributeBuilder().setName(Name.NAME).addValue(existingUserName).build();

    getConnectorFacade().search(
                    objClazz,
                    new EqualsFilter(name),
                    listHandler,
                    new OperationOptionsBuilder().build());
    List<ConnectorObject> users = listHandler.getObjects();
    assertEquals(1, users.size());
    String userName = users.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString();
    assertEquals(existingUserName, userName);
  }

  @Test
  @Order(130)
  public void testUserGetEnterprise() {
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
  public void testUserList() {
    ToListResultsHandler listHandler = new ToListResultsHandler();
    ObjectClass objClazz = new ObjectClass("Scim2User");
    getConnectorFacade()
        .search(
            objClazz,null,
            listHandler,
            new OperationOptionsBuilder().build());
    List<ConnectorObject> users = listHandler.getObjects();
    assertNotNull(users);
    assertTrue(users.size() > 0);
  }

  @Test
  @Order(200)
  public void testGroupList() {
    ToListResultsHandler listHandler = new ToListResultsHandler();
    ObjectClass objClazz = new ObjectClass("Scim2Group");
    OperationOptions options = new OperationOptionsBuilder().build();
    getConnectorFacade().search(objClazz,null,listHandler, options);
    List<ConnectorObject> groups = listHandler.getObjects();
    assertNotNull(groups);
  }

  @Test
  @Order(210)
  public void testGroupCreate() {
    Set<Attribute> attributes = new HashSet<>();
    ObjectClass objGroup = new ObjectClass("Scim2Group");
    OperationOptions options = new OperationOptionsBuilder().build();
    attributes.add(new AttributeBuilder().setName("displayName").addValue("ProvisionTest").build());
    Set<String> members = new HashSet<>();
    members.add(composeComplexType(idGeorgeWashington, "User", null, null));
    members.add(composeComplexType(idJeffersonDavis, "User", null, null));
    attributes.add(new AttributeBuilder().setName("members").addValue(members).build());
    generatedGroupId = getConnectorFacade().create(objGroup, attributes, options).getUidValue();
    assertNotNull(generatedGroupId);
  }

  @Test
  @Order(220)
  public void testGroupGetByName()
  {
    ObjectClass objGroup = new ObjectClass("Scim2Group");
    OperationOptions options = new OperationOptionsBuilder().build();
    ToListResultsHandler listHandler = new ToListResultsHandler();
    Attribute name =
            new AttributeBuilder().setName(Name.NAME).addValue(existingGroupName).build();
    getConnectorFacade().search(objGroup,new EqualsFilter(name),listHandler, options);
    List<ConnectorObject> groups = listHandler.getObjects();
    assertNotNull(groups);
    assertEquals(1, groups.size());
    String groupName = groups.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString();
  }

  @Test
  @Order(230)
  public void testGroupUpdateName()
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
  public void testGroupUpdateMembers()
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
  @Disabled
  @Order(310)
  public void testUserDelete() {
    ObjectClass objClazz = new ObjectClass("Scim2User");
    getConnectorFacade().delete(objClazz, new Uid("U07GJHF2BGD"), new OperationOptionsBuilder().build());
  }
  @Test
  @Disabled
  @Order(320)
  public void testGroupDelete() {
    ObjectClass objClazz = new ObjectClass("Scim2Group");
    getConnectorFacade().delete(objClazz, new Uid("U07GJHF2BGD"), new OperationOptionsBuilder().build());
  }
}
