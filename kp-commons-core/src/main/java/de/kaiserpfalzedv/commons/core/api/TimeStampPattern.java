/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.core.api;

/**
 * OffsetDateTimeStampValidation --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-01-17
 */
public interface TimeStampPattern {
    String VALID_PATTERN = "^[0-9]{4}(-[0-9]{2}){2}T([0-9]{2}:){2}[0-9]{2}.[0-9]{6}+[0-9]{2}:[0-9]{2}$";
    String VALID_PATTERN_MSG = "The timestamp must match the pattern '" + VALID_PATTERN + "'";
    int VALID_LENGTH = 32;
    String VALID_LENGTH_MSG = "The timestamp must be exactely 32 characters long.";
    String VALID_EXAMPLE = "2022-01-04T21:51:00.000000+01:00";
}
