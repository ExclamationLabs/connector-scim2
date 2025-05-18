package com.exclamationlabs.connid.base.neo.scim2;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.DirectAccessTokenAuthenticator;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

public class Scim2Authenticator implements Authenticator<Scim2Configuration> {

    @Override
    public String authenticate(Scim2Configuration scim2Configuration) throws ConnectorSecurityException {
        DirectAccessTokenAuthenticator directAuthenticator = new DirectAccessTokenAuthenticator();
        return directAuthenticator.authenticate(scim2Configuration);
    }
}
