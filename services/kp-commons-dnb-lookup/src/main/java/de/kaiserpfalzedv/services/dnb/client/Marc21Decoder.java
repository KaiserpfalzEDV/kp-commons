/*
 * Copyright (c) 2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.services.dnb.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.stereotype.Service;

import de.kaiserpfalzedv.services.dnb.marcxml.MarcConverter;
import de.kaiserpfalzedv.services.dnb.model.Book;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

/**
 * <p>DnbConvertMarc21StreamFilter -- Converts the MARC21-xml into a {@link List}&lt;{@link Book}&gt; json list.</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-22
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class Marc21Decoder implements Decoder {

    private final MarcConverter converter;

    @Override
    public Object decode(final Response response, final Type type) throws IOException, DecodeException, FeignException {
        return this.converter.convert(response.body().asInputStream());
    }
}
