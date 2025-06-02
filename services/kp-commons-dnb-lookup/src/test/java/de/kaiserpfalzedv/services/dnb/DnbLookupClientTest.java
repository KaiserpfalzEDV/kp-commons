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

package de.kaiserpfalzedv.services.dnb;

import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import de.kaiserpfalzedv.services.dnb.client.DnbLookupClient;
import de.kaiserpfalzedv.services.dnb.client.DnbLookupWebClient;
import de.kaiserpfalzedv.services.dnb.marcxml.MarcConverter;
import de.kaiserpfalzedv.services.dnb.model.Book;
import de.kaiserpfalzedv.services.dnb.model.LibraryLookupException;
import jakarta.annotation.PostConstruct;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link de.kaiserpfalzedv.services.dnb.client.DnbLookupWebClient}
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0 2023-01-17
 */
@SpringBootTest(
    webEnvironment = WebEnvironment.DEFINED_PORT,
    classes = {
        DnbLookupClient.class,
        DnbLookupWebClient.class,
        MarcConverter.class
    }
)
@ActiveProfiles({"test"})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@AutoConfigureWireMock(port = 8089)
@Slf4j
public class DnbLookupClientTest extends AbstractTestBase {

    @Autowired
    DnbLookupClient sut;

    @PostConstruct
    public void init() {
        super.setLog(log);
        super.setTestSuite("library-lookup");
    }

    @Test
    void shouldReturnABookOfListsWhenIsbnIsLookedUp() {
        this.startTest("dnb-lookup", "WOE=978-5-01234-678-9");
        try {
            final List<Book> result = this.sut.lookup("WOE=978-5-01234-678-9");
            log.info("Result. result={}", result);

            assertEquals(1, result.size());
        } catch (final LibraryLookupException e) {
            Assertions.fail(e);
        }
    }
}
