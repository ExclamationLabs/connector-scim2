package com.exclamationlabs.connid.base.scim2.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchemaExtension {
    private String schema;
    private Boolean required;
}
