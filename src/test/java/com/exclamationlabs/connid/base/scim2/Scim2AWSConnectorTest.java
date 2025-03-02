package com.exclamationlabs.connid.base.scim2;

import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import com.exclamationlabs.connid.base.scim2.driver.rest.Scim2Driver;
import org.apache.http.client.HttpClient;
import org.identityconnectors.framework.spi.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class Scim2AWSConnectorTest extends Scim2SlackConnectorTest {

  @BeforeEach
  public void setup() {
    connector =
        new Scim2Connector() {
          @Override
          public void init(Configuration configuration) {
            setAuthenticator(null);
            setDriver(
                new Scim2Driver() {
                  @Override
                  protected HttpClient createClient() {
                    return stubClient;
                  }
                });
            super.init(configuration);
          }
        };
    Scim2Configuration configuration = new Scim2Configuration();
    configuration.setServiceUrl("test");
    configuration.setCurrentToken("token");
    configuration.setEnableSlackSchema(false);
    configuration.setEnableAWSSchema(true);
    configuration.setToken(configuration.getCurrentToken());
    connector.init(configuration);
  }

}
