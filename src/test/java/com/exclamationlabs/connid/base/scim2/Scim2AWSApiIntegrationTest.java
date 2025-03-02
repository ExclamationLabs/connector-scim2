package com.exclamationlabs.connid.base.scim2;

import com.exclamationlabs.connid.base.connector.configuration.ConfigurationReader;
import com.exclamationlabs.connid.base.connector.test.ApiIntegrationTest;
import com.exclamationlabs.connid.base.scim2.attribute.Scim2GroupAttribute;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.test.common.ToListResultsHandler;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Scim2AWSApiIntegrationTest
    extends ApiIntegrationTest<Scim2Configuration, Scim2Connector> {

  private static final String generatedGroupName = "Test Group";
  private static String generatedUserId;
  private static String generatedGroupId;
  private static final String existingGroupName = "ProvisionTest";
  private static final String existingGroupId = "1488b4d8-0001-7035-d278-04af66e2a3a8";
  private static final String existingUserName = "gwashington";
  private static final String idGeorgeWashington = "94184408-40a1-706d-eff0-769a058c58a0";
  private static final String idThomasJefferson  = "U07GJHF2BGD";
  private static final String idJeffersonDavis = "U07DUFCB78X";
  private static final String idMarkTwain = "245874b8-a0a1-700d-125b-dfae8c555940";
  private static final String idTestGroup      = "S07H5SNR7U5";
  private static final String idScim2Group     = "1488b4d8-0001-7035-d278-04af66e2a3a8";

  protected String composeComplexType(String value, String type, String display, Boolean primary){
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
    return new Scim2Configuration("aws");
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
    attributes.add(new AttributeBuilder().setName("userName").addValue("mtwain").build());
    attributes.add(new AttributeBuilder().setName("externalId").addValue("mtwain").build());
    attributes.add(new AttributeBuilder().setName("name_familyName").addValue("Twain").build());
    attributes.add(new AttributeBuilder().setName("name_givenName").addValue("Mark").build());
    attributes.add(new AttributeBuilder().setName("displayName").addValue("George Washington").build());
    attributes.add(new AttributeBuilder().setName("locale").addValue("en_US.UTF-8").build());
    attributes.add(new AttributeBuilder().setName("preferredLanguage").addValue("en").build());
    attributes.add(new AttributeBuilder().setName("timezone").addValue("America/New_York").build());
    attributes.add(new AttributeBuilder().setName("userType").addValue("Guest").build());

    Set<String> phones = new HashSet<>();
    phones.add(composeComplexType("555-456-1256", "work", null, true));
    attributes.add(new AttributeBuilder().setName("phoneNumbers").addValue(phones).build());

    Set<String> emails = new HashSet<>();
    emails.add(composeComplexType("services-dev+mtwain@provisioniam.com", "work", null, true));
    attributes.add(new AttributeBuilder().setName("emails").addValue(emails).build());

    Uid newId = getConnectorFacade().create(
            objClazz, attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    generatedUserId = newId.getUidValue();
  }
  @Test
  @Order(101)
  public void testUserCreateWashington() {

    ObjectClass objClazz = new ObjectClass("Scim2User");
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(new AttributeBuilder().setName("userName").addValue("gwashington").build());
    attributes.add(new AttributeBuilder().setName("externalId").addValue("gwashington").build());
    attributes.add(new AttributeBuilder().setName("name_familyName").addValue("Washington").build());
    attributes.add(new AttributeBuilder().setName("name_givenName").addValue("George").build());
    attributes.add(new AttributeBuilder().setName("name_middleName").addValue("Founder").build());
    attributes.add(new AttributeBuilder().setName("name_honorificPrefix").addValue("General").build());
    attributes.add(new AttributeBuilder().setName("displayName").addValue("George Washington").build());
    attributes.add(new AttributeBuilder().setName("locale").addValue("en_US.UTF-8").build());
    attributes.add(new AttributeBuilder().setName("nickName").addValue("GeorgieW").build());
    attributes.add(new AttributeBuilder().setName("preferredLanguage").addValue("en").build());
    attributes.add(new AttributeBuilder().setName("profileUrl").addValue("http://www.exclamationlabs.com/").build());
    attributes.add(new AttributeBuilder().setName("title").addValue("President").build());
    attributes.add(new AttributeBuilder().setName("timezone").addValue("America/New_York").build());
    attributes.add(new AttributeBuilder().setName("userType").addValue("Employee").build());

    Set<String> phones = new HashSet<>();
    phones.add(composeComplexType("555-659-8850", "work", null, true));
    attributes.add(new AttributeBuilder().setName("phoneNumbers").addValue(phones).build());

    Set<String> emails = new HashSet<>();
    emails.add(composeComplexType("services-dev+gwwork@provisioniam.com", "work", null, true));
    attributes.add(new AttributeBuilder().setName("emails").addValue(emails).build());

    Uid newId = getConnectorFacade().create(
            objClazz, attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    generatedUserId = newId.getUidValue();
    System.out.println(String.format("UID= %s", generatedUserId));
  }

  @Test
  @Order(105)
  public void testUserUpdate()
  {
    ToListResultsHandler listHandler = new ToListResultsHandler();
    ObjectClass objClazz = new ObjectClass("Scim2User");
    OperationOptions options = new OperationOptionsBuilder().build();
    Uid uid = new Uid(idMarkTwain);
    Set<AttributeDelta> delta = new HashSet<>();
    //delta.add(new AttributeDeltaBuilder().setName("userName").addValueToReplace("mtwain").build());
    //delta.add(new AttributeDeltaBuilder().setName("displayName").addValueToReplace("Mark Twain").build());
    //delta.add(new AttributeDeltaBuilder().setName("name_familyName").addValueToReplace("Twain").build());
    //delta.add(new AttributeDeltaBuilder().setName("name_givenName").addValueToReplace("Mark").build());
    delta.add(new AttributeDeltaBuilder().setName("active").addValueToReplace(true).build());
    Set<String> emails = new HashSet<>();
    emails.add(composeComplexType("services-dev+mtwain@provisioniam.com", "work", null, true));
    // delta.add(new AttributeDeltaBuilder().setName("emails").addValueToReplace(emails).build());
    //Set<String> groups = new HashSet<>();
    //groups.add(composeComplexType(idScim2Group, null, "ProvisionTest", null));
    //delta.add(new AttributeDeltaBuilder().setName("groups").addValueToAdd(groups).build());
    Set<AttributeDelta> output = getConnectorFacade().updateDelta(objClazz, uid, delta, options);
    assertNotNull(output);
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
  @Order(151)
  public void testUserGetById() {
    ToListResultsHandler listHandler = new ToListResultsHandler();
    ObjectClass objClazz = new ObjectClass("Scim2User");
    Attribute id =
            new AttributeBuilder().setName(Uid.NAME).addValue(idGeorgeWashington).build();

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
    assertEquals(idGeorgeWashington, userId);
  }

  @Test
  @Order(152)
  public void testUserGetByName() {
    ToListResultsHandler listHandler = new ToListResultsHandler();
    ObjectClass objClazz = new ObjectClass("Scim2User");
    Attribute id =
            new AttributeBuilder().setName(Name.NAME).addValue(existingUserName).build();

    getConnectorFacade()
            .search(
                    objClazz,
                    new EqualsFilter(id),
                    listHandler,
                    new OperationOptionsBuilder().build());
    List<ConnectorObject> users = listHandler.getObjects();
    assertEquals(1, users.size());
    String userId = users.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString();
    assertTrue(StringUtils.isNotBlank(userId));
    assertEquals(existingUserName, userId);
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
    members.add(composeComplexType(idMarkTwain, "User", null, null));
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
  @Order(221)
  public void testGroupGetById()
  {
    ObjectClass objGroup = new ObjectClass("Scim2Group");
    OperationOptions options = new OperationOptionsBuilder().build();
    ToListResultsHandler listHandler = new ToListResultsHandler();
    Attribute name =
            new AttributeBuilder().setName(Uid.NAME).addValue(existingGroupId).build();
    getConnectorFacade().search(objGroup,new EqualsFilter(name),listHandler, options);
    List<ConnectorObject> groups = listHandler.getObjects();
    assertNotNull(groups);
    assertEquals(1, groups.size());
    String groupName = groups.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString();
  }

  @Test
  @Order(230)
  public void testGroupUpdateName()
  {
    ObjectClass objGroup = new ObjectClass("Scim2Group");
    OperationOptions options = new OperationOptionsBuilder().build();
    Uid uid = new Uid(idScim2Group);
    Set<AttributeDelta> delta = new HashSet<>();
    delta.add(new AttributeDeltaBuilder().setName(Name.NAME).addValueToReplace("ProvisionTest").build());
    Set<AttributeDelta> output = getConnectorFacade().updateDelta(objGroup, uid, delta, options);
    assertNotNull(output);
  }
  @Test
  @Order(231)
  public void testGroupUpdateMembers()
  {
    ObjectClass objGroup = new ObjectClass("Scim2Group");
    OperationOptions options = new OperationOptionsBuilder().build();
    Uid uid = new Uid(idScim2Group);
    Set<AttributeDelta> delta = new HashSet<>();
    Set<String> added = new HashSet<>();
    added.add(composeComplexType(idGeorgeWashington, null, null, null));
    added.add(composeComplexType(idMarkTwain,  null, null, null));
    AttributeDeltaBuilder builder;
    builder = new AttributeDeltaBuilder().setName(Scim2GroupAttribute.members.name()).addValueToAdd(added);
    delta.add(builder.build());
    Set<String> removed = new HashSet<>();
    //removed.add(composeComplexType(idGeorgeWashington, "User", null, null));
    //builder = new AttributeDeltaBuilder().setName(Scim2GroupAttribute.members.name()).addValueToRemove(removed);
    //delta.add(builder.build());
    Set<AttributeDelta> output = getConnectorFacade().updateDelta(objGroup, uid, delta, options);
    assertNotNull(output);
  }
}
