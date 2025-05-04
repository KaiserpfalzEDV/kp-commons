package de.kaiserpfalzedv.commons.users.domain.model;

import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 20.04.25
 */
@Getter
@ToString(callSuper = true)
public class UserIsDeletedException extends UserIsInactiveException {
  public UserIsDeletedException(@NotNull User user) {
    super(user, "User is deleted since " + user.getDeleted());
  }
  
  public OffsetDateTime getDeletedAt() {
    return user.getDeleted();
  }
}
