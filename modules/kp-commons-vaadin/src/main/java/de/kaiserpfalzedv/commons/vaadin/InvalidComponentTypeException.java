/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */

package de.kaiserpfalzedv.commons.vaadin;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>InvalidComponentTypeException -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-21
 */
@SuppressWarnings("unused")
@ToString(callSuper = true)
@Getter
@Slf4j
public class InvalidComponentTypeException extends BaseVaadinSystemException {
    private static final String MESSAGE = "I won't buy this object, it is scratched. The component is of type " +
            "'%s'. Type '%s' is expected.";

    Class<?> expected;
    Class<?> current;

    public InvalidComponentTypeException(final Class<?> expected, final Object current) {
        super(String.format(MESSAGE, current.getClass().getSimpleName(), expected.getSimpleName()));

        this.expected = expected;
        this.current = current.getClass();
    }


    public InvalidComponentTypeException(final Class<?> expected, final Object current, final Throwable cause) {
        super(String.format(MESSAGE, current.getClass().getSimpleName(), expected.getSimpleName()), cause);

        this.expected = expected;
        this.current = current.getClass();
    }
}
