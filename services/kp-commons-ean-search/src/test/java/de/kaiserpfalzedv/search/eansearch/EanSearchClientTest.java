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

package de.kaiserpfalzedv.search.eansearch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import de.kaiserpfalzedv.services.eansearch.client.EanSearchClient;
import de.kaiserpfalzedv.services.eansearch.client.EanSearchClientConfig;
import de.kaiserpfalzedv.services.eansearch.filter.RequestLimitFilter;
import de.kaiserpfalzedv.services.eansearch.mapper.EanSearchException;
import de.kaiserpfalzedv.services.eansearch.mapper.EanSearchTooManyRequestsException;
import de.kaiserpfalzedv.services.eansearch.mapper.ResponseErrorMapper;
import de.kaiserpfalzedv.services.eansearch.model.EanData;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Unit tests for {@link RequestLimitFilter}
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0 2023-01-17
 */
@SpringBootTest(
    webEnvironment = WebEnvironment.DEFINED_PORT,
    classes = {
        EanSearchClient.class,
        EanSearchClientConfig.class,
        RequestLimitFilter.class,
        ResponseErrorMapper.class
    }
)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableFeignClients(clients = {EanSearchClient.class})
@AutoConfigureWireMock(port = 8089)
@Slf4j
public class EanSearchClientTest  extends AbstractTestBase {

    @Autowired
    private EanSearchClient sut;

    @Autowired
    private RequestLimitFilter filter;

    @PostConstruct
    public void init() {
        super.setLog(log);
        super.setTestSuite("library-lookup");
    }

    @Test
    void shouldIncrementTheCounterInFilterWhenRequestIsDone() {
        try {
            final Set<EanData> result = this.sut.barcodeLookupEAN("978-5-01234-678-9");
            log.info("Result. result={}", result);

            assertEquals(1, result.size());
            assertEquals(15, this.filter.getRemaining());
        } catch (final EanSearchException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void shouldThrowExceptionWhenNoCreditIsLeft() {
        final EanSearchTooManyRequestsException thrown = Assertions.assertThrows(EanSearchTooManyRequestsException.class, () -> {
            this.sut.barcodeLookupEAN("978-5-01234-678-1");
        });

        assertEquals("Too many requests (eg. rate limit exceeded).", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreditsRanOutDuringRequest() {
        this.sut.barcodeLookupEAN("978-5-01234-678-0"); // used the last credit.

        final EanSearchTooManyRequestsException thrown = Assertions.assertThrows(EanSearchTooManyRequestsException.class, () -> {
            this.sut.barcodeLookupEAN("123412312"); // irrelevant query - should be filtered ...
        });

        assertEquals("Too many requests (eg. rate limit exceeded).", thrown.getMessage());
    }

    @Test
    void shouldReturnTheDefaultEntryWhenCalledWithISBN() {
        final Set<EanData> result = this.sut.barcodeLookupISBN("5-01234-678-9");

        assertEquals(1, result.size());
    }

    @BeforeEach
    void resetFilter() {
        this.filter.reset();
    }
}
