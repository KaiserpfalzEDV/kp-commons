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

package de.kaiserpfalzedv.commons.core.mongodb;

import io.github.cbartosiak.bson.codecs.jsr310.dayofweek.DayOfWeekAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.duration.DurationAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.instant.InstantAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.localdate.LocalDateAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.localdatetime.LocalDateTimeAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.localtime.LocalTimeAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.month.MonthAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.monthday.MonthDayAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.offsetdatetime.OffsetDateTimeAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.period.PeriodAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.year.YearAsInt32Codec;
import io.github.cbartosiak.bson.codecs.jsr310.yearmonth.YearMonthAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.zoneddatetime.ZonedDateTimeAsDocumentCodec;
import io.github.cbartosiak.bson.codecs.jsr310.zoneid.ZoneIdAsStringCodec;
import io.github.cbartosiak.bson.codecs.jsr310.zoneoffset.ZoneOffsetAsStringCodec;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.james.mime4j.dom.datetime.DateTime;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.time.*;

/**
 * OffsetDateTimeCodecProvider --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@RegisterForReflection
public class Jsr310CodecProvider implements CodecProvider {
    @SuppressWarnings("unchecked")
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == OffsetDateTime.class) {
            return (Codec<T>) new OffsetDateTimeAsDocumentCodec();
        } else if (clazz == OffsetTime.class) {
            return (Codec<T>) new OffsetDateTimeAsDocumentCodec();
        } else if (clazz == DateTime.class) {
            return (Codec<T>) new LocalDateTimeAsDocumentCodec();
        } else if (clazz == LocalDateTime.class) {
            return (Codec<T>) new LocalDateTimeAsDocumentCodec();
        } else if (clazz == LocalDate.class) {
            return (Codec<T>) new LocalDateAsDocumentCodec();
        } else if (clazz == LocalTime.class) {
            return (Codec<T>) new LocalTimeAsDocumentCodec();
        } else if (clazz == ZonedDateTime.class) {
            return (Codec<T>) new ZonedDateTimeAsDocumentCodec();
        } else if (clazz == Instant.class) {
            return (Codec<T>) new InstantAsDocumentCodec();
        } else if (clazz == Period.class) {
            return (Codec<T>) new PeriodAsDocumentCodec();
        } else if (clazz == Duration.class) {
            return (Codec<T>) new DurationAsDocumentCodec();
        } else if (clazz == Year.class) {
            return (Codec<T>) new YearAsInt32Codec();
        } else if (clazz == YearMonth.class) {
            return (Codec<T>) new YearMonthAsDocumentCodec();
        } else if (clazz == Month.class) {
            return (Codec<T>) new MonthAsStringCodec();
        } else if (clazz == MonthDay.class) {
            return (Codec<T>) new MonthDayAsDocumentCodec();
        } else if (clazz == DayOfWeek.class) {
            return (Codec<T>) new DayOfWeekAsStringCodec();
        } else if (clazz == ZoneId.class) {
            return (Codec<T>) new ZoneIdAsStringCodec();
        } else if (clazz == ZoneOffset.class) {
            return (Codec<T>) new ZoneOffsetAsStringCodec();
        }

        return null;
    }
}
