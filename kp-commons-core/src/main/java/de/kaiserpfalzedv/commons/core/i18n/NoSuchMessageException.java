/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.core.i18n;

import de.kaiserpfalzedv.commons.core.api.BaseException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

/**
 * @author rlichti
 * @version 1.2.0 2021-09-08
 * @since 1.2.0 2021-09-08
 */
@Slf4j
public class NoSuchMessageException extends BaseException {

    public NoSuchMessageException(String message) {
        super(message);
    }

    public NoSuchMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
