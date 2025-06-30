package com.exclamationlabs.connid.base.scim2.adapter;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.scim2.attribute.Scim2GroupAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.*;

import com.exclamationlabs.connid.base.connector.adapter.AdapterValueTypeConverter;
import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import com.exclamationlabs.connid.base.scim2.model.Scim2Group;

import java.util.*;

import com.google.gson.Gson;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;

public class Scim2GroupsAdapter extends BaseAdapter<Scim2Group, Scim2Configuration> {
  public static final String SCIM2_GROUP ="Scim2Group";
  public static final String SCIM2_CORE_GROUP_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:Group";
  private static final Log LOG = Log.getLog(Scim2GroupsAdapter.class);

  @Override
  public ObjectClass getType() {
    return new ObjectClass(SCIM2_GROUP);
  }

  @Override
  public Class<Scim2Group> getIdentityModelClass() {
    return Scim2Group.class;
  }

  private Set<Map<String, String>> getMembersFromJSON(Set<String> jsonMembers)
  {
    Set<Map<String, String>> members = null;
    if ( jsonMembers != null && !jsonMembers.isEmpty() ) {
      members = new HashSet<>();
      for (String jsonMember : jsonMembers) {
        Map<String, String> member = new HashMap<>();
        Gson gson = new Gson();
        Map<String, Object> memberObject = gson.fromJson(jsonMember, Map.class);
        for (Map.Entry<String, Object> entry : memberObject.entrySet()) {
          member.put(entry.getKey(), entry.getValue().toString());
        }
        members.add(member);
      }
    }
    return members;
  }

  private Set<Map<String, String>> getMembersFromValues(Set<String> listOfMembers)
  {
    Set<Map<String, String>> members = null;
    if ( listOfMembers != null && !listOfMembers.isEmpty() ) {
      members = new HashSet<>();
      for (String member : listOfMembers) {
        Map<String, String> memberMap = new HashMap<>();
        memberMap.put("value", member);
        members.add(memberMap);
      }
    }
    return members;
  }
  @Override
  public Set<ConnectorAttribute> getConnectorAttributes() {

    Boolean isSlack = getConfiguration().getEnableSlackSchema();
    Boolean isAWS = getConfiguration().getEnableAWSSchema();
    Boolean isStandard = getConfiguration().getEnableStandardSchema();
    Boolean isDynamic = getConfiguration().getEnableDynamicSchema();

    Set<ConnectorAttribute> result = null;

    // Slack and Amazon Web Service share the same group attributes as the core scim2 group schema
    result = new HashSet<>();
    result.add(new ConnectorAttribute(Uid.NAME, id.name(), STRING, NOT_UPDATEABLE, REQUIRED));
    // A human-readable name for the Group.
    result.add(new ConnectorAttribute(Name.NAME, displayName.name(), STRING, REQUIRED));
    result.add(new ConnectorAttribute(externalId.name(), STRING));
    /*
     * Identifier for members of the Group.
     * This is a JSON string containing sub attributes
     * value, $ref, and type
     */
    result.add(new ConnectorAttribute(members.name(), STRING, MULTIVALUED));
    result.add(new ConnectorAttribute(members_value.name(), STRING, MULTIVALUED));
    return result;
  }

  @Override
  protected Set<Attribute> constructAttributes(Scim2Group group)
  {
    Set<Attribute> attributes = new HashSet<>();
    Set<String> memberSet = new HashSet<>();
    Set<String> memberSetValues = new HashSet<>();
    //attributes.add(AttributeBuilder.build(displayName.name(), group.getDisplayName()));
    attributes.add(AttributeBuilder.build(Name.NAME, group.getIdentityNameValue()));
    attributes.add(AttributeBuilder.build(id.name(), group.getId()));
    attributes.add(AttributeBuilder.build(Uid.NAME, group.getIdentityIdValue()));
    attributes.add(AttributeBuilder.build(externalId.name(), group.getExternalId()));

    // Construct a multivalued set of JSON strings for the group members
    if ( group.getMembers() != null && group.getMembers().size() > 0 )
    {
      String member = null;

      for(Map<String, String> item: group.getMembers()) {
          StringJoiner joiner = new StringJoiner(",", "{", "}");
          item.forEach((key, value) -> {
            joiner.add(String.format("\"%s\":\"%s\"", key, value));
            if (key.equals("value")) {
              memberSetValues.add(value);
            }
          });
          member = joiner.toString();
          memberSet.add(member);
      }
      attributes.add(AttributeBuilder.build(members.name(), memberSet));
      attributes.add(AttributeBuilder.build(members_value.name(), memberSetValues));
    }
    return attributes;
  }

  @Override
  protected Scim2Group constructModel(
      Set<Attribute> attributes,
      Set<Attribute> addedMultiValueAttributes,
      Set<Attribute> removedMultiValueAttributes,
      boolean isCreate) {

    Scim2Group group = new Scim2Group();
    group.setId(AdapterValueTypeConverter.getIdentityIdAttributeValue(attributes));
    if(group.getId() == null || group.getId().isEmpty())
    {
      group.setId(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, id));
    }
    group.setDisplayName(AdapterValueTypeConverter.getIdentityNameAttributeValue(attributes));
    if ( group.getDisplayName() == null || group.getDisplayName().isEmpty()){
      group.setDisplayName(
              AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, displayName));
    }
    group.setExternalId(
        AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, externalId));

    Set<String> jsonMembers = readAssignments(attributes, members);
    group.setMembers(getMembersFromJSON(jsonMembers));

    Set<String> membersValues = readAssignments(attributes, members_value);
    group.setMembers(getMembersFromValues(membersValues));
    // Removed these members
    jsonMembers = readAssignments(removedMultiValueAttributes, members);
    group.setRemoveMembers(getMembersFromJSON(jsonMembers));

    membersValues = readAssignments(removedMultiValueAttributes, members_value);
    group.setRemoveMembers(getMembersFromValues(membersValues));

    // Add these members
    jsonMembers = readAssignments(addedMultiValueAttributes, members);
    group.setAddMembers(getMembersFromJSON(jsonMembers));

    membersValues = readAssignments(addedMultiValueAttributes, members_value);
    group.setAddMembers(getMembersFromValues(membersValues));
    // Set the schema List to groups
    Set<String> schemaList = new HashSet<>();
    schemaList.add(SCIM2_CORE_GROUP_SCHEMA);
    group.setSchemas(schemaList);
    return group;
  }
}
