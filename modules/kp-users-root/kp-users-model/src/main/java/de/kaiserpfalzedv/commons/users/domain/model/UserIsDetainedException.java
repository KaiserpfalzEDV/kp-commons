package de.kaiserpfalzedv.commons.users.domain.model;


import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.OffsetDateTime;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 20.04.25
 */
@Getter
@ToString(callSuper = true)
public class UserIsDetainedException extends UserIsInactiveException {
  public UserIsDetainedException(@NotNull User user) {
    super(user, "User is detained till " + user.getDetainedTill());
  }
  
  public OffsetDateTime getDetainedTill() {
    return user.getDetainedTill();
  }
  
  public Duration getDetainedFor() {
    return user.getDetainmentDuration();
  }
  
  public Long getDetainedDays() {
    return user.getDetainmentDuration().toDays();
  }
}
