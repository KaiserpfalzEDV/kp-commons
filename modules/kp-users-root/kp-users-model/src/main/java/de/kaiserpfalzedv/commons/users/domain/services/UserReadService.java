package de.kaiserpfalzedv.commons.users.domain.services;


import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 03.05.2025
 */
public interface UserReadService {
  Optional<User> findById(@NotBlank UUID id);

  Optional<User> findByUsername(@NotBlank final String nameSpace, @NotBlank final String name);
  Optional<User> findByEmail(@NotBlank final String email);
  Optional<User> findByOauth(@NotBlank final String issuer, @NotBlank final String sub);
  
  List<User> findAllByNamespace(@NotBlank final String namespace);
  Page<User> findAllByNamespace(@NotBlank final String namespace, Pageable pageable);
  
  List<User> findAllByIssuer(@NotBlank final String issuer);
  Page<User> findAllByIssuer(@NotBlank final String issuer, Pageable pageable);
}
