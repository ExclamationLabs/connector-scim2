package com.exclamationlabs.connid.base.neo.scim2.model.slack;

import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import com.exclamationlabs.connid.base.scim2.model.Scim2ComplexType;
import com.exclamationlabs.connid.base.scim2.model.slack.SlackChannel;
import lombok.Data;

import java.util.List;

@Data
public class SlackGuestUser {

  @ModelAttribute(modes={"SLACK_USER"})
  private List<Scim2ComplexType> channels;

  @ModelAttribute(modes={"SLACK_USER"})
  private String expiration;

  @ModelAttribute(value="guestType", modes={"SLACK_USER"})
  private String type;
}
