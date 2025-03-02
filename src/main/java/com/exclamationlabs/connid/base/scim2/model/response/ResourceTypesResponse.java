package com.exclamationlabs.connid.base.scim2.model.response;

import com.exclamationlabs.connid.base.scim2.model.Resource;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This response shows the resource types available on the SCIM2 server.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceTypesResponse {
    private List<String> schemas;

    @SerializedName("Resources")
    private List<Resource> resources;

}
