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

package de.kaiserpfalzedv.commons.users.domain.services;


import de.kaiserpfalzedv.commons.users.domain.model.abac.HasOwner;
import de.kaiserpfalzedv.commons.users.domain.model.abac.OwnedBy;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 01.05.2025
 */
@Service
@Singleton
@Scope("singleton")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AbacService {
  private final Enforcer enforcer;
  
  @PostConstruct
  public void init() {
    enforcer.addFunction("ownedBy", new OwnedBy());
  }
  
  public boolean enforce(final User subject, final HasOwner<UUID> object, final String action) {
    return enforcer.enforce(subject, object, action);
  }
}
