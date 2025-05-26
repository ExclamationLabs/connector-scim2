package com.exclamationlabs.connid.base.neo.scim2.schema;

import com.exclamationlabs.connid.base.edition.neo.IamType;
import com.exclamationlabs.connid.base.neo.scim2.Mode;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.exclamationlabs.connid.base.neo.scim2.schema.SchemaName.*;

public class SchemaFactory {
    private SchemaFactory() {
        // Prevent instantiation
    }

    public static List<String> getSchemaUrns(Set<Scim2Schema> schemas) {
        return schemas.stream()
                .map(Scim2Schema::getUrn)
                .collect(Collectors.toList());
    }

    public static Set<Scim2Schema> forMode(Mode mode, IamType iamType) {
        if (iamType == IamType.USER) {
            Set<Scim2Schema> result;
            switch (mode) {
                case AWS: result = Collections.emptySet(); break;
                case Slack: result = Set.of(
                        new Scim2Schema(CORE_USER.name(),
                                "urn:ietf:params:scim:schemas:core:2.0:User"),
                        new Scim2Schema(ENTERPRISE_USER.name(),
                                "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User"),
                        new Scim2Schema(SLACK_PROFILE_USER.name(),
                                "urn:ietf:params:scim:schemas:extension:slack:profile:2.0:User"),
                        new Scim2Schema(SLACK_GUEST_USER.name(),
                                "urn:ietf:params:scim:schemas:extension:slack:guest:2.0:User")); break;

                default: throw new ConfigurationException("Unsupported user mode: " + mode);
            }
            return result;
        } else if (iamType == IamType.GROUP) {
            Set<Scim2Schema> result;
            switch (mode) {
                case AWS: result = Collections.emptySet(); break;
                case Slack: result = Set.of(
                        new Scim2Schema(CORE_GROUP.name(),
                                "urn:ietf:params:scim:schemas:core:2.0:Group"),
                        new Scim2Schema(SLACK_GROUP.name(),
                                "urn:ietf:params:scim:schemas:extension:slack:profile:2.0:Group")

                        ); break;

                default: throw new ConfigurationException("Unsupported group mode: " + mode);
            }
            return result;
        }
        throw new ConfigurationException("Unsupported IamType: " + iamType);
    }
}
