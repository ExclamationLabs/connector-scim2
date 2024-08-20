package com.exclamationlabs.connid.base.scim2.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Scim2PatchOp
{
    private static final String SCIM2_PATCH_OP = "urn:ietf:params:scim:api:messages:2.0:PatchOp";
    private Set<String> schemas;
    @SerializedName("Operations")
    private List<Scim2Operation> operations;

    public Scim2PatchOp()
    {
        schemas = new HashSet<>();
        schemas.add(SCIM2_PATCH_OP);
    }

    public Set<String> getSchemas()
    {
        return schemas;
    }

    public void setSchemas(Set<String> schemas)
    {
        this.schemas = schemas;
        if ( this.schemas == null ) {
            schemas = new HashSet<>();
            schemas.add(SCIM2_PATCH_OP);
        } else if ( !this.schemas.contains(SCIM2_PATCH_OP))
        {
            schemas.add(SCIM2_PATCH_OP);
        }
    }

    public List<Scim2Operation> getOperations()
    {
        return operations;
    }

    public void setOperations(List<Scim2Operation> operations)
    {
        this.operations = operations;
    }
}
