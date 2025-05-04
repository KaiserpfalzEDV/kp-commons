package de.kaiserpfalzedv.commons.users.domain.model;


import de.kaiserpfalzedv.commons.api.BaseException;
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
public abstract class BaseUserException extends BaseException {
  protected final User user;
  
  public BaseUserException(@Nullable final User user, @NotBlank final String message) {
    super(message);
    
    this.user = user;
  }
  
  public BaseUserException(@Nullable final User user, @NotBlank final String message, @Nullable final Throwable cause) {
    super(message, cause);
    
    this.user = user;
  }
}
