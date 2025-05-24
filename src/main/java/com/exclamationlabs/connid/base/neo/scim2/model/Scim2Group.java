package com.exclamationlabs.connid.base.neo.scim2.model;

import com.exclamationlabs.connid.base.edition.neo.IamType;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttributeHolder;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelObjectClass;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import lombok.Data;
import org.identityconnectors.framework.common.objects.AttributeInfo;

import java.util.List;

@ModelObjectClass(value = "Scim2Group", forType = IamType.GROUP)
@Data
public class Scim2Group implements IdentityModel {

    private List<String> schemas;

    @ModelAttribute(identifier = ConnIdType.UID)
    private String id;

    @ModelAttribute(identifier = ConnIdType.NAME)
    private String displayName;

    @ModelAttribute(flags = {AttributeInfo.Flags.MULTIVALUED})
    private List<Scim2ComplexType> members;
}
