package com.exclamationlabs.connid.base.neo.scim2.model.slack;

import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import lombok.Data;

@Data
public class SlackProfileUser {

  @ModelAttribute(modes={"SLACK_USER"})
  private String startDate;
}
