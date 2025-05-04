package de.kaiserpfalzedv.commons.users.store.service;


import de.kaiserpfalzedv.commons.users.domain.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import de.kaiserpfalzedv.commons.users.domain.services.UserReadService;
import de.kaiserpfalzedv.commons.users.domain.services.UserWriteService;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 03.05.2025
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@XSlf4j
public class KpUserStoreService implements UserReadService, UserWriteService {
  
  @Override
  public Optional<User> findById(final UUID id) {
    return Optional.empty();
  }
  
  @Override
  public Optional<User> findByUsername(final String nameSpace, final String name) {
    return Optional.empty();
  }
  
  @Override
  public Optional<User> findByEmail(final String email) {
    return Optional.empty();
  }
  
  @Override
  public Optional<User> findByOauth(final String issuer, final String sub) {
    return Optional.empty();
  }
  
  @Override
  public List<User> findAllByNamespace(final String namespace) {
    return List.of();
  }
  
  @Override
  public Page<User> findAllByNamespace(final String namespace, final Pageable pageable) {
    return null;
  }
  
  @Override
  public List<User> findAllByIssuer(final String issuer) {
    return List.of();
  }
  
  @Override
  public Page<User> findAllByIssuer(final String issuer, final Pageable pageable) {
    return null;
  }
  
  @Override
  public User create(final User user) throws UserCantBeCreatedException {
    throw new UserCantBeCreatedException(user.getIssuer(), user.getSubject(), user.getUsername(), user.getEmail());
  }
  
  @Override
  public User updateIssuer(final User user, final String issuer, final String sub) {
    return null;
  }
  
  @Override
  public User updateNamespace(final User user, final String namespace) {
    return null;
  }
  
  @Override
  public User updateName(final User user, final String name) {
    return null;
  }
  
  @Override
  public User updateNamespaceAndName(final User user, final String namespace, final String name) {
    return null;
  }
  
  @Override
  public User updateEmail(final User user, final String email) {
    return null;
  }
  
  @Override
  public User updateDiscord(final User user, final String discord) {
    return null;
  }
}
