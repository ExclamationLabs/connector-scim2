package com.exclamationlabs.connid.base.scim2.driver.rest;

import com.exclamationlabs.connid.base.connector.driver.DriverInvocator;
import com.exclamationlabs.connid.base.connector.driver.rest.RestRequest;
import com.exclamationlabs.connid.base.connector.driver.rest.RestResponseData;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import com.exclamationlabs.connid.base.scim2.model.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.exclamationlabs.connid.base.scim2.model.response.ListGroupResponse;
import com.exclamationlabs.connid.base.scim2.model.response.ListUsersResponse;
import org.apache.http.HttpStatus;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class Scim2GroupsInvocator implements DriverInvocator<Scim2Driver, Scim2Group> {
  private static final Log LOG = Log.getLog(Scim2GroupsInvocator.class);

  @Override
  public String create(Scim2Driver driver, Scim2Group model) throws ConnectorException {
    String id = null;
    Scim2Configuration config = driver.getConfiguration();
    if ( config.getEnableSlackSchema()
            || config.getEnableStandardSchema()
            || config.getEnableAWSSchema() )
    {
      RestRequest<Scim2Group> request =
              new RestRequest.Builder<>(Scim2Group.class)
                      .withPost()
                      .withRequestUri(driver.getConfiguration().getGroupsEndpointUrl())
                      .withRequestBody(model)
                      .build();

      RestResponseData<Scim2Group> data = driver.executeRequest(request);
      Scim2Group group = data.getResponseObject();
      if ( group!= null) {
        id = group.getId();
      }
    }
    else if (config.getEnableDynamicSchema())
    {
      ;
    }
    return id;
  }

  @Override
  public void delete(Scim2Driver driver, String groupId) throws ConnectorException {
    // Delete is usually delete
    Scim2Configuration config = driver.getConfiguration();
    RestRequest<Void> req = new RestRequest.Builder<>(Void.class)
            .withDelete()
            .withRequestUri(config.getGroupsEndpointUrl() + "/" +groupId)
            .build();
    RestResponseData<Void> data = driver.executeRequest(req);
  }

  @Override
  public Set<Scim2Group> getAll(Scim2Driver driver, ResultsFilter filter, ResultsPaginator paginator, Integer resultCap) throws ConnectorException
  {
    List<Scim2Group> groupList = new ArrayList<>();
    Scim2Configuration config = driver.getConfiguration();
    String filterParameter = Scim2UsersInvocator.getFilterParameter(filter);
    String pagingParameter = Scim2UsersInvocator.getPagingParameter(paginator);
    String query = "";
    if ( pagingParameter != null && filterParameter != null )
    {
      query = "?" + pagingParameter + "&" +  filterParameter ;
    }
    else if (pagingParameter != null )
    {
      query = "?" + pagingParameter;
    }
    else if (filterParameter != null )
    {
      query = "?" + filterParameter;
    }
    RestRequest<ListGroupResponse> request =
            new RestRequest.Builder<>(ListGroupResponse.class)
                    .withGet()
                    .withRequestUri(config.getGroupsEndpointUrl() + query )
                    .build();
    RestResponseData<ListGroupResponse> data = driver.executeRequest(request);
    ListGroupResponse response = data.getResponseObject();
    if ( response != null && data.getResponseStatusCode() == HttpStatus.SC_OK)
    {
      groupList = response.getResources();
      if ( groupList != null && !groupList.isEmpty() && paginator.hasPagination())
      {
        Scim2UsersInvocator.updatePaginator(paginator, groupList.size(), response.getTotalResults(), response.getItemsPerPage());
      }
      else {
        paginator.setNoMoreResults(true);
      }
    }
    else {
      paginator.setNoMoreResults(true);
    }
    Set<Scim2Group> groupSet = new HashSet<>(groupList);

    for (Scim2Group group : groupSet) {
      if (group.getMembers() == null || group.getMembers().isEmpty()) {
        group.setMembers(getUsersForGroup(driver, group.getId()));
      }
    }
    return groupSet;
  }

  @Override
  public Set<Scim2Group> getAll(Scim2Driver driver, ResultsFilter filter, ResultsPaginator paginator, Integer resultCap, Map<String, Object> prefetchDataMap) throws ConnectorException
  {
    return getAll(driver, filter, paginator, resultCap);
  }

  @Override
  public Scim2Group getOne(Scim2Driver driver, String objectId, Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    Scim2Group group = null;
    Scim2Configuration config = driver.getConfiguration();
    if (config.getEnableStandardSchema()
            || config.getEnableAWSSchema()
            || config.getEnableSlackSchema())
    {
      RestRequest<Scim2Group> req =
              new RestRequest.Builder<>(Scim2Group.class)
                      .withGet()
                      .withRequestUri(config.getGroupsEndpointUrl() + "/" + objectId)
                      .build();
      RestResponseData<Scim2Group> response = driver.executeRequest(req);
      if (response.getResponseStatusCode() == HttpStatus.SC_OK) {
        group = response.getResponseObject();
        if (group.getMembers() == null || group.getMembers().isEmpty()) {
          group.setMembers(getUsersForGroup(driver, group.getId()));
        }
      }
    }
    else if (config.getEnableDynamicSchema())
    {
      // Assume dynamic is the same for now since a Group member is a map
      RestRequest<Scim2Group> req =
              new RestRequest.Builder<>(Scim2Group.class)
                      .withGet()
                      .withRequestUri(config.getGroupsEndpointUrl() + "/" + objectId)
                      .build();
      RestResponseData<Scim2Group> response = driver.executeRequest(req);
      if (response.getResponseStatusCode() == HttpStatus.SC_OK) {
        group = response.getResponseObject();
        if (group.getMembers() == null || group.getMembers().isEmpty()) {
          group.setMembers(getUsersForGroup(driver, group.getId()));
        }
      }
    }
    return group;
  }

  @Override
  public Scim2Group getOneByName(Scim2Driver driver, String objectName) throws ConnectorException
  {
    Scim2Group group = null;
    Scim2Configuration config = driver.getConfiguration();
    String displayName = URLEncoder.encode(objectName);
    String query;
    if (driver.getConfiguration().getGroupSkipFilterKeyword() != null &&
            driver.getConfiguration().getGroupSkipFilterKeyword() ) {
      query = "?displayName+eq+%22" + displayName + "%22";
    } else {
      query = "?filter=displayName+eq+%22" + displayName + "%22";
    }

    RestRequest<ListGroupResponse> request =
            new RestRequest.Builder<>(ListGroupResponse.class)
                    .withGet()
                    .withRequestUri(config.getGroupsEndpointUrl() + query )
                    .build();
    RestResponseData<ListGroupResponse> data = driver.executeRequest(request);

    if (data.getResponseStatusCode() == HttpStatus.SC_OK && data.getResponseObject() != null )
    {
      ListGroupResponse response = data.getResponseObject();
      List<Scim2Group> list = response.getResources();
      if (list != null && !list.isEmpty())
      {
        group = list.get(0);
        if (group.getMembers() == null || group.getMembers().isEmpty()) {
          group.setMembers(getUsersForGroup(driver, group.getId()));
        }
      }
    }
    return group;
  }

  @Override
  public Scim2Group getOneByName(Scim2Driver driver, String objectName, Map<String, Object> prefetchDataMap) throws ConnectorException
  {
    return getOneByName(driver, objectName);
  }

  @Override
  public void update(Scim2Driver driver, String groupId, Scim2Group model)
      throws ConnectorException {
    Scim2Configuration config = driver.getConfiguration();
    if ( model.getDisplayName() != null && !model.getDisplayName().isEmpty()) {
      try
      {
        model.setId(groupId);
        RestRequest<Scim2Group> req = new RestRequest.Builder<>(Scim2Group.class)
                .withPut()
                .withRequestUri(config.getGroupsEndpointUrl() + "/" + groupId)
                .withRequestBody(model)
                .build();
        RestResponseData<Scim2Group> response = driver.executeRequest(req);
        if ( response.getResponseStatusCode() == HttpStatus.SC_NOT_IMPLEMENTED) {
          Scim2PatchOpSingle patchOp = new Scim2PatchOpSingle();
          patchOp.setOperations(new ArrayList<>());
          Scim2OperationSingle op = new Scim2OperationSingle();
          op.setOperation("replace");
          Map<String, String> valueMap = new HashMap<>();
          valueMap.put("id", groupId);
          valueMap.put("displayName", model.getDisplayName());
          op.setValue(valueMap);
          patchOp.getOperations().add(op);
          req = new RestRequest.Builder<>(Scim2Group.class)
                  .withPatch()
                  .withRequestUri(config.getGroupsEndpointUrl() + "/" + groupId)
                  .withRequestBody(patchOp)
                  .build();
          driver.executeRequest(req);
        }
      }
      catch ( Exception e )
      {
        LOG.error(e, "{0}", e.getMessage());
      }
    }
    updateMembers(driver, groupId, model.getAddMembers(), model.getRemoveMembers());
  }

  public void updateMembers(Scim2Driver driver, String groupId,
                            Set<Map<String, String>> add,
                            Set<Map<String, String>> remove)
  {
    boolean hasWork = false;
    String url = driver.getConfiguration().getGroupsEndpointUrl() + "/" + groupId;
    Scim2PatchOp patchOp = new Scim2PatchOp();
    patchOp.setOperations(new ArrayList<>());

    if (remove != null && !remove.isEmpty())
    {
      for (Map<String, String>item: new ArrayList<>(remove))
      {
        Scim2OperationMulti op = new Scim2OperationMulti();
        op.setOperation("remove");
        String path = String.format("members[value eq \"%s\"]", item.get("value"));
        op.setPath(path);
        patchOp.getOperations().add(op);
      }
      hasWork = true;
    }

    if ( add != null && !add.isEmpty() ) {
      Scim2OperationMulti addOperation = new Scim2OperationMulti();
      addOperation.setOperation("add");
      addOperation.setPath("members");
      addOperation.setValue(new ArrayList<>(add));
      patchOp.getOperations().add(addOperation);
      hasWork = true;
    }

    if ( hasWork )
    {
      RestRequest<Scim2Group> request =
              new RestRequest.Builder<>(Scim2Group.class)
                      .withPatch()
                      .withRequestUri(url)
                      .withRequestBody(patchOp)
                      .build();
      RestResponseData<Scim2Group> data = driver.executeRequest(request);
      if (data.getResponseStatusCode() != HttpStatus.SC_OK && data.getResponseStatusCode() != HttpStatus.SC_NO_CONTENT)
      {
        LOG.warn(String.format("SCIM2 Update Group Members returned HTTP status %s", data.getResponseStatusCode()));
      }
    }
  }

  private Set<Map<String, String>> getUsersForGroup(Scim2Driver driver, String groupId) {
    Set<Map<String, String>> groupMaps = new HashSet<>();
    try {
      String filter = String.format("groups.value eq \"%s\"",
              URLEncoder.encode(groupId, StandardCharsets.UTF_8.toString()));
      String query = "?filter=" + URLEncoder.encode(filter, StandardCharsets.UTF_8.toString());

      RestRequest<ListUsersResponse> request =
              new RestRequest.Builder<>(ListUsersResponse.class)
                      .withGet()
                      .withRequestUri(driver.getConfiguration().getUsersEndpointUrl() + query )
                      .build();
      RestResponseData<ListUsersResponse> data = driver.executeRequest(request);
      ListUsersResponse response = data.getResponseObject();
      if (response != null && data.getResponseStatusCode() == HttpStatus.SC_OK)
      {
        List<Scim2User> users = response.getResources();
        for (Scim2User user : users)
        {
          if (isUserInGroup(driver, user.getId(), groupId))
          {
            Map<String, String> groupMap = new HashMap<>();
            groupMap.put("value", user.getId());
            groupMap.put("display", user.getDisplayName());
            groupMaps.add(groupMap);
          }
        }
      }

    } catch (Exception e) {
      LOG.warn("getUsersForGroup :: Exception: " + e.getMessage());
    }

      for (Map<String, String> groupMap : groupMaps) {
          if (groupMap != null) {
              LOG.info("getUsersForGroup :: Group Map: ");
              for (Map.Entry<String, String> entry : groupMap.entrySet()) {
                  LOG.info("Key: " + entry.getKey() + ", Value: " + entry.getValue());
              }
          }
      }

      return groupMaps;
  }

  private static boolean isUserInGroup(Scim2Driver driver,String userId, String groupId) throws Exception {
    String filter = String.format("id eq \"%s\" and members eq \"%s\"",
            URLEncoder.encode(groupId, StandardCharsets.UTF_8.toString()),
            URLEncoder.encode(userId, StandardCharsets.UTF_8.toString()));
    String query;
    if (driver.getConfiguration().getGroupSkipFilterKeyword() != null &&
            driver.getConfiguration().getGroupSkipFilterKeyword() ) {
      query = "?" + URLEncoder.encode(filter, StandardCharsets.UTF_8.toString());
    } else {
      query = "?filter=" + URLEncoder.encode(filter, StandardCharsets.UTF_8.toString());
    }

    RestRequest<ListGroupResponse> request =
            new RestRequest.Builder<>(ListGroupResponse.class)
                    .withGet()
                    .withRequestUri(driver.getConfiguration().getGroupsEndpointUrl() + query)
                    .build();


    RestResponseData<ListGroupResponse> data = driver.executeRequest(request);

    if (data.getResponseStatusCode() != HttpStatus.SC_OK && data.getResponseStatusCode() != HttpStatus.SC_NO_CONTENT)
    {
      LOG.warn(String.format("SCIM2 Update isUserInGroup returned HTTP status %s", data.getResponseStatusCode()));
    }else{
      return data.getResponseObject().getTotalResults() > 0;
    }

    return false;

  }
}
