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

package de.kaiserpfalzedv.commons.external.sms77.client;

import de.kaiserpfalzedv.commons.external.sms77.WiremockSms77;
import de.kaiserpfalzedv.commons.external.sms77.filter.Sms77RequestReportFilter;
import de.kaiserpfalzedv.commons.external.sms77.mapper.Sms77Exception;
import de.kaiserpfalzedv.commons.external.sms77.model.Sms;
import de.kaiserpfalzedv.commons.external.sms77.model.SmsResult;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Unit tests for {@link Sms77RequestReportFilter}
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@QuarkusTest
@QuarkusTestResource(value = WiremockSms77.class, parallel = true)
@Slf4j
public class Sms77ClientTest {

   @RestClient
   @Inject
   Sms77Client sut;

   @Inject
   Sms77RequestReportFilter filter;

   @Test
   void shouldsendTheSMSWhenT01IsSenta() {
      try {
         SmsResult result = sut.sendSMS(
                 Sms.builder()
                         .to(Set.of("491234567890"))
                         .text("T01")
                         .foreign_id("9272baaa-18a5-4db1-ac23-d03e477a0ebf")
                         .build()
         );
         log.info("Result. result={}", result);

         assertEquals("100", result.getSuccess());
      } catch (Sms77Exception e) {
         Assertions.fail(e);
      }
   }
}
