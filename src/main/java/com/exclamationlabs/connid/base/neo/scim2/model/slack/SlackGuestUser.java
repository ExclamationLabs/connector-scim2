package com.exclamationlabs.connid.base.neo.scim2.model.slack;

import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import com.exclamationlabs.connid.base.neo.scim2.model.Scim2ValueDisplayType;
import lombok.Data;
import org.identityconnectors.framework.common.objects.AttributeInfo;

import java.util.List;

@Data
public class SlackGuestUser {

  @ModelAttribute(modes={"SLACK_GUEST_USER"}, flags = {AttributeInfo.Flags.MULTIVALUED})
  private List<Scim2ValueDisplayType> channels;

  @ModelAttribute(modes={"SLACK_GUEST_USER"})
  private String expiration;

  @ModelAttribute(value="guestType", modes={"SLACK_GUEST_USER"})
  private String type;
}
