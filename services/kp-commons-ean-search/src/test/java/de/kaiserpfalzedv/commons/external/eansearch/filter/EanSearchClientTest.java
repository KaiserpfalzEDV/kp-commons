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

package de.kaiserpfalzedv.commons.external.eansearch.filter;

import de.kaiserpfalzedv.commons.external.eansearch.client.EanSearchClient;
import de.kaiserpfalzedv.commons.external.eansearch.mapper.EanSearchException;
import de.kaiserpfalzedv.commons.external.eansearch.mapper.EanSearchRequestLimitReachedException;
import de.kaiserpfalzedv.commons.external.eansearch.model.EanData;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Unit tests for {@link RequestLimitFilter}
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@QuarkusTest
@QuarkusTestResource(value = WiremockEanSearch.class, parallel = true)
@Slf4j
public class EanSearchClientTest {

   @RestClient
   @Inject
   EanSearchClient sut;

   @Inject
   RequestLimitFilter filter;

   @Test
   void shouldIncrementTheCounterInFilterWhenRequestIsDone() {
      try {
         Set<EanData> result = sut.barcodeLookupEAN("978-5-01234-678-9");
         log.info("Result. result={}", result);

         assertEquals(1, result.size());
         assertEquals(15, filter.getRemaining());
      } catch (EanSearchException e) {
         Assertions.fail(e);
      }
   }

   @Test
   void shouldThrowExceptionWhenNoCreditIsLeft() {
      try {
         Set<EanData> result = sut.barcodeLookupEAN("978-5-01234-678-1");
         log.info("Result. result={}", result);

         Assertions.fail("There should have been an EanSearchException");
      } catch (EanSearchRequestLimitReachedException e) {
         // everything is fine
      }
   }

   @Test
   void shouldThrowExceptionWhenCreditsRanOutDuringRequest() {
      sut.barcodeLookupEAN("978-5-01234-678-0"); // used the last credit.

      try {
         sut.barcodeLookupEAN("123412312"); // irrelevant query - should be filtered ...
      } catch (EanSearchRequestLimitReachedException e) {
         // nice.
      }
   }

   @Test
   void shouldReturnTheDefaultEntryWhenCalledWithISBN() {
      Set<EanData> result = sut.barcodeLookupISBN("5-01234-678-9");

      assertEquals(1, result.size());
   }

   @BeforeEach
   void resetFilter() {
      filter.reset();
   }
}
