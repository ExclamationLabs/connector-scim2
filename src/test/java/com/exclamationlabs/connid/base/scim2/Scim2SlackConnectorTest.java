package com.exclamationlabs.connid.base.scim2;

import com.exclamationlabs.connid.base.connector.test.util.ConnectorMockRestTest;
import com.exclamationlabs.connid.base.connector.test.util.ConnectorTestUtils;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import com.exclamationlabs.connid.base.scim2.driver.rest.Scim2Driver;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.spi.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.exclamationlabs.connid.base.scim2.Scim2Connector.GROUP_OBJECT_CLASS;
import static com.exclamationlabs.connid.base.scim2.Scim2Connector.USER_OBJECT_CLASS;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class Scim2SlackConnectorTest extends ConnectorMockRestTest {

  // TODO: Add more tests for user and group creation, update, delete.

  private static final String SINGLE_USER_RESPONSE = "{\n" +
          "  \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\n" +
          "  \"id\": \"U1234567890\",\n" +
          "  \"externalId\": \"\",\n" +
          "  \"meta\": {\n" +
          "    \"created\": \"2020-06-25T13:48:05-07:00\",\n" +
          "    \"location\": \"https://api.example.com/scim/v2/Users/U1234567890\"\n" +
          "  },\n" +
          "  \"userName\": \"jdoe\",\n" +
          "  \"nickName\": \"jdoe\",\n" +
          "  \"name\": {\n" +
          "    \"givenName\": \"John\",\n" +
          "    \"familyName\": \"Doe\"\n" +
          "  },\n" +
          "  \"displayName\": \"John Doe\",\n" +
          "  \"profileUrl\": \"https://example.com/team/jdoe\",\n" +
          "  \"title\": \"Software Engineer\",\n" +
          "  \"timezone\": \"America/New_York\",\n" +
          "  \"active\": true,\n" +
          "  \"emails\": [\n" +
          "    {\n" +
          "      \"value\": \"jdoe@example.com\",\n" +
          "      \"primary\": true\n" +
          "    }\n" +
          "  ],\n" +
          "  \"photos\": [\n" +
          "    {\n" +
          "      \"value\": \"https://example.com/avatar/jdoe.jpg\",\n" +
          "      \"type\": \"photo\"\n" +
          "    }\n" +
          "  ],\n" +
          "  \"groups\": []\n" +
          "}";

  private static final String SINGLE_GROUP_RESPONSE =
          "{\n" +
                  "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:Group\"],\n" +
                  "      \"id\": \"G1122334455\",\n" +
                  "      \"meta\": {\n" +
                  "        \"created\": \"2024-08-23T06:43:08-07:00\",\n" +
                  "        \"location\": \"https://api.example.com/scim/v2/Groups/G1122334455\"\n" +
                  "      },\n" +
                  "      \"displayName\": \"ExampleGroup3\",\n" +
                  "      \"members\": [\n" +
                  "        {\n" +
                  "          \"value\": \"U1234567890\",\n" +
                  "          \"display\": \"John Doe\"\n" +
                  "        },\n" +
                  "        {\n" +
                  "          \"value\": \"U5566778899\",\n" +
                  "          \"display\": \"Bob Brown\"\n" +
                  "        }\n" +
                  "      ]\n" +
                  "    }";;

  protected Scim2Connector connector;

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
    configuration.setEnableSlackSchema(true);
    configuration.setToken(configuration.getCurrentToken());
    connector.init(configuration);
  }

  @Test
  public void test100Test() {
    final String responseData = "{\n" +
            "  \"schemas\" : [ \"urn:ietf:params:scim:api:messages:2.0:ListResponse\" ],\n" +
            "  \"Resources\" : [ {\n" +
            "    \"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:ResourceType\" ],\n" +
            "    \"id\" : \"User\",\n" +
            "    \"name\" : \"User\",\n" +
            "    \"endpoint\" : \"/Users\",\n" +
            "    \"description\" : \"User Account\",\n" +
            "    \"schema\" : \"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
            "    \"meta\" : {\n" +
            "      \"location\" : \"https://api.slack.com/scim/v2/ResourceTypes/User\",\n" +
            "      \"resourceType\" : \"ResourceType\"\n" +
            "    },\n" +
            "    \"schemaExtensions\" : [ {\n" +
            "      \"schema\" : \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\",\n" +
            "      \"required\" : false\n" +
            "    }, {\n" +
            "      \"schema\" : \"urn:ietf:params:scim:schemas:extension:slack:guest:2.0:User\",\n" +
            "      \"required\" : false\n" +
            "    }, {\n" +
            "      \"schema\" : \"urn:ietf:params:scim:schemas:extension:slack:directory:2.0:User\",\n" +
            "      \"required\" : false\n" +
            "    }, {\n" +
            "      \"schema\" : \"urn:ietf:params:scim:schemas:extension:slack:profile:2.0:User\",\n" +
            "      \"required\" : false\n" +
            "    } ]\n" +
            "  }, {\n" +
            "    \"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:ResourceType\" ],\n" +
            "    \"id\" : \"Group\",\n" +
            "    \"name\" : \"Group\",\n" +
            "    \"endpoint\" : \"/Groups\",\n" +
            "    \"description\" : \"Group\",\n" +
            "    \"schema\" : \"urn:ietf:params:scim:schemas:core:2.0:Group\",\n" +
            "    \"meta\" : {\n" +
            "      \"location\" : \"https://api.slack.com/scim/v2/ResourceTypes/Group\",\n" +
            "      \"resourceType\" : \"ResourceType\"\n" +
            "    }\n" +
            "  } ]\n" +
            "}";
    prepareMockResponse(responseData);
    connector.test();
  }

  @Test
  public void test101TestMissingUserType() {
    final String responseData = "{\n" +
            "  \"schemas\" : [ \"urn:ietf:params:scim:api:messages:2.0:ListResponse\" ],\n" +
            "  \"Resources\" : [ {\n" +
            "    \"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:ResourceType\" ],\n" +
            "    \"id\" : \"Group\",\n" +
            "    \"name\" : \"Group\",\n" +
            "    \"endpoint\" : \"/Groups\",\n" +
            "    \"description\" : \"Group\",\n" +
            "    \"schema\" : \"urn:ietf:params:scim:schemas:core:2.0:Group\",\n" +
            "    \"meta\" : {\n" +
            "      \"location\" : \"https://api.slack.com/scim/v2/ResourceTypes/Group\",\n" +
            "      \"resourceType\" : \"ResourceType\"\n" +
            "    }\n" +
            "  } ]\n" +
            "}";
    prepareMockResponse(responseData);
    assertThrows(ConnectorException.class, () -> connector.test());
  }

  @Test
  public void test102TestMissingGroupType() {
    final String responseData = "{\n" +
            "  \"schemas\" : [ \"urn:ietf:params:scim:api:messages:2.0:ListResponse\" ],\n" +
            "  \"Resources\" : [ {\n" +
            "    \"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:ResourceType\" ],\n" +
            "    \"id\" : \"User\",\n" +
            "    \"name\" : \"User\",\n" +
            "    \"endpoint\" : \"/Users\",\n" +
            "    \"description\" : \"User Account\",\n" +
            "    \"schema\" : \"urn:ietf:params:scim:schemas:core:2.0:User\",\n" +
            "    \"meta\" : {\n" +
            "      \"location\" : \"https://api.slack.com/scim/v2/ResourceTypes/User\",\n" +
            "      \"resourceType\" : \"ResourceType\"\n" +
            "    },\n" +
            "    \"schemaExtensions\" : [ {\n" +
            "      \"schema\" : \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\",\n" +
            "      \"required\" : false\n" +
            "    }, {\n" +
            "      \"schema\" : \"urn:ietf:params:scim:schemas:extension:slack:guest:2.0:User\",\n" +
            "      \"required\" : false\n" +
            "    }, {\n" +
            "      \"schema\" : \"urn:ietf:params:scim:schemas:extension:slack:directory:2.0:User\",\n" +
            "      \"required\" : false\n" +
            "    }, {\n" +
            "      \"schema\" : \"urn:ietf:params:scim:schemas:extension:slack:profile:2.0:User\",\n" +
            "      \"required\" : false\n" +
            "    } ]\n" +
            "  } ]\n" +
            "}";
    prepareMockResponse(responseData);
    assertThrows(ConnectorException.class, () -> connector.test());
  }

  @Test
  public void test105Schema() {
    assertNotNull(connector.schema());
  }


  @Test
  public void test130UsersGet() {
    final String responseData = "{\n" +
            "  \"schemas\": [\"urn:ietf:params:scim:api:messages:2.0:ListResponse\"],\n" +
            "  \"Resources\": [\n" +
            "    {\n" +
            "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\n" +
            "      \"id\": \"U1234567890\",\n" +
            "      \"externalId\": \"\",\n" +
            "      \"meta\": {\n" +
            "        \"created\": \"2020-06-25T13:48:05-07:00\",\n" +
            "        \"location\": \"https://api.example.com/scim/v2/Users/U1234567890\"\n" +
            "      },\n" +
            "      \"userName\": \"jdoe\",\n" +
            "      \"nickName\": \"jdoe\",\n" +
            "      \"name\": {\n" +
            "        \"givenName\": \"John\",\n" +
            "        \"familyName\": \"Doe\"\n" +
            "      },\n" +
            "      \"displayName\": \"John Doe\",\n" +
            "      \"profileUrl\": \"https://example-dev.example.com/team/jdoe\",\n" +
            "      \"title\": \"Software Engineer\",\n" +
            "      \"timezone\": \"America/New_York\",\n" +
            "      \"active\": true,\n" +
            "      \"emails\": [\n" +
            "        {\n" +
            "          \"value\": \"jdoe@example.com\",\n" +
            "          \"primary\": true\n" +
            "        }\n" +
            "      ],\n" +
            "      \"photos\": [\n" +
            "        {\n" +
            "          \"type\": \"photo\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"groups\": []\n" +
            "    },\n" +
            "    {\n" +
            "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\n" +
            "      \"id\": \"U0987654321\",\n" +
            "      \"externalId\": \"\",\n" +
            "      \"meta\": {\n" +
            "        \"created\": \"2020-06-30T08:50:13-07:00\",\n" +
            "        \"location\": \"https://api.example.com/scim/v2/Users/U0987654321\"\n" +
            "      },\n" +
            "      \"userName\": \"asmith\",\n" +
            "      \"nickName\": \"asmith\",\n" +
            "      \"name\": {\n" +
            "        \"givenName\": \"Alice\",\n" +
            "        \"familyName\": \"Smith\"\n" +
            "      },\n" +
            "      \"displayName\": \"Alice Smith\",\n" +
            "      \"profileUrl\": \"https://example-dev.example.com/team/asmith\",\n" +
            "      \"title\": \"Project Manager\",\n" +
            "      \"timezone\": \"America/Denver\",\n" +
            "      \"active\": true,\n" +
            "      \"emails\": [\n" +
            "        {\n" +
            "          \"value\": \"asmith@example.com\",\n" +
            "          \"primary\": true\n" +
            "        }\n" +
            "      ],\n" +
            "      \"photos\": [\n" +
            "        {\n" +
            "          \"value\": \"https://secure.gravatar.com/avatar/abcdef1234567890abcdef1234567890.jpg?s=192&d=https%3A%2F%2Fa.example.com%2Fimg%2Favatars%2Fava_0011-192.png\",\n" +
            "          \"type\": \"photo\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"groups\": []\n" +
            "    },\n" +
            "    {\n" +
            "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\n" +
            "      \"id\": \"U1122334455\",\n" +
            "      \"externalId\": \"\",\n" +
            "      \"meta\": {\n" +
            "        \"created\": \"2020-06-30T11:03:58-07:00\",\n" +
            "        \"location\": \"https://api.example.com/scim/v2/Users/U1122334455\"\n" +
            "      },\n" +
            "      \"userName\": \"bwhite\",\n" +
            "      \"nickName\": \"bwhite\",\n" +
            "      \"name\": {\n" +
            "        \"givenName\": \"Bob\",\n" +
            "        \"familyName\": \"White\"\n" +
            "      },\n" +
            "      \"displayName\": \"Bob White\",\n" +
            "      \"profileUrl\": \"https://example-dev.example.com/team/bwhite\",\n" +
            "      \"title\": \"Designer\",\n" +
            "      \"timezone\": \"America/New_York\",\n" +
            "      \"active\": true,\n" +
            "      \"emails\": [\n" +
            "        {\n" +
            "          \"value\": \"bwhite@example.com\",\n" +
            "          \"primary\": true\n" +
            "        }\n" +
            "      ],\n" +
            "      \"photos\": [\n" +
            "        {\n" +
            "          \"value\": \"https://secure.gravatar.com/avatar/1234abcd5678efgh1234abcd5678efgh.jpg?s=192&d=https%3A%2F%2Fa.example.com%2Fimg%2Favatars%2Fava_0017-192.png\",\n" +
            "          \"type\": \"photo\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"groups\": []\n" +
            "    },\n" +
            "    {\n" +
            "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\n" +
            "      \"id\": \"U2233445566\",\n" +
            "      \"externalId\": \"\",\n" +
            "      \"meta\": {\n" +
            "        \"created\": \"2020-06-30T08:36:05-07:00\",\n" +
            "        \"location\": \"https://api.example.com/scim/v2/Users/U2233445566\"\n" +
            "      },\n" +
            "      \"userName\": \"mho\",\n" +
            "      \"nickName\": \"mho\",\n" +
            "      \"name\": {\n" +
            "        \"givenName\": \"Ming\",\n" +
            "        \"familyName\": \"Ho\"\n" +
            "      },\n" +
            "      \"displayName\": \"Ming Ho\",\n" +
            "      \"profileUrl\": \"https://example-dev.example.com/team/mho\",\n" +
            "      \"title\": \"Data Scientist\",\n" +
            "      \"timezone\": \"America/New_York\",\n" +
            "      \"active\": true,\n" +
            "      \"emails\": [\n" +
            "        {\n" +
            "          \"value\": \"mho@example.com\",\n" +
            "          \"primary\": true\n" +
            "        }\n" +
            "      ],\n" +
            "      \"photos\": [\n" +
            "        {\n" +
            "          \"value\": \"https://secure.gravatar.com/avatar/5678efgh1234abcd5678efgh1234abcd.jpg?s=192&d=https%3A%2F%2Fa.example.com%2Fimg%2Favatars%2Fava_0025-192.png\",\n" +
            "          \"type\": \"photo\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"groups\": []\n" +
            "    },\n" +
            "    {\n" +
            "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\n" +
            "      \"id\": \"U3344556677\",\n" +
            "      \"externalId\": \"\",\n" +
            "      \"meta\": {\n" +
            "        \"created\": \"2020-06-30T09:02:33-07:00\",\n" +
            "        \"location\": \"https://api.example.com/scim/v2/Users/U3344556677\"\n" +
            "      },\n" +
            "      \"userName\": \"jira_bot\",\n" +
            "      \"nickName\": \"jira_bot\",\n" +
            "      \"name\": {\n" +
            "        \"givenName\": \"Jira\",\n" +
            "        \"familyName\": \"Bot\"\n" +
            "      },\n" +
            "      \"displayName\": \"Jira Bot\",\n" +
            "      \"profileUrl\": \"https://example-dev.example.com/team/jira_bot\",\n" +
            "      \"title\": \"Automation Bot\",\n" +
            "      \"timezone\": \"America/Los_Angeles\",\n" +
            "      \"active\": false,\n" +
            "      \"emails\": [\n" +
            "        {\n" +
            "          \"value\": \"botuser-1234-5678@example-bots.com\",\n" +
            "          \"primary\": true\n" +
            "        }\n" +
            "      ],\n" +
            "      \"photos\": [\n" +
            "        {\n" +
            "          \"value\": \"https://avatars.example.com/2020-06-30/1234567890abcdef1234567890abcdef_192.png\",\n" +
            "          \"type\": \"photo\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"groups\": []\n" +
            "    },\n" +
            "    {\n" +
            "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\n" +
            "      \"id\": \"U4455667788\",\n" +
            "      \"externalId\": \"\",\n" +
            "      \"meta\": {\n" +
            "        \"created\": \"2020-07-01T05:48:41-07:00\",\n" +
            "        \"location\": \"https://api.example.com/scim/v2/Users/U4455667788\"\n" +
            "      },\n" +
            "      \"userName\": \"pcaskey\",\n" +
            "      \"nickName\": \"pcaskey\",\n" +
            "      \"name\": {\n" +
            "        \"givenName\": \"Paul\",\n" +
            "        \"familyName\": \"Caskey\"\n" +
            "      },\n" +
            "      \"displayName\": \"Paul Caskey\",\n" +
            "      \"profileUrl\": \"https://example-dev.example.com/team/pcaskey\",\n" +
            "      \"title\": \"Network Engineer\",\n" +
            "      \"timezone\": \"America/Chicago\",\n" +
            "      \"active\": true,\n" +
            "      \"emails\": [\n" +
            "        {\n" +
            "          \"value\": \"pcaskey@example.com\",\n" +
            "          \"primary\": true\n" +
            "        }\n" +
            "      ],\n" +
            "      \"photos\": [\n" +
            "        {\n" +
            "          \"value\": \"https://secure.gravatar.com/avatar/abcdef1234567890abcdef1234567890.jpg?s=192&d=https%3A%2F%2Fa.example.com%2Fimg%2Favatars%2Fava_0019-192.png\",\n" +
            "          \"type\": \"photo\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"groups\": []\n" +
            "    },\n" +
            "    {\n" +
            "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\n" +
            "      \"id\": \"U5566778899\",\n" +
            "      \"externalId\": \"\",\n" +
            "      \"meta\": {\n" +
            "        \"created\": \"2020-06-25T13:40:07-07:00\",\n" +
            "        \"location\": \"https://api.example.com/scim/v2/Users/U5566778899\"\n" +
            "      },\n" +
            "      \"userName\": \"sjeanes\",\n" +
            "      \"nickName\": \"sjeanes\",\n" +
            "      \"name\": {\n" +
            "        \"givenName\": \"Sam\",\n" +
            "        \"familyName\": \"Jeanes\"\n" +
            "      },\n" +
            "      \"displayName\": \"Sam Jeanes\",\n" +
            "      \"profileUrl\": \"https://example-dev.example.com/team/sjeanes\",\n" +
            "      \"title\": \"DevOps Engineer\",\n" +
            "      \"timezone\": \"America/Denver\",\n" +
            "      \"active\": true,\n" +
            "      \"emails\": [\n" +
            "        {\n" +
            "          \"value\": \"sjeanes@example.com\",\n" +
            "          \"primary\": true\n" +
            "        }\n" +
            "      ],\n" +
            "      \"photos\": [\n" +
            "        {\n" +
            "          \"value\": \"https://secure.gravatar.com/avatar/1234567890abcdef1234567890abcdef.jpg?s=192&d=https%3A%2F%2Fa.example.com%2Fimg%2Favatars%2Fava_0008-192.png\",\n" +
            "          \"type\": \"photo\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"groups\": []\n" +
            "    },\n" +
            "    {\n" +
            "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\n" +
            "      \"id\": \"U6677889900\",\n" +
            "      \"externalId\": \"\",\n" +
            "      \"meta\": {\n" +
            "        \"created\": \"2020-07-06T12:05:20-07:00\",\n" +
            "        \"location\": \"https://api.example.com/scim/v2/Users/U6677889900\"\n" +
            "      },\n" +
            "      \"userName\": \"calendar_bot\",\n" +
            "      \"nickName\": \"calendar_bot\",\n" +
            "      \"name\": {\n" +
            "        \"givenName\": \"Calendar\",\n" +
            "        \"familyName\": \"Bot\"\n" +
            "      },\n" +
            "      \"displayName\": \"Calendar Bot\",\n" +
            "      \"profileUrl\": \"https://example-dev.example.com/team/calendar_bot\",\n" +
            "      \"title\": \"Automation Bot\",\n" +
            "      \"timezone\": \"America/Los_Angeles\",\n" +
            "      \"active\": true,\n" +
            "      \"emails\": [\n" +
            "        {\n" +
            "          \"value\": \"botuser-5678-1234@example-bots.com\",\n" +
            "          \"primary\": true\n" +
            "        }\n" +
            "      ],\n" +
            "      \"photos\": [\n" +
            "        {\n" +
            "          \"value\": \"https://avatars.example.com/2020-07-06/abcdef1234567890abcdef1234567890_192.png\",\n" +
            "          \"type\": \"photo\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"groups\": []\n" +
            "    },\n" +
            "    {\n" +
            "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\"],\n" +
            "      \"id\": \"U7788990011\",\n" +
            "      \"externalId\": \"\",\n" +
            "      \"meta\": {\n" +
            "        \"created\": \"2020-07-07T11:03:49-07:00\",\n" +
            "        \"location\": \"https://api.example.com/scim/v2/Users/U7788990011\"\n" +
            "      },\n" +
            "      \"userName\": \"jira_server\",\n" +
            "      \"nickName\": \"jira_server\",\n" +
            "      \"name\": {\n" +
            "        \"givenName\": \"Jira\",\n" +
            "        \"familyName\": \"Server\"\n" +
            "      },\n" +
            "      \"displayName\": \"Jira Server\",\n" +
            "      \"profileUrl\": \"https://example-dev.example.com/team/jira_server\",\n" +
            "      \"title\": \"Automation Bot\",\n" +
            "      \"timezone\": \"America/Los_Angeles\",\n" +
            "      \"active\": true,\n" +
            "      \"emails\": [\n" +
            "        {\n" +
            "          \"value\": \"botuser-1234-5678@example-bots.com\",\n" +
            "          \"primary\": true\n" +
            "        }\n" +
            "      ],\n" +
            "      \"photos\": [\n" +
            "        {\n" +
            "          \"value\": \"https://avatars.example.com/2020-07-07/abcdef1234567890abcdef1234567890_192.png\",\n" +
            "          \"type\": \"photo\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"groups\": []\n" +
            "    }],\n" +
            "  \"totalResults\" : 9,\n" +
            "  \"itemsPerPage\" : 10,\n" +
            "  \"startIndex\" : 1\n" +
            "}";

    prepareMockResponse(responseData);

    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);
    ObjectClass oClass = new ObjectClass(USER_OBJECT_CLASS);
    connector.executeQuery(oClass, "", resultsHandler, new OperationOptionsBuilder().build());
    assertEquals(9, idValues.size());
    assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
  }

  @Test
  public void test140UserGet() {
    prepareMockResponse(SINGLE_USER_RESPONSE);
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);
    ObjectClass oClass = new ObjectClass(USER_OBJECT_CLASS);
    connector.executeQuery(oClass, "U1234567890", resultsHandler, new OperationOptionsBuilder().build());
    assertEquals(1, idValues.size());
    assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    assertEquals("U1234567890", idValues.get(0));
    assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    assertEquals("jdoe", nameValues.get(0));
  }

  @Test
  public void test145UserGetByName() {
    Attribute name =
            new AttributeBuilder().setName(Name.NAME).addValue("jdoe").build();
    prepareMockResponse("{\n" +
            "  \"schemas\" : [ \"urn:ietf:params:scim:api:messages:2.0:ListResponse\" ],\n" +
            "  \"Resources\" : [ \n" + SINGLE_USER_RESPONSE +
            "\n" +
            "],\n" +
            "  \"totalResults\" : 1,\n" +
            "  \"itemsPerPage\" : 1,\n" +
            "  \"startIndex\" : 1\n" +
            "}");
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);
    ObjectClass oClass = new ObjectClass(USER_OBJECT_CLASS);
    connector.executeQuery(oClass, new EqualsFilter(name), resultsHandler, new OperationOptionsBuilder().build());
    assertEquals(1, idValues.size());
    assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    assertEquals("U1234567890", idValues.get(0));
    assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    assertEquals("jdoe", nameValues.get(0));
  }

  @Test
  public void test230GroupsGet() {
    final String responseData =
        "{\n" +
                "  \"schemas\": [\"urn:ietf:params:scim:api:messages:2.0:ListResponse\"],\n" +
                "  \"Resources\": [\n" +
                "    {\n" +
                "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:Group\"],\n" +
                "      \"id\": \"G1234567890\",\n" +
                "      \"meta\": {\n" +
                "        \"created\": \"2024-08-16T08:16:12-07:00\",\n" +
                "        \"location\": \"https://api.example.com/scim/v2/Groups/G1234567890\"\n" +
                "      },\n" +
                "      \"displayName\": \"ExampleGroup1\",\n" +
                "      \"members\": [\n" +
                "        {\n" +
                "          \"value\": \"U1234567890\",\n" +
                "          \"display\": \"John Doe\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"U0987654321\",\n" +
                "          \"display\": \"Jane Smith\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:Group\"],\n" +
                "      \"id\": \"G0987654321\",\n" +
                "      \"meta\": {\n" +
                "        \"created\": \"2024-08-16T07:55:54-07:00\",\n" +
                "        \"location\": \"https://api.example.com/scim/v2/Groups/G0987654321\"\n" +
                "      },\n" +
                "      \"displayName\": \"ExampleGroup2\",\n" +
                "      \"members\": [\n" +
                "        {\n" +
                "          \"display\": \"Alice Johnson\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"U5566778899\",\n" +
                "          \"display\": \"Bob Brown\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:Group\"],\n" +
                "      \"id\": \"G1122334455\",\n" +
                "      \"meta\": {\n" +
                "        \"created\": \"2024-08-23T06:43:08-07:00\",\n" +
                "        \"location\": \"https://api.example.com/scim/v2/Groups/G1122334455\"\n" +
                "      },\n" +
                "      \"displayName\": \"ExampleGroup3\",\n" +
                "      \"members\": [\n" +
                "        {\n" +
                "          \"value\": \"U1234567890\",\n" +
                "          \"display\": \"John Doe\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"U5566778899\",\n" +
                "          \"display\": \"Bob Brown\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"totalResults\": 3,\n" +
                "  \"itemsPerPage\": 3,\n" +
                "  \"startIndex\": 1\n" +
                "}";
    prepareMockResponse(responseData);
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ObjectClass gClass = new ObjectClass(GROUP_OBJECT_CLASS);
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

    connector.executeQuery(gClass, "", resultsHandler, new OperationOptionsBuilder().build());
    assertEquals(3, idValues.size());
    assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
  }

  @Test
  public void test240GroupGet() {

    prepareMockResponse(SINGLE_GROUP_RESPONSE);
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ObjectClass gClass = new ObjectClass(GROUP_OBJECT_CLASS);
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

    connector.executeQuery(gClass, "G1122334455", resultsHandler, new OperationOptionsBuilder().build());
    assertEquals(1, idValues.size());
    assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    assertEquals("G1122334455", idValues.get(0));
  }


  @Test
  public void test245GroupGetByName() {
    Attribute name =
            new AttributeBuilder().setName(Name.NAME).addValue("mygroup").build();

    prepareMockResponse("{\n" +
            "  \"schemas\" : [ \"urn:ietf:params:scim:api:messages:2.0:ListResponse\" ],\n" +
            "  \"Resources\" : [ \n" + SINGLE_GROUP_RESPONSE +
            "\n" +
            "],\n" +
            "  \"totalResults\" : 1,\n" +
            "  \"itemsPerPage\" : 1,\n" +
            "  \"startIndex\" : 1\n" +
            "}");
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);
    ObjectClass oClass = new ObjectClass(GROUP_OBJECT_CLASS);
    connector.executeQuery(oClass, new EqualsFilter(name), resultsHandler, new OperationOptionsBuilder().build());
    assertEquals(1, idValues.size());
    assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    assertEquals("G1122334455", idValues.get(0));
    assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    assertEquals("ExampleGroup3", nameValues.get(0));
  }
}
