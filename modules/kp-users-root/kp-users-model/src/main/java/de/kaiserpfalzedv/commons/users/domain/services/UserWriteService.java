package de.kaiserpfalzedv.commons.users.domain.services;


import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import jakarta.validation.constraints.NotBlank;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 03.05.2025
 */
public interface UserWriteService {
  User create(User user);
  User updateIssuer(User user, @NotBlank String issuer, @NotBlank String sub);

  User updateNamespace(User user, @NotBlank String namespace);
  User updateName(User user, @NotBlank String name);
  User updateNamespaceAndName(User user, @NotBlank String namespace, @NotBlank String name);

  User updateEmail(User user, @NotBlank String email);
  User updateDiscord(User user, @NotBlank String discord);
}
