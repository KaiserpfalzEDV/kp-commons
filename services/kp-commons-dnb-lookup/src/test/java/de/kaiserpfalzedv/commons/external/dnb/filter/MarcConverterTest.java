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

import de.kaiserpfalzedv.commons.external.dnb.client.DnbLookupCounterFilter;
import de.kaiserpfalzedv.commons.external.dnb.marcxml.MarcConverter;
import de.kaiserpfalzedv.commons.external.dnb.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * Unit tests for {@link DnbLookupCounterFilter}
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 3.0.0  2023-01-17
 */
@Slf4j
public class MarcConverterTest {

   MarcConverter sut = new MarcConverter();

   private InputStream is;

   @Test
   void shouldDeliverJsonWriterWhenLexArcanaMarc21XmlIsFead() {
      List<Book> result = sut.convertToJson(is);

      log.debug("sut generated a list of books. result={}", result);
   }

   @BeforeEach
   protected void setUpLexArcanaInputStream() {
      loadNewInputStream("__files/lex-arcana.marc21.xml");
   }

   private void loadNewInputStream(final String fileName) {
      closeInputStream(is);

      is = getClass().getClassLoader().getResourceAsStream(fileName);

      log.debug("Working on XML. file='{}'", getClass().getClassLoader().getResource(fileName));
   }


   private void closeInputStream(InputStream is) {
      if (is != null) {
         try {
            is.close();
         } catch (IOException e) {
            log.error("Input stream had some problems: " + e.getMessage(), e);
         }
      }
   }
}
