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

package de.kaiserpfalzedv.commons.users.store.user;

import com.google.common.eventbus.EventBus;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserCantBeCreatedException;
import de.kaiserpfalzedv.commons.users.domain.model.user.UserNotFoundException;
import de.kaiserpfalzedv.commons.users.domain.model.user.events.state.UserCreatedEvent;
import de.kaiserpfalzedv.commons.users.store.model.user.JpaUserWriteService;
import de.kaiserpfalzedv.commons.users.store.model.user.UserJPA;
import de.kaiserpfalzedv.commons.users.store.model.user.UserRepository;
import de.kaiserpfalzedv.commons.users.store.model.user.UserToJpa;
import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@XSlf4j
public class JpaUserWriteServiceTest {
  
  @InjectMocks
  private JpaUserWriteService sut;
  
  @Mock
  private UserRepository repository;
  
  @Mock
  private EventBus bus;
  
  @Mock
  private UserToJpa toJpa;
  
  
  @BeforeEach
  public void setUp() {
  }
  
  @Test
  void shouldCreateUserSuccessfully() throws UserCantBeCreatedException {
    log.entry();
    
    when(toJpa.apply(any())).thenReturn(DEFAULT_USER);
    
    sut.create(DEFAULT_USER);
    
    verify(repository).save(DEFAULT_USER);
    verify(bus).post(any(UserCreatedEvent.class));
    
    log.exit();
  }
  
  @Test
  void shouldThrowExceptionWhenUserNotFoundForUpdateIssuer() {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
    
    assertThrows(UserNotFoundException.class, () -> sut.updateIssuer(DEFAULT_ID, "newIssuer", "newSub"));
    
    log.exit();
  }
  
  @Test
  void shouldUpdateIssuerSuccessfully() throws UserNotFoundException {
    log.entry();
    
    when(repository.findById(DEFAULT_ID)).thenReturn(Optional.of(DEFAULT_USER));
    
    sut.updateIssuer(DEFAULT_ID, "newIssuer", "newSub");
    
    verify(repository).saveAndFlush(any(UserJPA.class));
    verify(bus).post(any());
    
    log.exit();
  }
  
  
  private static final UUID DEFAULT_ID = UUID.randomUUID();
  private static final OffsetDateTime CREATED_AT = OffsetDateTime.now();
  private static final UserJPA DEFAULT_USER = UserJPA.builder()
      .id(DEFAULT_ID)
      
      .nameSpace("namespace")
      .name("name")
      
      .issuer("issuer")
      .subject(DEFAULT_ID.toString())
      
      .version(0)
      .revId(0)
      .revisioned(CREATED_AT)
      
      .created(CREATED_AT)
      .modified(CREATED_AT)
      
      .build();
}
