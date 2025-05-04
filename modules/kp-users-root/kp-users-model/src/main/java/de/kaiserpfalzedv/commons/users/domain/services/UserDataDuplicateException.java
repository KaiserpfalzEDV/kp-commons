package de.kaiserpfalzedv.commons.users.domain.services;


import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 03.05.2025
 */
@Getter
@ToString(callSuper = true)
public class UserDataDuplicateException extends UserDataException {
  public UserDataDuplicateException(@Nullable final User user, final String message) {
    super(user, message);
  }
}
