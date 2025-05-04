/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.kaiserpfalzedv.commons.users.domain.model.abac;


import de.kaiserpfalzedv.commons.api.resources.HasId;

import java.io.Serializable;


/**
 * Interface for Objects to be used in DCIS casbin rules.
 * They have to have an owner.
 *
 * @param <T> The type of the owner id returned for the jcasbin checks.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 01.05.2025
 *
 * @see org.casbin.jcasbin.main.Enforcer
 */
public interface HasOwner<T extends Serializable> extends HasId<T> {
  default String getABACObjectName() {
    return getClass().getSimpleName();
  }
  
  default T getOwnerId() {
    return getOwner().getId();
  }
  
  HasId<T> getOwner();
}
