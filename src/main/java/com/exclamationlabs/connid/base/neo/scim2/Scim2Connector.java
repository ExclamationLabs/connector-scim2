package com.exclamationlabs.connid.base.neo.scim2;

import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.IamType;
import com.exclamationlabs.connid.base.neo.scim2.schema.SchemaFactory;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.ConnectorClass;

import java.util.concurrent.atomic.AtomicBoolean;


@ConnectorClass(
    displayNameKey = "scim2.connector.display",
    configurationClass = Scim2Configuration.class)
public class Scim2Connector extends BaseConnector<Scim2Configuration> {

  private Mode mode;

  public Scim2Connector() {
    super(Scim2Configuration.class);
  }

  @Override
  public void init(Configuration configuration) throws ConfigurationException {
    super.init(configuration);
    initializePrimaryMode((Scim2Configuration) configuration);
  }

  @Override
  public boolean allowedForModes(Scim2Configuration configuration, String[] modesFromAttribute, IamType iamType) {
    if (modesFromAttribute == null || modesFromAttribute.length == 0) {
      return true;
    }
    initializePrimaryMode(configuration);

    for (String schemaName : modesFromAttribute) {
        var schemaSet = SchemaFactory.forMode(mode, iamType);
        AtomicBoolean match = new AtomicBoolean(false);
        schemaSet.forEach(schema -> {
          if (schema.getName().equalsIgnoreCase(schemaName)) {
            match.set(true);
          }
        });
        if (match.get()) {
          return true;
        }
    }

    return false;
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
