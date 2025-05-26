package com.exclamationlabs.connid.base.neo.scim2.model;

import com.exclamationlabs.connid.base.edition.neo.IamType;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttributeHolder;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelObjectClass;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.Direction;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import com.exclamationlabs.connid.base.neo.scim2.model.slack.SlackGuestUser;
import com.exclamationlabs.connid.base.neo.scim2.model.slack.SlackProfileUser;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.identityconnectors.framework.common.objects.AttributeInfo;

import java.util.List;

@ModelObjectClass(value = "Scim2User", forType = IamType.USER)
@Data
public class Scim2User implements IdentityModel {

    private List<String> schemas;

    @ModelAttribute(identifier = ConnIdType.UID)
    private String id;

    @ModelAttribute
    private String externalId;

    @ModelAttribute(identifier = ConnIdType.NAME)
    private String userName;

    @ModelAttribute
    private String displayName;

    @ModelAttribute
    private String nickName;

    @ModelAttribute
    private String profileUrl;

    @ModelAttribute
    private String title;

    @ModelAttribute
    private String userType;

    @ModelAttribute
    private String preferredLanguage;

    @ModelAttribute
    private String locale;

    @ModelAttribute
    private String timezone;

    @ModelAttribute
    private Boolean active;

    @ModelAttribute(direction = Direction.OUTBOUND_ONLY)
    private String password;

    private Scim2Meta meta;

    @ModelAttributeHolder
    private Scim2Name name;

    @ModelAttribute(flags = {AttributeInfo.Flags.MULTIVALUED})
    private List<Scim2ComplexType> emails;

    @ModelAttribute(flags = {AttributeInfo.Flags.MULTIVALUED})
    private List<Scim2ComplexType> phoneNumbers;

    @ModelAttribute(flags = {AttributeInfo.Flags.MULTIVALUED})
    private List<Scim2ComplexType> ims;

    @ModelAttribute(flags = {AttributeInfo.Flags.MULTIVALUED})
    private List<Scim2ComplexType> photos;

    @ModelAttribute(flags = {AttributeInfo.Flags.MULTIVALUED})
    private List<Scim2ComplexType> addresses;

    @ModelAttribute(flags = {AttributeInfo.Flags.MULTIVALUED})
    private List<Scim2ValueDisplayType> groups;

    @ModelAttribute(flags = {AttributeInfo.Flags.MULTIVALUED})
    private List<Scim2ComplexType> entitlements;

    @ModelAttribute(flags = {AttributeInfo.Flags.MULTIVALUED})
    private List<Scim2ComplexType> roles;

    @ModelAttribute(flags = {AttributeInfo.Flags.MULTIVALUED})
    private List<Scim2ComplexType> x509Certificates;

    @ModelAttributeHolder(modes={"ENTERPRISE_USER"})
    @SerializedName("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User")
    private Scim2EnterpriseUser enterpriseUser;

    @ModelAttributeHolder(modes={"SLACK_GUEST_USER"})
    @SerializedName("urn:ietf:params:scim:schemas:extension:slack:guest:2.0:User")
    private SlackGuestUser guestInfo;

    @ModelAttributeHolder(modes={"SLACK_PROFILE_USER"})
    @SerializedName("urn:ietf:params:scim:schemas:extension:slack:profile:2.0:User")
    private SlackProfileUser profileInfo;
}
