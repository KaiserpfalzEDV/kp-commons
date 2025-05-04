package de.kaiserpfalzedv.commons.users.domain.model;


import de.kaiserpfalzedv.commons.users.domain.BaseUserException;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;


/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 20.04.25
 */
@Getter
@ToString(callSuper = true)
public abstract class UserIsInactiveException extends BaseUserException {
  public UserIsInactiveException(@Nullable final User user, @NotBlank final String message) {
    super(user, message);
  }
}
