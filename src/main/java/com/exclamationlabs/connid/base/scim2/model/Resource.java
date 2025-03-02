package com.exclamationlabs.connid.base.scim2.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Resource schema and meta information available for a SCIM2 resource type.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
    private List<String> schemas;
    private String id;
    private String name;
    private String endpoint;
    private String description;
    private String schema;
    private Meta meta;
    private List<SchemaExtension> schemaExtensions;
}
