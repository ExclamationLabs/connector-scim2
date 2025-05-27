package com.exclamationlabs.connid.base.scim2.driver.rest;

import com.exclamationlabs.connid.base.connector.driver.DriverInvocator;
import com.exclamationlabs.connid.base.connector.driver.rest.RestRequest;
import com.exclamationlabs.connid.base.connector.driver.rest.RestResponseData;
import com.exclamationlabs.connid.base.connector.filter.FilterType;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import com.exclamationlabs.connid.base.scim2.driver.rest.slack.Scim2SlackUsersInvocator;
import com.exclamationlabs.connid.base.scim2.model.*;
import com.exclamationlabs.connid.base.scim2.model.response.ListGroupResponse;
import com.exclamationlabs.connid.base.scim2.model.response.ListUsersResponse;
import com.exclamationlabs.connid.base.scim2.model.slack.Scim2SlackUser;
import org.apache.http.HttpStatus;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.URLEncoder;

public class Scim2UsersInvocator implements DriverInvocator<Scim2Driver, Scim2User>
{
    private static final Log LOG = Log.getLog(Scim2UsersInvocator.class);

    public static List<Scim2OperationMulti> addOperations(String name, List<Map<String, String>> add, List<Map<String, String>> remove) {
        List<Scim2OperationMulti> operations = new ArrayList<>();
        if (remove != null && !remove.isEmpty())
        {
            for (Map<String, String> item: remove)
            {
                Scim2OperationMulti op = new Scim2OperationMulti();
                op.setOperation("remove");
                String path = String.format("%s[value eq \"%s\"]", name, item.get("value"));
                op.setPath(path);
                operations.add(op);
            }
        }
        if ( add != null && !add.isEmpty() ) {
            Scim2OperationMulti addOperation = new Scim2OperationMulti();
            addOperation.setOperation("add");
            addOperation.setPath(name);
            addOperation.setValue(new ArrayList<>(add));
            operations.add(addOperation);
        }
        return operations;
    }
    @Override
    public String create(Scim2Driver driver, Scim2User user) throws ConnectorException
    {
        String id = null;
        Scim2Configuration config = driver.getConfiguration();
        if (config.getEnableSlackSchema())
        {
            id = new Scim2SlackUsersInvocator().create(driver, (Scim2SlackUser) user);
        }
        else if ( config.getEnableStandardSchema() || config.getEnableAWSSchema() )
        {
            RestRequest<Scim2User> request =
                    new RestRequest.Builder<>(Scim2User.class)
                            .withPost()
                            .withRequestUri(driver.getConfiguration().getUsersEndpointUrl())
                            .withRequestBody(user)
                            .build();

            RestResponseData<Scim2User> data = driver.executeRequest(request);
            Scim2User theUser = data.getResponseObject();
            if ( theUser != null) {
                id = theUser.getId();
            }
        }
        else if (config.getEnableDynamicSchema())
        {
            ;
        }
        return id;
    }

    @Override
    public void delete(Scim2Driver driver, String userId) throws ConnectorException
    {
        Scim2Configuration config = driver.getConfiguration();

        if (config.getEnableSlackSchema())
        {
            new Scim2SlackUsersInvocator().delete(driver, userId);
        }
        else if (config.getEnableAWSSchema()
                || config.getEnableStandardSchema()
                || config.getEnableDynamicSchema())
        {
            // Delete is usually delete 
            RestRequest<Void> req = new RestRequest.Builder<>(Void.class)
                            .withDelete()
                            .withRequestUri(config.getUsersEndpointUrl() + "/" +userId)
                            .build();
            RestResponseData<Void> data = driver.executeRequest(req);
        }
    }

    @Override
    // public <T extends Scim2User> Set<T> getAll(
    public Set<Scim2User> getAll(Scim2Driver driver, ResultsFilter filter, ResultsPaginator paginator, Integer forceNumber)
            throws ConnectorException
    {
        Set<? extends Scim2User> allUsers = null;
        Scim2Configuration config = driver.getConfiguration();

        if (config.getEnableSlackSchema())
        {
            allUsers = new Scim2SlackUsersInvocator().getAll(driver, filter, paginator, forceNumber);
        }
        else if (config.getEnableAWSSchema() || config.getEnableStandardSchema())
        {
            allUsers = getUsersList(driver, filter, paginator);
            for (Scim2User user : allUsers) {
                if (user.getGroups() == null || user.getGroups().isEmpty()) {
                    user.setGroups(getGroupsForUser(driver, user.getId()));
                }
            }
        }
        else if (config.getEnableDynamicSchema())
        {
            // Dynamic Schema user LinkedTreeList
            allUsers = getUsersList(driver, filter, paginator);;
        }
        return (Set<Scim2User>) allUsers;
    }

    /**
     * @param filter
     * @return a SCIM2 filter or null when filter is not specified
     */
    public static String getFilterParameter(ResultsFilter filter )
    {
        String parameter = null;
        if ( filter != null && filter.hasFilter())
        {
            String attribute = filter.getAttribute();
            String value = filter.getValue();
            if ( filter.getFilterType().equals(FilterType.EqualsFilter))
            {
                parameter = "filter=" + attribute +"%20eq%20%22"+value+"%22";
            }
        }
        return parameter;
    }

    @Override
    public Scim2User getOne(Scim2Driver driver, String objectId, Map<String, Object> prefetchDataMap)
            throws ConnectorException
    {
        Scim2User user = null;
        Scim2Configuration config = driver.getConfiguration();
        if (config.getEnableSlackSchema())
        {
            user = new Scim2SlackUsersInvocator().getOne(driver, objectId, prefetchDataMap);
        }
        else if (config.getEnableStandardSchema() || config.getEnableAWSSchema())
        {
            RestRequest<Scim2User> req =
                    new RestRequest.Builder<>(Scim2User.class)
                            .withGet()
                            .withRequestUri(driver.getConfiguration().getUsersEndpointUrl() + "/" + objectId)
                            .build();
            RestResponseData<Scim2User> response = driver.executeRequest(req);
            if (response.getResponseStatusCode() == HttpStatus.SC_OK) {
                user = response.getResponseObject();
                if (user.getGroups() == null || user.getGroups().isEmpty()) {
                    user.setGroups(getGroupsForUser(driver, user.getId()));
                }
            }
        }
        else if (config.getEnableDynamicSchema())
        {
            ;
        }
        return user;
    }

    @Override
    public Scim2User getOneByName(Scim2Driver driver, String name, Map<String, Object> prefetchDataMap)
    {
        return getOneByName(driver, name);
    }

    @Override
    public Scim2User getOneByName(Scim2Driver driver, String name) throws ConnectorException
    {
        Scim2User user = null;
        Scim2Configuration config = driver.getConfiguration();
        if (config.getEnableSlackSchema())
        {
            user = new Scim2SlackUsersInvocator().getOneByName(driver, name);
        }
        else if (config.getEnableAWSSchema() || config.getEnableStandardSchema())
        {
            String queryString = "?filter=userName%20eq%20%22" + name + "%22";
            RestRequest<ListUsersResponse> req = new RestRequest.Builder<>(ListUsersResponse.class)
                    .withGet()
                    .withRequestUri(config.getUsersEndpointUrl() + queryString)
                    .build();
            RestResponseData<ListUsersResponse> response = driver.executeRequest(req);
            if (response.getResponseStatusCode() == HttpStatus.SC_OK)
            {
                List<Scim2User> list = response.getResponseObject().getResources();
                if (list != null && list.size() > 0)
                {
                    user = list.get(0);
                    if (user.getGroups() == null || user.getGroups().isEmpty()) {
                        user.setGroups(getGroupsForUser(driver, user.getId()));
                    }

                }
            }
        }
        else if (config.getEnableDynamicSchema())
        {
            // Do the dynamic lookup here. The difference is the type
            ;
        }
        return user;
    }

    /**
     * @param paginator
     * @return SCIM2 Pagination parameters or null when pagination is not specified
     */
    public static String getPagingParameter(ResultsPaginator paginator)
    {
        String parameter = null;
        if ( paginator != null )
        {
            if ( paginator.hasPagination() )
            {
                Integer count = paginator.getPageSize();
                parameter = "count=" + count;
            }
            if ( paginator.getCurrentOffset() != null ) {
                Integer startIndex = paginator.getCurrentOffset();
                String start = "startIndex=" + startIndex;
                parameter = (parameter == null) ? start : parameter + "&" + start  ;
            }
            else if ( paginator.getCurrentPageNumber() != null )
            {
                int startIndex = ((paginator.getCurrentPageNumber()-1) * paginator.getPageSize()) + 1;
                String start = "startIndex=" + startIndex;
                parameter = (parameter == null) ? start : parameter + "&" + start  ;
            }
        }
        else {
            parameter = "";
        }
        return parameter;
    }

    @Override
    public Map<String, Object> getPrefetch(Scim2Driver driver)
    {
        return new LinkedHashMap<>();
    }

    public Set<Scim2User> getUsersList( Scim2Driver driver, ResultsFilter filter, ResultsPaginator paginator)
    {
        Scim2Configuration config = driver.getConfiguration();
        List<Scim2User> userList = new ArrayList<>();
        String filterParameter = Scim2UsersInvocator.getFilterParameter(filter);
        String pagingParameter = Scim2UsersInvocator.getPagingParameter(paginator);
        String query = "";
        if (pagingParameter != null && filterParameter != null)
        {
            query = "?" + pagingParameter + "&" + filterParameter;
        }
        else if (pagingParameter != null)
        {
            query = "?" + pagingParameter;
        }
        else if (filterParameter != null)
        {
            query = "?" + filterParameter;
        }
        RestRequest<ListUsersResponse> request =
                new RestRequest.Builder<>(ListUsersResponse.class)
                        .withGet()
                        .withRequestUri(config.getUsersEndpointUrl() + query)
                        .build();
        RestResponseData<ListUsersResponse> data = driver.executeRequest(request);
        ListUsersResponse response = data.getResponseObject();

        if (response != null && data.getResponseStatusCode() == 200)
        {
            userList = response.getResources();
            if ( userList != null && !userList.isEmpty() && paginator.hasPagination()) {
                updatePaginator(paginator, userList.size(), response.getTotalResults(), response.getItemsPerPage());
            } else {
                paginator.setNoMoreResults(true);
            }
        } else {
            paginator.setNoMoreResults(true);
        }
        // Convert the list to a Set
        Set<Scim2User> subSet = new HashSet<>(userList);

        return subSet;
    }
    @Override
    public void update(Scim2Driver driver, String userId, Scim2User user)
            throws ConnectorException
    {
        Scim2Configuration config = driver.getConfiguration();
        if (config.getEnableSlackSchema())
        {
            new Scim2SlackUsersInvocator().update(driver, userId, (Scim2SlackUser) user);
        }
        else if (config.getEnableStandardSchema() || config.getEnableAWSSchema())
        {
            Scim2User current = getOne(driver, userId, null);
            user.setId(userId);
            try
            {
                if (user.getUserName() == null || user.getUserName().isEmpty())
                {
                    user.setUserName(current.getUserName());
                }
                if (user.getName() == null)
                {
                    user.setName(current.getName());
                }
                else
                {
                    if (user.getName().getFamilyName() == null || user.getName().getFamilyName().isEmpty())
                    {
                        user.getName().setFamilyName(current.getName().getFamilyName());
                    }
                    if (user.getName().getGivenName() == null || user.getName().getGivenName().isEmpty())
                    {
                        user.getName().setGivenName(current.getName().getGivenName());
                    }
                }
                if (user.getDisplayName() == null || user.getDisplayName().isEmpty())
                {
                    if (current.getDisplayName() == null || current.getDisplayName().isEmpty())
                    {
                        user.setDisplayName(current.getName().getGivenName() + " " + current.getName().getFamilyName());
                    }
                    else
                    {
                        user.setDisplayName(current.getDisplayName());
                    }
                }
            }
            catch ( Exception e)
            {
                LOG.error(e, "{0}", e.getMessage());
            }

            RestRequest<Scim2User> req = new RestRequest.Builder<>(Scim2User.class)
                    .withPut()
                    .withRequestUri(config.getUsersEndpointUrl() + "/" + userId)
                    .withRequestBody(user)
                    .build();
            driver.executeRequest(req);
            updateMultiValued(driver, userId, user);
        }
        else if (config.getEnableDynamicSchema())
        {
            ;
        }
    }

    public void updateMultiValued(Scim2Driver driver, String userId, Scim2User user)
        throws ConnectorException {

        String url = driver.getConfiguration().getUsersEndpointUrl() + "/" + userId;
        Scim2PatchOp patchOp = new Scim2PatchOp();
        patchOp.setOperations(new ArrayList<>());
        List<Scim2OperationMulti> operations = new ArrayList<>();
        operations.addAll(addOperations("addresses", user.getAddressesAdded(), user.getAddressesRemoved()));
        operations.addAll(addOperations("emails", user.getEmailsAdded(), user.getEmailsRemoved()));
        operations.addAll(addOperations("entitlements", user.getEntitlementsAdded(), user.getEntitlementsRemoved()));
        operations.addAll(addOperations("groups", user.getGroupsAdded(), user.getGroupsRemoved()));
        operations.addAll(addOperations("ims", user.getImsAdded(), user.getImsRemoved()));
        operations.addAll(addOperations("phoneNumbers", user.getPhoneNumbersAdded(), user.getPhoneNumbersRemoved()));
        operations.addAll(addOperations("photos", user.getPhotosAdded(), user.getPhotosRemoved()));
        operations.addAll(addOperations("roles", user.getRolesAdded(), user.getRolesRemoved()));
        operations.addAll(addOperations("x509Certificates", user.getX509CertificatesAdded(), user.getX509CertificatesRemoved()));
        patchOp.setOperations(operations);
        if (!operations.isEmpty())
        {
            RestRequest<Scim2User> request =
                    new RestRequest.Builder<>(Scim2User.class)
                            .withPatch()
                            .withRequestUri(url)
                            .withRequestBody(patchOp)
                            .build();
            RestResponseData<Scim2User> data = driver.executeRequest(request);
            if (data.getResponseStatusCode() != HttpStatus.SC_OK && data.getResponseStatusCode() != HttpStatus.SC_NO_CONTENT)
            {
                LOG.warn(String.format("SCIM2 Update User returned HTTP status %s", data.getResponseStatusCode()));
            }
        }
    }
    public static void updatePaginator(ResultsPaginator paginator, int totalReturned, int totalResults, int pageSize){
        paginator.setTotalResults(totalResults);
        if (pageSize < paginator.getPageSize())
        {
            // override the number of items returned in the page
            pageSize = paginator.getPageSize();
        }
        Integer pages = totalResults / pageSize;
        if ( (totalResults % pageSize) > 0 )
        {
            pages++;
        }
        paginator.setNumberOfTotalPages( pages);
        if (paginator.getNumberOfProcessedResults() == null) {
            paginator.setNumberOfProcessedResults(0);
        }
        paginator.setNumberOfProcessedResults(paginator.getNumberOfProcessedResults() + totalReturned);
        if ( paginator.getNumberOfProcessedPages() == null )
        {
            paginator.setNumberOfProcessedPages(0);
        }
        paginator.setNumberOfProcessedPages(paginator.getNumberOfProcessedPages()+1);
        if (Objects.equals(paginator.getTotalResults(), paginator.getNumberOfProcessedResults())) {
            paginator.setNoMoreResults(true);
        }
        if ( totalReturned == 0 )
        {
            paginator.setNoMoreResults(true);
        }
    }

    private List<Map<String, String>> getGroupsForUser(Scim2Driver driver, String userId) {
        List<Map<String, String>> groupMaps = new ArrayList<>();
        try {

            RestRequest<ListGroupResponse> request =
                    new RestRequest.Builder<>(ListGroupResponse.class)
                            .withGet()
                            .withRequestUri(driver.getConfiguration().getGroupsEndpointUrl()  )
                            .build();
            RestResponseData<ListGroupResponse> data = driver.executeRequest(request);
            ListGroupResponse response = data.getResponseObject();
            if (response != null && data.getResponseStatusCode() == HttpStatus.SC_OK)
            {
                List<Scim2Group> groups = response.getResources();
                for (Scim2Group group : groups)
                {
                    if (isUserInGroup(driver, userId, group.getId()))
                    {
                        Map<String, String> groupMap = new HashMap<>();
                        groupMap.put("value", group.getId());
                        groupMap.put("display", group.getDisplayName());
                        groupMaps.add(groupMap);
                    }
                }
            }

        } catch (Exception e) {
            LOG.warn("getGroupsForUser :: Exception: " + e.getMessage());
        }

        for (Map<String, String> groupMap : groupMaps) {
            if (groupMap != null) {
                LOG.info("getGroupsForUser :: Group Map: ");
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
        String query = "";
        if (driver.getConfiguration().getUserInGroupSkipFilterKeyword() != null &&
                driver.getConfiguration().getUserInGroupSkipFilterKeyword() ) {
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
