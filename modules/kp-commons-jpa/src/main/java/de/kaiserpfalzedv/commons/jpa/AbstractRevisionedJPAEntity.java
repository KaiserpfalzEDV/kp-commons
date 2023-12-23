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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@MappedSuperclass
@RevisionEntity
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class AbstractRevisionedJPAEntity extends AbstractJPAEntity {
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DbSequence")
    @SequenceGenerator(name = "DbSequence", sequenceName = "hibernate_sequence")
    @Column(name = "REVID", nullable = false, updatable = false)
    protected int revId;

    @RevisionTimestamp
    @Column(name = "REVISIONED", insertable = true, updatable = false)
    @NonNull
    protected OffsetDateTime revisioned;


    @Override
    public AbstractRevisionedJPAEntity clone() throws CloneNotSupportedException {
        AbstractRevisionedJPAEntity result = (AbstractRevisionedJPAEntity) super.clone();

        result.revId = revId;

        if (revisioned != null) {
            result.revisioned = OffsetDateTime.from(revisioned);
        }

        return result;
    }
}
