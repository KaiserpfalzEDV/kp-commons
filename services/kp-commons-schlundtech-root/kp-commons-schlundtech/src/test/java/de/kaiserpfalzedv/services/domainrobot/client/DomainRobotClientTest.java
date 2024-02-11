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

package de.kaiserpfalzedv.services.domainrobot.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.openfeign.EnableFeignClients;

import lombok.extern.slf4j.Slf4j;


/**
 * Unit tests for {@link Sms77RequestReportFilter}
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {
      DomainRobotClient.class,
      DomainRobotClientConfig.class
})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableFeignClients
@AutoConfigureWireMock(port = 8089)
@Slf4j
public class DomainRobotClientTest {

   @Autowired
   DomainRobotClient sut;

   @Test
   void shouldsendTheSMSWhenT01IsSenta() {
      final int result = this.sut.sendSMS("test");
      log.info("Result. result={}", result);

      assertEquals(100, result);
   }
}
