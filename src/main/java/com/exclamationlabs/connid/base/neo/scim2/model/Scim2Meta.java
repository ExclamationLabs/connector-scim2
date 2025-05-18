package com.exclamationlabs.connid.base.neo.scim2.model;

import lombok.Getter;

/**
 * SCIM2 Meta Type is a common attribute included with most resources such as Users, Groups, Schemas
 */
@Getter
public class Scim2Meta {
    private String created;
    private String lastModified;
    private String location;
    private String resourceType;
    private String version;
}
