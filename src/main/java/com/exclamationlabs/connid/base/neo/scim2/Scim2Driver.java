package com.exclamationlabs.connid.base.neo.scim2;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.filter.FilterType;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.IamType;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessDriver;
import com.exclamationlabs.connid.base.edition.neo.driver.rest.RestClient;
import com.exclamationlabs.connid.base.neo.scim2.schema.SchemaFactory;
import com.exclamationlabs.connid.base.neo.scim2.schema.Scim2Schema;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Scim2Driver implements FullAccessDriver<Scim2Configuration> {

    private RestClient<Scim2Configuration> restClient;

    private Mode mode;
    private Map<IamType, Set<Scim2Schema>> schemas;


    @Override
    public boolean supportsNativePagination(Scim2Configuration scim2Configuration) {
        return true;
    }

    @Override
    public void initialize(Scim2Configuration scim2Configuration, Authenticator<Scim2Configuration> authenticator) throws ConnectorException {
        restClient = new RestClient<>(scim2Configuration, authenticator, new Scim2RestBehavior());
        if (schemas == null) {
            initializePrimaryMode(scim2Configuration);
            var userSchemas = SchemaFactory.forMode(mode, IamType.USER);
            var groupSchemas = SchemaFactory.forMode(mode, IamType.GROUP);
            schemas = new HashMap<>();
            schemas.put(IamType.USER, userSchemas);
            schemas.put(IamType.GROUP, groupSchemas);
        }
    }

    @Override
    public void test(Scim2Configuration scim2Configuration) throws ConnectorException {

    }

    @Override
    public void close() {

    }

    public RestClient<Scim2Configuration> getClient() {
        return restClient;
    }

    /**
     * @param filter
     * @return a SCIM2 filter or null when filter is not specified
     */
    static String getFilterParameter(ResultsFilter filter )
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

    /**
     * @param paginator
     * @return SCIM2 Pagination parameters or null when pagination is not specified
     */
    static String getPagingParameter(ResultsPaginator paginator)
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

    private void initializePrimaryMode(Scim2Configuration configuration) {
        if (mode == null) {
            var modeName = configuration.getMode();
            try {
                mode = Mode.valueOf(modeName);
            } catch (IllegalArgumentException e) {
                throw new ConfigurationException(
                        "Unrecognized primary mode: " + modeName);
            }
        }
    }
}
