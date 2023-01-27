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

package de.kaiserpfalzedv.commons.external.dnb.filter;

import de.kaiserpfalzedv.commons.external.dnb.client.DnbLookupClient;
import de.kaiserpfalzedv.commons.external.dnb.client.DnbLookupCounterFilter;
import de.kaiserpfalzedv.commons.external.dnb.model.Book;
import de.kaiserpfalzedv.commons.external.dnb.model.DnbLookupException;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Unit tests for {@link DnbLookupCounterFilter}
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@QuarkusTest
@QuarkusTestResource(value = WiremockDnbLookup.class, parallel = true)
@Slf4j
public class DnbLookupClientTest {

   @RestClient
   @Inject
   DnbLookupClient sut;

   @Test
   void shouldIncrementTheCounterInFilterWhenRequestIsDone() {
      try {
        List<Book> result = sut.dnbLookup("WOE=978-5-01234-678-9");
         log.info("Result. result={}", result);

         assertEquals(1, result.size());
      } catch (DnbLookupException e) {
         Assertions.fail(e);
      }
   }
}
