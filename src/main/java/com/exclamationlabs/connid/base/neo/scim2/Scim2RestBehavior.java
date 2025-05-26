package com.exclamationlabs.connid.base.neo.scim2;

import com.exclamationlabs.connid.base.edition.neo.driver.rest.RestBehavior;
import com.exclamationlabs.connid.base.edition.neo.driver.rest.RestFaultHandler;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;

public class Scim2RestBehavior implements RestBehavior<Scim2Configuration> {

    @Override
    public String getName() {
        return "SCIM2";
    }

    @Override
    public boolean usesBearerAuthentication() {
        return true;
    }

    @Override
    public boolean supportsReauthentication() {
        return true;
    }

    @Override
    public RestFaultHandler<Scim2Configuration> getFaultHandler() {
        return new Scim2FaultHandler();
    }

    @Override
    public String getBaseServiceUrl(Scim2Configuration configuration) {
        return configuration.getServiceUrl();
    }
}
