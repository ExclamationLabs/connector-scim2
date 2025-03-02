package com.exclamationlabs.connid.base.scim2.driver.rest;

import com.exclamationlabs.connid.base.connector.driver.rest.BaseRestDriver;
import com.exclamationlabs.connid.base.connector.driver.rest.RestFaultProcessor;
import com.exclamationlabs.connid.base.connector.driver.rest.RestRequest;
import com.exclamationlabs.connid.base.connector.driver.rest.RestResponseData;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import com.exclamationlabs.connid.base.scim2.model.Resource;
import com.exclamationlabs.connid.base.scim2.model.Scim2Group;
import com.exclamationlabs.connid.base.scim2.model.Scim2User;
import com.exclamationlabs.connid.base.scim2.model.response.ResourceTypesResponse;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.List;
import java.util.stream.Collectors;

public class Scim2Driver extends BaseRestDriver<Scim2Configuration> {

  public Scim2Driver() {
    super();
    addInvocator(Scim2User.class, new Scim2UsersInvocator());
    addInvocator(Scim2Group.class, new Scim2GroupsInvocator());
  }

  @Override
  protected boolean usesBearerAuthorization() {
    return true;
  }

  @Override
  protected RestFaultProcessor getFaultProcessor() {
    return Scim2FaultProcessor.getInstance();
  }

  @Override
  protected String getBaseServiceUrl() {
    return getConfiguration().getServiceUrl();
  }

  @Override
  public IdentityModel getOneByName(
      Class<? extends IdentityModel> identityModelClass, String nameValue)
      throws ConnectorException {
    return this.getInvocator(identityModelClass).getOneByName(this, nameValue);
  }

  /**
   * Validate the resource types available on the SCIM2 server in order to test the connector.
   * Verify that Users and Groups are available.
   *
   * @throws ConnectorException if there is an error during the connection test
   */
  @Override
  public void test() throws ConnectorException {
    try {
      Logger.info(this, "Performing Scim2 Connector Test Procedure");
      RestResponseData<ResourceTypesResponse> rd = executeRequest(
                  new RestRequest.Builder<>(ResourceTypesResponse.class)
                      .withGet()
                      .withRequestUri("/ResourceTypes")
                      .build());
      ResourceTypesResponse response = rd.getResponseObject();
      if (response == null) {
        throw new ConnectorException("ResourceTypes response was null.");
      }
      if (response.getResources() == null || response.getResources().isEmpty()) {
        throw new ConnectorException("ResourceTypes resources is null or empty.");
      }
      List<String> resourceNames = response.getResources().stream()
          .map(Resource::getName)
          .collect(Collectors.toList());
      if (!resourceNames.contains("User")) {
          throw new ConnectorException("ResourceTypes does not contain User resource.");
      }
      if (!resourceNames.contains("Group")) {
          throw new ConnectorException("ResourceTypes does not contain Group resource.");
      }
    } catch (Exception e) {
      throw new ConnectorException("SCIM2 Connection test to detect resource types failed.", e);
    }
  }

  /*@Override
  public void initialize(
      Scim2Configuration configuration, Authenticator<Scim2Configuration> authenticator)
      throws ConnectorException {
    super();
    System.out.println("Scim2 Configuration Called 1--->  " + configuration);
  }*/

  @Override
  public void close() {}
}
