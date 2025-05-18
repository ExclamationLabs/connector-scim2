package com.exclamationlabs.connid.base.neo.scim2.model.response;

import com.exclamationlabs.connid.base.neo.scim2.model.Scim2User;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Scim2UsersResponse {

    private Integer itemsPerPage;

    @SerializedName("Resources")
    private List<Scim2User> resources;

    private List<String> schemas;

    private Integer startIndex;
    private Integer totalResults;
}
