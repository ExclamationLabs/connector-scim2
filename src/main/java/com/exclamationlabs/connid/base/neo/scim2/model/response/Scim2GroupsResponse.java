package com.exclamationlabs.connid.base.neo.scim2.model.response;

import com.exclamationlabs.connid.base.neo.scim2.model.Scim2Group;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Scim2GroupsResponse {

    private Integer itemsPerPage;

    @SerializedName("Resources")
    private List<Scim2Group> resources;

    private List<String> schemas;

    private Integer startIndex;
    private Integer totalResults;
}
