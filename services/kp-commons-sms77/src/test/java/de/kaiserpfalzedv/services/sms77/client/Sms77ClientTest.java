/*
 * Copyright (c) 2023-2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.services.sms77.client;

import de.kaiserpfalzedv.services.sms77.mapper.Sms77Exception;
import de.kaiserpfalzedv.services.sms77.mapper.Sms77ResponeMapper;
import de.kaiserpfalzedv.services.sms77.model.NumberFormatCheckResult;
import de.kaiserpfalzedv.services.sms77.model.Sms;
import de.kaiserpfalzedv.services.sms77.model.SmsResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Unit tests for {@link Sms77WebClient}
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {
        Sms77WebClient.class,
        Sms77ResponeMapper.class,
    }
)
@ActiveProfiles({"test"})
@Import({
    Sms77WebClient.class,
    Sms77ResponeMapper.class,
})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@AutoConfigureWireMock(port = 0)
@Slf4j
public class Sms77ClientTest {

   @Autowired
   Sms77WebClient sut;

   @Test
   void shouldsendTheSMSWhenT01IsSenta() {
      try {
         final SmsResult result = this.sut.sendSMS(
               Sms.builder()
                     .to(Set.of("491234567890"))
                     .text("T01")
                     .foreign_id("9272baaa-18a5-4db1-ac23-d03e477a0ebf")
                     .build()
         );
         log.info("Result. result={}", result);

         assertEquals("100", result.getSuccess());
      } catch (final Sms77Exception e) {
         Assertions.fail(e);
      }
   }
   
   @Test
   void shouldCheckTheNumber() {
      try {
         final NumberFormatCheckResult result = this.sut.checkNumberFormat("491234567890");
         log.info("Result. result={}", result);

         assertTrue(result.isSuccess());
      } catch (final Sms77Exception e) {
         Assertions.fail(e);
      }
   }
}
