package de.kaiserpfalzedv.commons.google.spreadsheet;

import de.kaiserpfalzedv.commons.google.GoogleBaseSystemException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SpreadSheetException --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-12-28
 */
@Slf4j
@Getter
public abstract class SpreadSheetBaseSystemException extends GoogleBaseSystemException {
    private final String sheet;

    public SpreadSheetBaseSystemException(final String sheet, final String message) {
        super(message);

        this.sheet = sheet;
    }

    public SpreadSheetBaseSystemException(final String sheet, final String message, final Throwable cause) {
        super(message, cause);

        this.sheet = sheet;
    }

    public SpreadSheetBaseSystemException(final String sheet, final Throwable cause) {
        super(cause);

        this.sheet = sheet;
    }

    public SpreadSheetBaseSystemException(final String sheet, final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

        this.sheet = sheet;
    }
}
