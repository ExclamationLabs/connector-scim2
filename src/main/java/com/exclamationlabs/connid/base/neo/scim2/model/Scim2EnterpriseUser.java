package com.exclamationlabs.connid.base.neo.scim2.model;

import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import lombok.Data;

@Data
public class Scim2EnterpriseUser  {

    @ModelAttribute(modes={"ENTERPRISE_USER"})
    private String costCenter;

    @ModelAttribute(modes={"ENTERPRISE_USER"})
    private String department;

    @ModelAttribute(modes={"ENTERPRISE_USER"})
    private String division;

    @ModelAttribute(modes={"ENTERPRISE_USER"})
    private String employeeNumber;

    @ModelAttribute(modes={"ENTERPRISE_USER"})
    private Scim2ValueDisplayType manager;

    @ModelAttribute(modes={"ENTERPRISE_USER"})
    private String organization;
}
