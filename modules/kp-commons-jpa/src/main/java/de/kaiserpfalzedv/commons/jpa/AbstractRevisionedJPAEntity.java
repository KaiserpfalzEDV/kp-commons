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

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "lombok provided SuperBuilder constructor.")
@MappedSuperclass
@RevisionEntity
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, of = {"revId"})
public abstract class AbstractRevisionedJPAEntity<T extends Serializable> extends AbstractJPAEntity<T> {
    @RevisionNumber
    protected int revId;

    @RevisionTimestamp
    @Column(name = "REVISIONED", insertable = true, updatable = false)
    protected OffsetDateTime revisioned;


    @SuppressWarnings("java:S2975")
    @Override
    public AbstractRevisionedJPAEntity<T> clone() throws CloneNotSupportedException {
        final AbstractRevisionedJPAEntity<T> result = (AbstractRevisionedJPAEntity<T>) super.clone();

        result.revId = this.revId;
        result.revisioned = this.revisioned;

        return result;
    }
}
