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

package de.kaiserpfalzedv.commons.users.store.model.role;


import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-11
 */
@SuppressWarnings("LoggingSimilarMessage")
@ExtendWith(MockitoExtension.class)
@XSlf4j
public class JpaRoleReadServiceTest {
  private JpaRoleReadService sut;
  
  @Mock
  private RoleRepository roleRepository;
  
  
  @BeforeEach
  public void setUp() {
    reset(roleRepository);
    
    sut = new JpaRoleReadService(roleRepository);
  }
  
  @AfterEach
  public void tearDown() {
    validateMockitoUsage(); // validate if the mocks are used as expected.
  }
  
  
  @Test
  void shouldFindRoleByIdWhenRoleExists() {
    log.entry();
    
    when(roleRepository.findById(DEFAULT_ROLE.getId())).thenReturn(Optional.of(DEFAULT_ROLE));
    
    Optional<RoleJPA> result = sut.retrieve(DEFAULT_ROLE.getId());
    log.debug("result. role={}", result.orElse(null));
    
    assertTrue(result.isPresent());
    
    log.exit();
  }
  
  @Test
  void shouldNotFindRoleByIdWhenRoleWithIdDoesNotExist() {
    log.entry();
    
    when(roleRepository.findById(DEFAULT_ROLE.getId())).thenReturn(Optional.empty());
    
    Optional<RoleJPA> result = sut.retrieve(DEFAULT_ROLE.getId());
    log.debug("result. role={}", result.orElse(null));
    
    assertTrue(result.isEmpty());
    
    log.exit();
  }
  
  @Test
  void shouldReturnListOfRolesWhenRolesExist() {
    log.entry();
    
    when(roleRepository.findAll()).thenReturn(List.of(DEFAULT_ROLE));
    
    List<RoleJPA> result = sut.retrieveAll();
    log.debug("result. roles={}", result);
    
    assertEquals(1, result.size());
    
    log.exit();
  }
  
  @Test
  void shouldReturnPageOfRolesWhenRolesExist() {
    log.entry();
    
    when(roleRepository.findAll(DEFAULT_PAGEABLE)).thenReturn(DEFAULT_PAGE);
    
    Page<RoleJPA> result = sut.retrieveAll(DEFAULT_PAGEABLE);
    log.debug("result. roles={}", result);
    
    assertEquals(1, result.getTotalElements());
    
    log.exit();
  }
  
  
  @Test
  void shouldReturnListOfRolesInNamespaceWhenRolesInNameSpaceExist() {
    log.entry();
    
    when(roleRepository.findByNameSpace(DEFAULT_ROLE.getNameSpace())).thenReturn(List.of(DEFAULT_ROLE));
    
    List<RoleJPA> result = sut.retrieveAllFromNamespace(DEFAULT_ROLE.getNameSpace());
    log.debug("result. roles={}", result);
    
    assertEquals(1, result.size());
    
    log.exit();
  }
  
  @Test
  void shouldReturnPageOfRolesInNamespaceWhenRolesInNameSpaceExist() {
    log.entry();
    
    when(roleRepository.findByNameSpace(DEFAULT_ROLE.getNameSpace(), DEFAULT_PAGEABLE)).thenReturn(DEFAULT_PAGE);
    
    Page<RoleJPA> result = sut.retrieveAllFromNamespace(DEFAULT_ROLE.getNameSpace(), DEFAULT_PAGEABLE);
    log.debug("result. roles={}", result);
    
    assertEquals(1, result.getTotalElements());
    
    log.exit();
  }
  
  @Test
  void shouldReturnListOfRolesByNameWhenRolesWithNameExist() {
    log.entry();
    
    when(roleRepository.findByName(DEFAULT_ROLE.getName())).thenReturn(List.of(DEFAULT_ROLE));
    
    List<RoleJPA> result = sut.retrieveByName(DEFAULT_ROLE.getName());
    log.debug("result. roles={}", result);
    
    assertEquals(1, result.size());
    
    log.exit();
  }
  
  @Test
  void shouldReturnPageOfRolesByNameWhenRolesWithNameExist() {
    log.entry();
    
    when(roleRepository.findByName(DEFAULT_ROLE.getName(), DEFAULT_PAGEABLE)).thenReturn(DEFAULT_PAGE);
    
    Page<RoleJPA> result = sut.retrieveByName(DEFAULT_ROLE.getName(), DEFAULT_PAGEABLE);
    log.debug("result. roles={}", result);
    
    assertEquals(1, result.getTotalElements());
    
    log.exit();
  }
  
  
  private static final UUID DEFAULT_ID = UUID.randomUUID();
  private static final OffsetDateTime CREATED_AT = OffsetDateTime.now();
  private static final RoleJPA DEFAULT_ROLE = RoleJPA.builder()
      .id(DEFAULT_ID)
      .nameSpace("namespace")
      .name("name")
      .version(0)
      .created(CREATED_AT)
      .modified(CREATED_AT)
      .build();
  
  private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10);
  private static final Page<RoleJPA> DEFAULT_PAGE = new PageImpl<>(List.of(DEFAULT_ROLE));
}
