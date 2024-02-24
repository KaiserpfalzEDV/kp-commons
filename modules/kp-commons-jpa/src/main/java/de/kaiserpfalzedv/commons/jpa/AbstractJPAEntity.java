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

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import de.kaiserpfalzedv.commons.api.resources.HasId;
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

@MappedSuperclass
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public abstract class AbstractJPAEntity implements HasId, Cloneable {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @Column(name = "ID", length = 36, nullable = false, updatable = false, unique = true)
    @ToString.Include
    protected UUID id;

    @NonNull
    @Version
    @Column(name = "VERSION", nullable = false)
    @Builder.Default
    @ToString.Include
    protected Integer version = 0;

    @CreationTimestamp
    @Column(name = "CREATED", nullable = false, updatable = false)
    @NonNull
    protected OffsetDateTime created;

    @UpdateTimestamp
    @Column(name = "MODIFIED", nullable = false)
    @NonNull
    protected OffsetDateTime modified;
    
    @Column(name = "DELETED", insertable = false)
    protected OffsetDateTime deleted;


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final AbstractJPAEntity entity)) return false;
        return this.id.equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    protected AbstractJPAEntity clone() throws CloneNotSupportedException {
        final AbstractJPAEntity result = (AbstractJPAEntity) super.clone();

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