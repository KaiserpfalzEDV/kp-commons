package de.kaiserpfalzedv.commons.users.domain.services;


import de.kaiserpfalzedv.commons.users.domain.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.role.Role;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 03.05.2025
 */
public interface UserWriteService {
  void create(User user) throws UserCantBeCreatedException;
  void updateIssuer(User user, @NotBlank String issuer, @NotBlank String sub);
  
  void updateNamespace(User user, @NotBlank String namespace);
  void updateName(User user, @NotBlank String name);
  void updateNamespaceAndName(User user, @NotBlank String namespace, @NotBlank String name);
  
  void updateEmail(User user, @NotBlank String email);
  void updateDiscord(User user, @NotBlank String discord);
  
  void addRole(User user, @NotNull Role role);
  void removeRole(User user, @NotNull Role role);
}
