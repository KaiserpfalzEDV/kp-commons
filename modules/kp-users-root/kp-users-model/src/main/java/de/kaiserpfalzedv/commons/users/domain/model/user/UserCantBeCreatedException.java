/*
 * Copyright (c) 2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.users.domain.model.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;


/**
 * The user can't be created with the given data.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 04.05.2025
 */
@Getter
@ToString
public class UserCantBeCreatedException extends BaseUserException {
  private final String issuer;
  private final String subject;
  private final String username;
  private final String email;
  
  public UserCantBeCreatedException(@NotNull final User user, final Throwable cause) {
    super(user, createExceptionMessage(user.getIssuer(), user.getSubject(), user.getUsername(), user.getEmail()), cause);
    
    this.issuer = user.getIssuer();
    this.subject = user.getSubject();
    this.username = user.getUsername();
    this.email = user.getEmail();
  }
  
  public UserCantBeCreatedException(@NotBlank final String issuer, @NotBlank final String subject,
                                    @NotBlank final String username, final String email) {
    super(null, createExceptionMessage(issuer, subject, username, email));
    
    this.issuer = issuer;
    this.subject = subject;
    this.username = username;
    this.email = email;
  }
  
  public UserCantBeCreatedException(@NotBlank final String issuer, @NotBlank final String subject,
                                    @NotBlank final String username, final String email,
                                    final Throwable cause) {
    super(null, createExceptionMessage(issuer, subject, username, email), cause);
    
    this.issuer = issuer;
    this.subject = subject;
    this.username = username;
    this.email = email;
  }
  
  private static String createExceptionMessage(final String issuer, final String subject,
                                               final String username, final String email) {
    return "User creation failed. Can't create user with issuer='%s', subject='%s', username='%s', and email='%s'"
        .formatted(issuer, subject, username, email.isBlank() ? "---" : email);
  }
}
