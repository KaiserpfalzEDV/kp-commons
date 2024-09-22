/*
 * Copyright (c) 2022-2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.jpa;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "lombok provided superbuilder constructor.")
@MappedSuperclass
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public abstract class AbstractJPAEntity<T extends Serializable> implements HasId<T>, Cloneable {
    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false, insertable = true, updatable = false, unique = true)
    protected T id;

    @NonNull
    @Version
    @Column(name = "VERSION", nullable = false, insertable = true, updatable = true)
    @Builder.Default
    protected Integer version = 0;

    @CreationTimestamp
    @Column(name = "CREATED", nullable = false, insertable = true, updatable = false)
    protected OffsetDateTime created;

    @UpdateTimestamp
    @Column(name = "MODIFIED", nullable = true, insertable = false, updatable = true)
    protected OffsetDateTime modified;

    @Column(name = "DELETED", nullable = true, insertable = false, updatable = true)
    @Nullable
    protected OffsetDateTime deleted;


    @SuppressWarnings({"unchecked","java:S2097"})
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(AbstractJPAEntity.class.isAssignableFrom(o.getClass()))) return false;

        return this.id.equals(((HasId<T>)o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    @SuppressWarnings("java:S2975")
    protected AbstractJPAEntity<T> clone() throws CloneNotSupportedException {
        @SuppressWarnings("unchecked")
        final AbstractJPAEntity<T> result = (AbstractJPAEntity<T>) super.clone();

        result.id = this.id;
        result.version = this.version;

        if (this.created != null) {
            result.created = this.created;
        }

        if (this.modified != null) {
            result.modified = this.modified;
        }

        return result;
    }
}