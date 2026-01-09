package com.exclamationlabs.connid.base.scim2.authenticator;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.util.GuardedStringUtil;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * Enhanced OAuth2 authenticator for SCIM APIs requiring custom authentication flow.
 *
 * Authentication flow:
 * 1. Sends GET request to /authorize endpoint with:
 *    - entity-id header: the entity number
 *    - entity-token header: Basic auth (base64 encoded username:password)
 *    - uuid header: tracking UUID
 * 2. Returns the LoginToken from the response body
 */
public class EnhancedOAuth2Authenticator implements Authenticator<Scim2Configuration> {

  @Override
  public String authenticate(Scim2Configuration configuration) throws ConnectorSecurityException {
    try {
      String serviceUrl = configuration.getServiceUrl();
      String authorizeEndpoint = configuration.getAuthorizeEndpoint();

      if (StringUtils.isBlank(authorizeEndpoint)) {
        authorizeEndpoint = "/authorize";
      }

      // Build the full authorize URL
      String authorizeUrl = serviceUrl;
      if (!serviceUrl.endsWith("/") && !authorizeEndpoint.startsWith("/")) {
        authorizeUrl += "/";
      }
      authorizeUrl += authorizeEndpoint;

      HttpGet request = new HttpGet(authorizeUrl);

      // Add entity-id header
      request.setHeader("entity-id", configuration.getEntityId());

      // Add uuid header for tracking
      request.setHeader("uuid", UUID.randomUUID().toString());

      // Build Basic auth header for entity-token
      String username = configuration.getEnhancedOAuth2Username();
      String password = GuardedStringUtil.read(configuration.getEnhancedOAuth2Password());
      String auth = username + ":" + password;
      String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
      request.setHeader("entity-token", "Basic " + encodedAuth);

      // Execute request
      HttpClient client = HttpClients.createDefault();
      HttpResponse response = client.execute(request);

      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != 200) {
        String errorBody = "";
        if (response.getEntity() != null) {
          errorBody = EntityUtils.toString(response.getEntity());
        }
        throw new ConnectorSecurityException(
            "Enhanced OAuth2 authentication failed with status " + statusCode + ": " + errorBody);
      }

      // The response body is the LoginToken (a string)
      String token = EntityUtils.toString(response.getEntity());

      if (StringUtils.isBlank(token)) {
        throw new ConnectorSecurityException("Enhanced OAuth2 authentication returned empty token");
      }

      // Remove any surrounding quotes if present (JSON string format)
      token = token.trim();
      if (token.startsWith("\"") && token.endsWith("\"")) {
        token = token.substring(1, token.length() - 1);
      }

      return token;

    } catch (IOException e) {
      throw new ConnectorSecurityException(
          "Error during Enhanced OAuth2 authentication: " + e.getMessage(), e);
    }
  }
}
