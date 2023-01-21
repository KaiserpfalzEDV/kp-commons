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

package de.kaiserpfalzedv.commons.vaadin.mvp.data;

import com.vaadin.flow.component.AttachEvent;
import de.kaiserpfalzedv.commons.vaadin.mvp.nodata.BasicViewImpl;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * <p>BasicDataViewImpl -- Basis for the concrete views.</p>
 *
 * @author klenkes {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-12-30
 *
 * @param <T> The data to be displayed
 */
@SuppressWarnings("unused")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
public abstract class BasicDataViewImpl<T extends Serializable> extends BasicViewImpl<T> implements BasicDataView<T> {


    public BasicDataViewImpl(final BasicDataPresenter<T> presenter, final BasicDataForm<T> form) {
        super(presenter, form);
    }

    @SuppressWarnings("unchecked")
    protected BasicDataPresenter<T> presenter() {
        return (BasicDataPresenter<T>) presenter;
    }

    @SuppressWarnings("unchecked")
    protected BasicDataForm<T> form() {
        return (BasicDataForm<T>) form;
    }

    @Override
    public void setData(T data) {
        form().setData(data);

        updateView();
    }

    public T getData() {
        return form().getData();
    }

    public void onAttach(final AttachEvent attachEvent) {
        log.trace("view attached. view={}, event={}", this, attachEvent);
        super.onAttach(attachEvent);

        registerListener(BasicDataForm.SaveEvent.class, e -> presenter().save());
        registerListener(BasicDataForm.DeleteEvent.class, e -> presenter().delete());
    }
}
