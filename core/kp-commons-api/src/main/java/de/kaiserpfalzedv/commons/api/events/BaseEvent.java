/*
 * Copyright (c) 2024-2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.api.events;


import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@Getter
@ToString(of = {"id", "timestamp"})
@EqualsAndHashCode(of = {"id"})
public abstract class BaseEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private final UUID id = UUID.randomUUID();

    @Builder.Default
    protected final OffsetDateTime timestamp = OffsetDateTime.now();
    
    @Builder.Default
    protected final String application = "unset";

    
    abstract public String getI18nKey();
    
    public Object[] getI18nData() {
        return new Object[0];
    }
}
