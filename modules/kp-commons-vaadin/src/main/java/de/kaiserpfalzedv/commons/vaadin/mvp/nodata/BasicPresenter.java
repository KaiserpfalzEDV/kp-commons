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

package de.kaiserpfalzedv.commons.vaadin.mvp.nodata;

import de.kaiserpfalzedv.commons.api.i18n.HasLocale;
import de.kaiserpfalzedv.commons.vaadin.users.HasUser;

import javax.validation.constraints.NotNull;
import java.util.Locale;

/**
 * <p>Basic Presenter -- Common API to every Presenter.</p>
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2023-01-21
 */
public interface BasicPresenter extends HasUser, HasLocale {
    /**
     * Inserts the view this presenter works on.
     *
     * @param view The basic view of this presenter.
     */
    <V extends BasicView> void setView(@NotNull final V view);

    /**
     * Reads the view this presenter works on.
     */
    <V extends BasicView> V getView();

    <F extends BasicForm> void setForm(@NotNull final F form);

    <F extends BasicForm> F getForm();

    default Locale getLocale() {
        return getUser().getLocale();
    }

    void execute();
    void reset();
    void close();
}
