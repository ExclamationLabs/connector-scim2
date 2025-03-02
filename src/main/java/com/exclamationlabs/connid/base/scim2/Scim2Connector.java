package com.exclamationlabs.connid.base.scim2;

import com.exclamationlabs.connid.base.connector.BaseFullAccessConnector;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.DirectAccessTokenAuthenticator;
import com.exclamationlabs.connid.base.scim2.adapter.Scim2GroupsAdapter;
import com.exclamationlabs.connid.base.scim2.adapter.Scim2UserAdapter;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import com.exclamationlabs.connid.base.scim2.driver.rest.Scim2Driver;
import org.apache.commons.lang3.BooleanUtils;
import org.identityconnectors.framework.common.objects.SuggestedValues;
import org.identityconnectors.framework.common.objects.SuggestedValuesBuilder;

import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.DiscoverConfigurationOp;

import java.util.HashMap;
import java.util.Map;

import static com.exclamationlabs.connid.base.scim2.adapter.Scim2GroupsAdapter.SCIM2_CORE_GROUP_SCHEMA;
import static com.exclamationlabs.connid.base.scim2.adapter.Scim2UserAdapter.SCIM2_CORE_USER_SCHEMA;

@ConnectorClass(
    displayNameKey = "scim2.connector.display",
    configurationClass = Scim2Configuration.class)
public class Scim2Connector extends BaseFullAccessConnector<Scim2Configuration> implements DiscoverConfigurationOp{

  public static final String USER_OBJECT_CLASS = Scim2UserAdapter.SCIM2_USER;
  public static final String GROUP_OBJECT_CLASS = Scim2GroupsAdapter.SCIM2_GROUP;

  public Scim2Connector() {
    super(Scim2Configuration.class);
    setAuthenticator((Authenticator) new DirectAccessTokenAuthenticator());
    setDriver(new Scim2Driver());
    setAdapters(new Scim2UserAdapter(), new Scim2GroupsAdapter());
  }

  @Override
  public Map<String, SuggestedValues> discoverConfiguration()
  {
    Map<String, SuggestedValues> suggestions = new HashMap<>();

    // deepGet Suggestion
    suggestions.put("deepGet", SuggestedValuesBuilder.buildOpen(BooleanUtils.toBoolean(configuration.getDeepGet())));

    // deepImport Suggestion
    suggestions.put("deepImport", SuggestedValuesBuilder.buildOpen(BooleanUtils.toBoolean(configuration.getDeepImport())));

    // pagination Suggestion
    boolean pagination = configuration.getPagination() == null || configuration.getPagination();
    suggestions.put("pagination", SuggestedValuesBuilder.buildOpen(pagination));

    int ioErrorRetries = configuration.getIoErrorRetries() == null ? 3 : configuration.getIoErrorRetries();
    suggestions.put("ioErrorRetries", SuggestedValuesBuilder.buildOpen(ioErrorRetries));


    if ( configuration.getImportBatchSize() != null )
    {
      suggestions.put("importBatchSize", SuggestedValuesBuilder.buildOpen(configuration.getImportBatchSize()));
    }
    else
    {
      suggestions.put("importBatchSize", SuggestedValuesBuilder.buildOpen(20, 50, 100));
    }

    boolean duplicateErrorReturnsId = configuration.getDuplicateErrorReturnsId() == null || configuration.getDuplicateErrorReturnsId();
    suggestions.put("duplicateErrorReturnsId", SuggestedValuesBuilder.buildOpen(duplicateErrorReturnsId));

    if ( !configuration.getEnableStandardSchema() )
    {
      suggestions.put("enableStandardSchema", SuggestedValuesBuilder.buildOpen(true));
    }
    if ( !configuration.getEnableEnterpriseUser())
    {
      suggestions.put("enableEnterpriseUser", SuggestedValuesBuilder.buildOpen(true));
    }

    String usersEndpointUrl = configuration.getUsersEndpointUrl() == null ? "/Users" : configuration.getUsersEndpointUrl();
    suggestions.put("usersEndpointUrl", SuggestedValuesBuilder.buildOpen(usersEndpointUrl));

    String groupsEndpointUrl = configuration.getGroupsEndpointUrl() == null ? "/Groups" : configuration.getGroupsEndpointUrl();
    suggestions.put("groupsEndpointUrl", SuggestedValuesBuilder.buildOpen(groupsEndpointUrl));

    // User Schema ID List
    suggestions.put("userSchemaIdList", SuggestedValuesBuilder.buildOpen(SCIM2_CORE_USER_SCHEMA));

    // Group Schema ID List
    suggestions.put("groupSchemaIdList", SuggestedValuesBuilder.buildOpen(SCIM2_CORE_GROUP_SCHEMA));

    return suggestions;
  }
  public void testPartialConfiguration()
  {
    test();
  }
}
