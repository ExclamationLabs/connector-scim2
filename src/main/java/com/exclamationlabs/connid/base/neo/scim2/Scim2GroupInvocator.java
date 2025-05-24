package com.exclamationlabs.connid.base.neo.scim2;

import com.exclamationlabs.connid.base.connector.driver.exception.DriverDataNotFoundException;
import com.exclamationlabs.connid.base.connector.driver.rest.RestRequest;
import com.exclamationlabs.connid.base.connector.driver.rest.RestResponseData;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.IamType;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessInvocator;
import com.exclamationlabs.connid.base.neo.scim2.model.Scim2Group;
import com.exclamationlabs.connid.base.neo.scim2.model.response.Scim2GroupsResponse;
import com.exclamationlabs.connid.base.neo.scim2.schema.SchemaFactory;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import com.exclamationlabs.connid.base.scim2.model.response.ListGroupResponse;
import org.apache.http.HttpStatus;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Scim2GroupInvocator implements FullAccessInvocator<Scim2Configuration, Scim2Driver, Scim2Group> {
    @Override
    public String create(Scim2Driver driver, Scim2Configuration config, Scim2Group group) throws ConnectorException {
        var schemas = SchemaFactory.forMode(Mode.valueOf(config.getMode()), IamType.GROUP);
        group.setSchemas(SchemaFactory.getSchemaUrns(schemas));
        var createResponse = driver.getClient().post(config.getGroupsEndpointUrl(), group, Scim2Group.class);
        if (createResponse.getStatusCode() == HttpStatus.SC_CREATED || createResponse.getStatusCode() == HttpStatus.SC_OK) {
            var response = createResponse.getResponseBody();
            return response.getId();
        } else {
            throw new ConnectorException("Failed to create group: " + createResponse.getStatusCode());
        }
    }

    @Override
    public void update(Scim2Driver driver, Scim2Configuration config, String groupId, Scim2Group group) throws ConnectorException {
        //Scim2Group current = getOne(driver, config, groupId, null);
        group.setId(null); // Cannot update id

        // Set schema urn's for put
        var schemas = SchemaFactory.forMode(Mode.valueOf(config.getMode()), IamType.GROUP);
        group.setSchemas(SchemaFactory.getSchemaUrns(schemas));

        driver.getClient().put(String.format("%s/%s",config.getGroupsEndpointUrl(), groupId), group, Scim2Group.class);
    }

    @Override
    public void delete(Scim2Driver driver, Scim2Configuration scim2Configuration, String groupId) throws ConnectorException {
        driver.getClient().delete(String.format("%s/%s", scim2Configuration.getGroupsEndpointUrl(), groupId), Void.class);
    }

    @Override
    public boolean supportsNativePagination(Scim2Configuration scim2Configuration) {
        return true;
    }

    @Override
    public Set<Scim2Group> getAll(Scim2Driver driver, Scim2Configuration config, ResultsFilter filter,
                                  ResultsPaginator paginator, Integer integer, Map<String, Object> map) throws ConnectorException {
        List<Scim2Group> groupList = new ArrayList<>();
        String filterParameter = Scim2Driver.getFilterParameter(filter);
        String pagingParameter = Scim2Driver.getPagingParameter(paginator);
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

        var groupsResponse = driver.getClient().get(config.getGroupsEndpointUrl() + query, Scim2GroupsResponse.class);
        var responseData = groupsResponse.getResponseBody();

        if ( responseData != null && groupsResponse.getStatusCode() == HttpStatus.SC_OK ) {
            groupList = responseData.getResources();
            if (groupList != null && !groupList.isEmpty()) {
                paginator.setTotalResults(responseData.getTotalResults());
                Integer pages = responseData.getTotalResults() / responseData.getItemsPerPage();
                if (responseData.getTotalResults() % responseData.getItemsPerPage() > 0 )
                {
                    pages++;
                }
                paginator.setNumberOfTotalPages( pages);
                if (paginator.getNumberOfProcessedResults() == null) {
                    paginator.setNumberOfProcessedResults(0);
                }
                paginator.setNumberOfProcessedResults(paginator.getNumberOfProcessedResults() + groupList.size());
                if ( paginator.getNumberOfProcessedPages() == null )
                {
                    paginator.setNumberOfProcessedPages(0);
                }
                paginator.setNumberOfProcessedPages(paginator.getNumberOfProcessedPages()+1);
                if ( paginator.getTotalResults().equals(paginator.getNumberOfProcessedResults())) {
                    paginator.setNoMoreResults(true);
                }
            }
            else {
                paginator.setNoMoreResults(true);
            }
        }

        return groupList != null ? new HashSet<>(groupList) : Collections.emptySet();
    }

    @Override
    public Scim2Group getOne(Scim2Driver driver, Scim2Configuration config, String groupId, Map<String, Object> map) throws ConnectorException {
        try {
            var response = driver.getClient().get(String.format("%s/%s", config.getGroupsEndpointUrl(), groupId), Scim2Group.class);

            if (response.getStatusCode() == HttpStatus.SC_OK) {
                var group = response.getResponseBody();
                return group;
            }
        } catch (DriverDataNotFoundException nfe) {
            return null;
        }
        return null;
    }


    @Override
    public Scim2Group getOneByName(Scim2Driver driver, Scim2Configuration config, String objectName, Map<String, Object> prefetchDataMap) throws ConnectorException
    {
        Scim2Group group = null;
        String displayName = URLEncoder.encode(objectName);
        String query = "?filter=displayName+eq+%22" + displayName + "%22";
        var groupsResponse = driver.getClient().get(config.getGroupsEndpointUrl() + query, Scim2GroupsResponse.class);

        if (groupsResponse.getStatusCode() == HttpStatus.SC_OK && groupsResponse.getResponseBody() != null )
        {
            Scim2GroupsResponse response = groupsResponse.getResponseBody();
            List<Scim2Group> list = response.getResources();
            if (list != null && !list.isEmpty())
            {
                group = list.get(0);
//                if (group.getMembers() == null || group.getMembers().isEmpty()) {
//                    group.setMembers(getUsersForGroup(driver, group.getId()));
//                }
            }
        }
        return group;
    }
}
