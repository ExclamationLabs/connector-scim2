package com.exclamationlabs.connid.base.neo.scim2;

import com.exclamationlabs.connid.base.connector.driver.exception.DriverDataNotFoundException;
import com.exclamationlabs.connid.base.connector.driver.rest.RestRequest;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.IamType;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessInvocator;
import com.exclamationlabs.connid.base.neo.scim2.model.Scim2Group;
import com.exclamationlabs.connid.base.neo.scim2.model.Scim2User;
import com.exclamationlabs.connid.base.neo.scim2.model.response.Scim2GroupsResponse;
import com.exclamationlabs.connid.base.neo.scim2.model.response.Scim2UsersResponse;
import com.exclamationlabs.connid.base.neo.scim2.schema.SchemaFactory;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Scim2UserInvocator implements FullAccessInvocator<Scim2Configuration, Scim2Driver, Scim2User> {

    @Override
    public String create(Scim2Driver driver, Scim2Configuration config, Scim2User user) throws ConnectorException {
        var schemas = SchemaFactory.forMode(Mode.valueOf(config.getMode()), IamType.USER);
        user.setSchemas(SchemaFactory.getSchemaUrns(schemas));
        var userCreateResponse = driver.getClient().post(config.getUsersEndpointUrl(), user, Scim2User.class);
        if (userCreateResponse.getStatusCode() == HttpStatus.SC_CREATED || userCreateResponse.getStatusCode() == HttpStatus.SC_OK) {
            var userResponse = userCreateResponse.getResponseBody();
            return userResponse.getId();
        } else {
            throw new ConnectorException("Failed to create user: " + userCreateResponse.getStatusCode());
        }
    }

    @Override
    public void update(Scim2Driver driver, Scim2Configuration config, String userId, Scim2User user) throws ConnectorException {
        Scim2User current = getOne(driver, config, userId, null);
        user.setId(null); // Cannot update id
        user.setExternalId(StringUtils.trimToEmpty(user.getExternalId()));
        try
        {
            // Set schema urn's for put
            var schemas = SchemaFactory.forMode(Mode.valueOf(config.getMode()), IamType.USER);
            user.setSchemas(SchemaFactory.getSchemaUrns(schemas));

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
            if (user.getEmails() == null) {
                user.setEmails(current.getEmails());
            }
        }
        catch ( Exception e)
        {
            Logger.error(this, e.getMessage(), e);
        }

        driver.getClient().put(String.format("%s/%s",config.getUsersEndpointUrl(), userId), user, Scim2User.class);
        // TODO: patch complex sub-items
    }

    @Override
    public void delete(Scim2Driver driver, Scim2Configuration scim2Configuration, String userId) throws ConnectorException {
        driver.getClient().delete(String.format("%s/%s", scim2Configuration.getUsersEndpointUrl(), userId), Void.class);
    }

    @Override
    public boolean supportsNativePagination(Scim2Configuration scim2Configuration) {
        return false;
    }

    @Override
    public Set<Scim2User> getAll(Scim2Driver driver, Scim2Configuration config, ResultsFilter filter, ResultsPaginator paginator, Integer integer, Map<String, Object> map) throws ConnectorException {
        List<Scim2User> userList = new ArrayList<>();
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

        var usersResponse = driver.getClient().get(config.getUsersEndpointUrl() + query, Scim2UsersResponse.class);
        var responseData = usersResponse.getResponseBody();

        if ( responseData != null && usersResponse.getStatusCode() == HttpStatus.SC_OK ) {
            userList = responseData.getResources();
            if (userList != null && !userList.isEmpty()) {
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
                paginator.setNumberOfProcessedResults(paginator.getNumberOfProcessedResults() + userList.size());
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

        return userList != null ? new HashSet<>(userList) : Collections.emptySet();
    }

    @Override
    public Scim2User getOne(Scim2Driver driver, Scim2Configuration config, String userId, Map<String, Object> map) throws ConnectorException {

        try {
            var response = driver.getClient().get(String.format("%s/%s", config.getUsersEndpointUrl(), userId), Scim2User.class);

            if (response.getStatusCode() == HttpStatus.SC_OK) {
                var user = response.getResponseBody();
                return user;
            }
        } catch (DriverDataNotFoundException nfe) {
            return null;
        }
        return null;
    }

    @Override
    public Scim2User getOneByName(Scim2Driver driver, Scim2Configuration config, String objectName, Map<String, Object> prefetchDataMap) throws ConnectorException
    {
        Scim2User user = null;
        String displayName = URLEncoder.encode(objectName);
        String query = "?filter=userName+eq+%22" + displayName + "%22";
        var response = driver.getClient().get(config.getUsersEndpointUrl() + query, Scim2UsersResponse.class);

        if (response.getStatusCode() == HttpStatus.SC_OK && response.getResponseBody() != null )
        {
            Scim2UsersResponse data = response.getResponseBody();
            var list = data.getResources();
            if (list != null && !list.isEmpty())
            {
                user = list.get(0);
//                if (group.getMembers() == null || group.getMembers().isEmpty()) {
//                    group.setMembers(getUsersForGroup(driver, group.getId()));
//                }
            }
        }
        return user;
    }
}
