package de.kaiserpfalzedv.commons.google.spreadsheet;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * TabCreationFailedSystemException -- Not all tabs could be created.
 *
 * When a user tries to create multiple tabs on a spreadsheet, some (or all) may fail.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-12-28
 */
@Slf4j
@Getter
public class TabCreationFailedException extends SpreadSheetBaseException {
    private final String[] titles;
    private final String[] failedTitles;

    public TabCreationFailedException(final String sheet, final String[] titles, final Collection<String> failedTitles) {
        super(sheet, "Some tab(s) could not be created in sheet.");

        this.titles = titles;
        this.failedTitles = failedTitles.toArray(new String[] {});
    }
}
