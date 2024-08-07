package com.exclamationlabs.connid.base.scim2.driver.rest;

import com.exclamationlabs.connid.base.connector.driver.rest.BaseRestDriver;
import com.exclamationlabs.connid.base.connector.driver.rest.RestFaultProcessor;
import com.exclamationlabs.connid.base.connector.driver.rest.RestRequest;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import com.exclamationlabs.connid.base.scim2.model.Scim2Group;
import com.exclamationlabs.connid.base.scim2.model.Scim2User;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class Scim2Driver extends BaseRestDriver<Scim2Configuration> {

  public Scim2Driver() {
    super();
    // addInvocator(Scim2SlackUser.class, new Scim2SlackUsersInvocator());

    addInvocator(Scim2User.class, new Scim2UsersInvocator());
    addInvocator(Scim2Group.class, new Scim2GroupsInvocator());
    // addInvocator(Scim2SlackUser.class, new Scim2SlackUsersInvocator());
    // addInvocator(Scim2SlackGroup.class, new Scim2SlackGroupInvocator());

    /*if(getConfiguration().getEnableSlackSchema()){
      addInvocator(Scim2SlackUser.class, new Scim2SlackUsersInvocator());
      addInvocator(Scim2Group.class, new Scim2SlackGroupInvocator());
    }else{
      addInvocator(Scim2User.class, new Scim2UsersInvocator());
      addInvocator(Scim2Group.class, new Scim2GroupsInvocator());
    }*/

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

  @Override
  public void test() throws ConnectorException {
    try {
      Logger.info(this, "Performing Scim2 Connector Test Procedure");
      String adminUser =
          executeRequest(
                  new RestRequest.Builder<>(String.class)
                      .withGet()
                      .withRequestUri("/Users")
                      .build())
              .getResponseObject();
      // if (adminUser == null || adminUser.getId() == null) {
      //  throw new ConnectorException("Invalid admin user response");
      // }
      System.out.println("yada yada " + adminUser);
    } catch (Exception e) {
      throw new ConnectorException("Test for Slack connection user failed.", e);
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
