/*
 * Copyright (c) 2022-2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.r2dbc;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "lombok provided superbuilder constructor.")
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(onlyExplicitlyIncluded = true)
public abstract class AbstractR2dbcEntity<T extends Serializable> implements HasId<T>, Cloneable {
    @Id
    @ToString.Include
    @Column(value = "ID")
    protected T id;
    
    @NotNull
    @ToString.Include
    @Builder.Default
    @Column(value = "CREATED")
    protected OffsetDateTime created = OffsetDateTime.now(ZoneOffset.UTC);
    
    @NotNull
    @Builder.Default
    @Column(value = "MODIFIED")
    protected OffsetDateTime modified = OffsetDateTime.now(ZoneOffset.UTC);
    
    @Nullable
    @Column(value = "DELETED")
    protected OffsetDateTime deleted;


    @SuppressWarnings({"unchecked","java:S2097"})
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(AbstractR2dbcEntity.class.isAssignableFrom(o.getClass()))) return false;

        return this.id.equals(((HasId<T>)o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    @SuppressWarnings("java:S2975")
    protected AbstractR2dbcEntity<T> clone() throws CloneNotSupportedException {
        @SuppressWarnings("unchecked")
        final AbstractR2dbcEntity<T> result = (AbstractR2dbcEntity<T>) super.clone();

        result.id = this.id;
        result.created = this.created;
        result.modified = this.modified;
        
        if (this.deleted != null) {
            result.deleted = this.deleted.toInstant().atOffset(ZoneOffset.UTC);
        }

        return result;
    }
}