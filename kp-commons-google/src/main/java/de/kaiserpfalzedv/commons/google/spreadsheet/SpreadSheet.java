/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

package de.kaiserpfalzedv.commons.google.spreadsheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;

/**
 * SpreadSheet --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@Builder(setterPrefix = "with", toBuilder = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@AllArgsConstructor
@Getter
@ToString
@Slf4j
public class SpreadSheet {
    @ToString.Exclude
    private final NetHttpTransport transport;
    @ToString.Exclude
    private final JsonFactory factory;
    @ToString.Exclude
    private final Credential credential;
    @Setter
    private String name;

    @ToString.Exclude
    private Sheets sheets;

    public synchronized Sheets open() {
        if (sheets == null) {
            log.debug("Creating new sheets connection: {}", this);
            sheets = new Sheets.Builder(transport, factory, credential)
                    .setApplicationName(name)
                    .build();
        }

        log.info("Opened sheets: {}", this);
        return sheets;
    }

    public Sheets.Spreadsheets sheetCollection() {
        log.trace("Selecting spreadsheets: {}", this);

        return open().spreadsheets();
    }

    public Sheets.Spreadsheets.Get sheet(final String sheet) throws IOException {
        log.debug("Selecting sheet: name='{}'", sheet);

        return sheetCollection().get(sheet);
    }

    public Sheets.Spreadsheets.Get tab(final String sheet, final String tab, final String... titles) throws IOException {
        log.debug("Selecting sheet: name='{}', tab='{}', titles={}", sheet, tab, titles);

        Sheets.Spreadsheets.Get result = sheet(sheet);

        if (result == null) {
            log.debug("Tab does not exist. Creating a new tab");
        }

        return result;
    }

    public Sheets.Spreadsheets.Get newSheet(final String sheet, final String[] titles) {
        log.debug("Creating a new Sheet: ");

        // FIXME 2021-11-13 klenkes74 Implement the newSheet function
        throw new UnsupportedOperationException("not-yet-implemented");
    }
}
