package com.exclamationlabs.connid.base.scim2.configuration;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationInfo;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationReader;
import org.identityconnectors.framework.common.objects.ConnectorMessages;
import org.identityconnectors.framework.spi.ConfigurationClass;
import org.identityconnectors.framework.spi.ConfigurationProperty;
import org.identityconnectors.common.security.GuardedString;
import javax.validation.constraints.*;
import java.util.*;

import com.exclamationlabs.connid.base.connector.configuration.basetypes.RestConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.ResultsConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.ServiceConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2ClientCredentialsConfiguration;

/**
* This class was automatically generated by connector-base-config-plugin.
* It is not ideal to modify this file, as subsequent builds of this project will overlay
* your changes in this file.  Instead, modify the configuration.structure.yml in this project.
*/
@ConfigurationClass(skipUnsupported = true, ignore={"active", "name", "source", "currentToken", "oauth2Information"})
public class Scim2Configuration implements ConnectorConfiguration, RestConfiguration, ResultsConfiguration, ServiceConfiguration, Oauth2ClientCredentialsConfiguration {

    protected ConnectorMessages connectorMessages;

    @ConfigurationInfo(path="source", internal=true)
    protected String source;

    @ConfigurationInfo(path="name", internal=true)
    protected String name;

    @ConfigurationInfo(path="active", internal=true)
    protected Boolean active;

    @ConfigurationInfo(path="currentToken", internal=true)
    protected String currentToken;

    @ConfigurationInfo(path="results.deepGet")
    private Boolean deepGet;

    @NotBlank(message="userSchemaIdList cannot be blank")
    @ConfigurationInfo(path="custom.userSchemaIdList")
    private String userSchemaIdList;

    @ConfigurationInfo(path="results.pagination")
    private Boolean pagination;

    @NotBlank(message="tokenUrl cannot be blank")
    @ConfigurationInfo(path="security.authenticator.oauth2ClientCredentials.tokenUrl")
    private String tokenUrl;

    @NotBlank(message="serviceUrl cannot be blank")
    @ConfigurationInfo(path="service.serviceUrl")
    private String serviceUrl;

    @NotNull(message="importBatchSize cannot be null")
    @ConfigurationInfo(path="results.importBatchSize")
    private Integer importBatchSize = 50;

    @NotBlank(message="groupSchemaIDList cannot be blank")
    @ConfigurationInfo(path="custom.groupSchemaIDList")
    private String groupSchemaIDList;

    @NotNull(message="enableStandardSchema cannot be null")
    @ConfigurationInfo(path="custom.enableStandardSchema")
    private Boolean enableStandardSchema = false;

    @ConfigurationInfo(path="security.authenticator.oauth2ClientCredentials.oauth2Information", internal=true)
    private Map<String,String> oauth2Information;

    @ConfigurationInfo(path="service.duplicateErrorReturnsId")
    private Boolean duplicateErrorReturnsId = false;

    @NotBlank(message="schemaRawJson cannot be blank")
    @ConfigurationInfo(path="custom.schemaRawJson")
    private String schemaRawJson;

    @ConfigurationInfo(path="security.authenticator.oauth2ClientCredentials.scope")
    private String scope;

    @NotBlank(message="resourceTypeUrl cannot be blank")
    @ConfigurationInfo(path="custom.resourceTypeUrl")
    private String resourceTypeUrl;

    @NotNull(message="clientSecret cannot be null")
    @ConfigurationInfo(path="security.authenticator.oauth2ClientCredentials.clientSecret")
    private GuardedString clientSecret;

    @NotBlank(message="clientId cannot be blank")
    @ConfigurationInfo(path="security.authenticator.oauth2ClientCredentials.clientId")
    private String clientId;

    @NotNull(message="enableDynamicSchema cannot be null")
    @ConfigurationInfo(path="custom.enableDynamicSchema")
    private Boolean enableDynamicSchema = false;

    @NotBlank(message="usersEndpointUrl cannot be blank")
    @ConfigurationInfo(path="custom.usersEndpointUrl")
    private String usersEndpointUrl;

    @NotBlank(message="resourceTypeRawJson cannot be blank")
    @ConfigurationInfo(path="custom.resourceTypeRawJson")
    private String resourceTypeRawJson;

    @NotBlank(message="schemaUrl cannot be blank")
    @ConfigurationInfo(path="custom.schemaUrl")
    private String schemaUrl;

    @NotNull(message="enableAWSSchema cannot be null")
    @ConfigurationInfo(path="custom.enableAWSSchema")
    private Boolean enableAWSSchema = false;

    @NotNull(message="enableSlackSchema cannot be null")
    @ConfigurationInfo(path="custom.enableSlackSchema")
    private Boolean enableSlackSchema = false;

    @NotBlank(message="accountId cannot be blank")
    @ConfigurationInfo(path="custom.accountId")
    private String accountId;

    @Min(1)
    @Max(100)
    @ConfigurationInfo(path="rest.ioErrorRetries")
    private Integer ioErrorRetries = 5;

    @ConfigurationInfo(path="results.deepImport")
    private Boolean deepImport;

    @NotNull(message="useResourceTypeUrl cannot be null")
    @ConfigurationInfo(path="custom.useResourceTypeUrl")
    private Boolean useResourceTypeUrl = false;

    @NotBlank(message="groupEndpointUrl cannot be blank")
    @ConfigurationInfo(path="custom.groupEndpointUrl")
    private String groupEndpointUrl;

    @NotNull(message="enableEnterpriseUser cannot be null")
    @ConfigurationInfo(path="custom.enableEnterpriseUser")
    private Boolean enableEnterpriseUser = false;

    @NotNull(message="useSchemaUrl cannot be null")
    @ConfigurationInfo(path="custom.useSchemaUrl")
    private Boolean useSchemaUrl = false;


    public Scim2Configuration() {
        source = "default";
        name = "default";
        active = true;
    }

    public Scim2Configuration(String configurationName) {
        name = configurationName;
        active = true;
        ConfigurationReader.prepareTestConfiguration(this);
    }

    @ConfigurationProperty(
    displayMessageKey = "Deep Get Enabled",
    helpMessageKey = "If true, an individual getOne request for each item in any getAll request will be performed.",
    order = 1101,
    confidential = false,
    required = false)
    public Boolean getDeepGet() {
        return this.deepGet;
    }

    public void setDeepGet(Boolean input) {
        this.deepGet = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "User SchemaId List",
    helpMessageKey = "A list of user schemas that define a user. This is discoverable from the Resource Type URL, JSON, or by one of the prebuilt java classes",
    order = 830,
    confidential = false,
    required = true)
    public String getUserSchemaIdList() {
        return this.userSchemaIdList;
    }

    public void setUserSchemaIdList(String input) {
        this.userSchemaIdList = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Pagination Enabled",
    helpMessageKey = "Set to true if this connector (and its underlying API) supports pagination.",
    order = 1104,
    confidential = false,
    required = false)
    public Boolean getPagination() {
        return this.pagination;
    }

    public void setPagination(Boolean input) {
        this.pagination = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "OAuth2 Token URL",
    helpMessageKey = "URL used to obtain OAuth2 token",
    order = 2901,
    confidential = false,
    required = true)
    public String getTokenUrl() {
        return this.tokenUrl;
    }

    public void setTokenUrl(String input) {
        this.tokenUrl = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Service URL",
    helpMessageKey = "The base service URL for the API this connector needs to access to get or store data.",
    order = 1201,
    confidential = false,
    required = true)
    public String getServiceUrl() {
        return this.serviceUrl;
    }

    public void setServiceUrl(String input) {
        this.serviceUrl = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Import Batch Size",
    helpMessageKey = "If supplied, import operations will be invoked using this given batch size, so that API`s that support paging can import all records using a particular batch size (instead  of all at once.",
    order = 1103,
    confidential = false,
    required = true)
    public Integer getImportBatchSize() {
        return this.importBatchSize;
    }

    public void setImportBatchSize(Integer input) {
        this.importBatchSize = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Group SchemaId List",
    helpMessageKey = "A list of Group schemas that define a Group. This is discoverable from the Resource Type URL or JSON. or by one of the prebuilt java classes ",
    order = 840,
    confidential = false,
    required = true)
    public String getGroupSchemaIDList() {
        return this.groupSchemaIDList;
    }

    public void setGroupSchemaIDList(String input) {
        this.groupSchemaIDList = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Enable Standard Schema",
    helpMessageKey = "Uses prebuilt java objects based on the stand schema. ",
    order = 760,
    confidential = false,
    required = true)
    public Boolean getEnableStandardSchema() {
        return this.enableStandardSchema;
    }

    public void setEnableStandardSchema(Boolean input) {
        this.enableStandardSchema = input;
    }

    public Map<String,String> getOauth2Information() {
        return this.oauth2Information;
    }

    public void setOauth2Information(Map<String,String> input) {
        this.oauth2Information = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Duplicate Error Returns Id",
    helpMessageKey = "When a create is attempted and an AlreadyExistsException is generated by the driver/invocator, the adapter shall attempt to call getOneByName() driver/invocator method to return the id of the existing record matching the current name value.",
    order = 1202,
    confidential = false,
    required = false)
    public Boolean getDuplicateErrorReturnsId() {
        return this.duplicateErrorReturnsId;
    }

    public void setDuplicateErrorReturnsId(Boolean input) {
        this.duplicateErrorReturnsId = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Schema JSON",
    helpMessageKey = "The actual Schema return for a particular service provider. This can be populated from the URL at discovery time.",
    order = 710,
    confidential = false,
    required = true)
    public String getSchemaRawJson() {
        return this.schemaRawJson;
    }

    public void setSchemaRawJson(String input) {
        this.schemaRawJson = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "OAuth2 Scope",
    helpMessageKey = "OAuth2 Scope (not used for some implementations)",
    order = 2904,
    confidential = false,
    required = false)
    public String getScope() {
        return this.scope;
    }

    public void setScope(String input) {
        this.scope = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Resource Type URL",
    helpMessageKey = "URL to Discover resource type for a particular service provider. These included Users and Groups URL endpoints. ",
    order = 730,
    confidential = false,
    required = true)
    public String getResourceTypeUrl() {
        return this.resourceTypeUrl;
    }

    public void setResourceTypeUrl(String input) {
        this.resourceTypeUrl = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "OAuth2 Client Secret",
    helpMessageKey = "OAuth2 Client Secret",
    order = 2903,
    confidential = true,
    required = true)
    public GuardedString getClientSecret() {
        return this.clientSecret;
    }

    public void setClientSecret(GuardedString input) {
        this.clientSecret = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "OAuth2 Client Id",
    helpMessageKey = "OAuth2 Client Id",
    order = 2902,
    confidential = true,
    required = true)
    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String input) {
        this.clientId = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Enable Dynamic Schema",
    helpMessageKey = "Use the Resource Type and/or the Schema defined ",
    order = 800,
    confidential = false,
    required = true)
    public Boolean getEnableDynamicSchema() {
        return this.enableDynamicSchema;
    }

    public void setEnableDynamicSchema(Boolean input) {
        this.enableDynamicSchema = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Users Endpoint URL",
    helpMessageKey = "Discovered from the resource type or entered manually",
    order = 810,
    confidential = false,
    required = true)
    public String getUsersEndpointUrl() {
        return this.usersEndpointUrl;
    }

    public void setUsersEndpointUrl(String input) {
        this.usersEndpointUrl = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Resource Type JSON",
    helpMessageKey = "The actual resource types for a particular service provider",
    order = 740,
    confidential = false,
    required = true)
    public String getResourceTypeRawJson() {
        return this.resourceTypeRawJson;
    }

    public void setResourceTypeRawJson(String input) {
        this.resourceTypeRawJson = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Schema URL",
    helpMessageKey = "URL to discover schema for a particular service provider",
    order = 701,
    confidential = false,
    required = true)
    public String getSchemaUrl() {
        return this.schemaUrl;
    }

    public void setSchemaUrl(String input) {
        this.schemaUrl = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Enable AWS Schema",
    helpMessageKey = "Use a pre-built java objects as defined for AWS As specified here https://docs.aws.amazon.com/singlesignon/latest/developerguide/what-is-scim.html",
    order = 780,
    confidential = false,
    required = true)
    public Boolean getEnableAWSSchema() {
        return this.enableAWSSchema;
    }

    public void setEnableAWSSchema(Boolean input) {
        this.enableAWSSchema = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Enable Slack Schema",
    helpMessageKey = "Use prebuilt java classes as define for Slack as specified here: https://api.slack.com/admins/scim2",
    order = 790,
    confidential = false,
    required = true)
    public Boolean getEnableSlackSchema() {
        return this.enableSlackSchema;
    }

    public void setEnableSlackSchema(Boolean input) {
        this.enableSlackSchema = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Zoom Account Id",
    helpMessageKey = "Zoom Account Id required for authentication.",
    order = 701,
    confidential = false,
    required = true)
    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String input) {
        this.accountId = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "IO Error Retries",
    helpMessageKey = "If IO Error occurs during API invocation, this number of retries will be attempted before giving up",
    order = 1001,
    confidential = false,
    required = false)
    public Integer getIoErrorRetries() {
        return this.ioErrorRetries;
    }

    public void setIoErrorRetries(Integer input) {
        this.ioErrorRetries = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Deep Import Enabled",
    helpMessageKey = "If true, an individual getOne request for each item in Import getAll requests will be performed.",
    order = 1102,
    confidential = false,
    required = false)
    public Boolean getDeepImport() {
        return this.deepImport;
    }

    public void setDeepImport(Boolean input) {
        this.deepImport = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Use Resource Type URL",
    helpMessageKey = "Whether to use the URL or the JSON. Especially when the URL is not supported by the service provider.",
    order = 750,
    confidential = false,
    required = true)
    public Boolean getUseResourceTypeUrl() {
        return this.useResourceTypeUrl;
    }

    public void setUseResourceTypeUrl(Boolean input) {
        this.useResourceTypeUrl = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Group Endpoint URL",
    helpMessageKey = "Discovered from the resource type or entered manually",
    order = 820,
    confidential = false,
    required = true)
    public String getGroupEndpointUrl() {
        return this.groupEndpointUrl;
    }

    public void setGroupEndpointUrl(String input) {
        this.groupEndpointUrl = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Enable Enterprise User",
    helpMessageKey = "Extend the user schema with enterprise attributes ",
    order = 770,
    confidential = false,
    required = true)
    public Boolean getEnableEnterpriseUser() {
        return this.enableEnterpriseUser;
    }

    public void setEnableEnterpriseUser(Boolean input) {
        this.enableEnterpriseUser = input;
    }

    @ConfigurationProperty(
    displayMessageKey = "Use Schema URL",
    helpMessageKey = "Where to use URL or JSON especially when URL is not available",
    order = 720,
    confidential = false,
    required = true)
    public Boolean getUseSchemaUrl() {
        return this.useSchemaUrl;
    }

    public void setUseSchemaUrl(Boolean input) {
        this.useSchemaUrl = input;
    }


    @Override
    public ConnectorMessages getConnectorMessages() {
        return connectorMessages;
    }

    @Override
    public void setConnectorMessages(ConnectorMessages messages) {
        connectorMessages = messages;
    }

    @Override
    public String getCurrentToken() {
        return currentToken;
    }

    @Override
    public void setCurrentToken(String input) {
        currentToken = input;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String input) {
        source = input;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String input) {
        name = input;
    }

    @Override
    public Boolean getActive() {
        return active;
    }

    @Override
    public void setActive(Boolean input) {
        active = input;
    }


}