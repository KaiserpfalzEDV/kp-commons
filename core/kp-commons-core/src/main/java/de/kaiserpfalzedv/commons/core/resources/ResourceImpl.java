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

package de.kaiserpfalzedv.commons.core.resources;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.kaiserpfalzedv.commons.api.resources.Metadata;
import de.kaiserpfalzedv.commons.api.resources.Resource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Resource -- A generic resource holding a single data set.
 *
 * @param <D> The data set provided by this resource.
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 3.0.0  2023-01-07
 * @version 2.1.0  2022-01-16
 * @since 2.0.0  2021-05-24
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REF2", justification = "Use of lombok provided builder.")
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonPropertyOrder({"metadata", "spec", "status"})
public class ResourceImpl<D extends Serializable> implements Resource<D> {
    /** serial class version */
    private static final long serialVersionUID = 0L;

    @Schema(
            name = "metadata",
            description = "Technical data to the resource.",
            required = true
    )
    @NotNull
    @ToString.Include
    @EqualsAndHashCode.Include
    protected Metadata metadata;


    @Schema(
            name = "spec",
            description = "The resource data itself.",
            required = true
    )
    @Builder.Default
    protected D spec = null;


    @Schema(
            name = "status",
            description = "The status of the resource (containing the history).",
            nullable = true
    )
    @Builder.Default
    @SuppressFBWarnings(value = {"EI_EXPOSE_REP","EI_EXPOSE_REP2"}, justification = "lombok provided @Getter are created")
    protected StatusImpl status = null;


    @Override
    @JsonIgnore
    public PointerImpl toPointer() {
        return PointerImpl.builder()
                .kind(this.getKind())
                .apiVersion(this.getApiVersion())
                .nameSpace(this.getNameSpace())
                .nameSpace(this.getName())
                .build();
    }

    @JsonIgnore
    @Override
    public String getKind() {
        return this.getMetadata().getKind();
    }

    @JsonIgnore
    @Override
    public String getApiVersion() {
        return this.getMetadata().getApiVersion();
    }

    @JsonIgnore
    @Override
    public String getNameSpace() {
        return this.getMetadata().getNameSpace();
    }

    @JsonIgnore
    @Override
    public String getName() {
        return this.getMetadata().getName();
    }

    @Override
    @JsonIgnore
    public UUID getUid() {
        return this.metadata.getUid();
    }

    @Override
    @JsonIgnore
    public Integer getGeneration() {
        return this.metadata.getGeneration();
    }


    @JsonIgnore
    public Optional<D> getData() {
        return Optional.ofNullable(this.spec);
    }

    @JsonIgnore
    public Optional<StatusImpl> getState() {
        return Optional.ofNullable(this.status);
    }

    @Override
    @JsonIgnore
    public String getSelfLink() {
        return this.getMetadata().getSelfLink();
    }


    @Override
    synchronized public ResourceImpl<D> increaseGeneration() {
        return this.toBuilder()
                .metadata(this.getMetadata().increaseGeneration())
                .build();
    }


    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @SuppressFBWarnings(value = "CN_IDIOM_NO_SUPER_CALL", justification = "Using the lombok builder.")
    @Override
    public ResourceImpl<D> clone() {
        return this.toBuilder().build();
    }
}
