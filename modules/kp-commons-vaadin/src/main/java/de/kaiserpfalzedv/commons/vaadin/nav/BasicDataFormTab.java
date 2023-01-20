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

package de.kaiserpfalzedv.commons.vaadin.nav;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.tabs.Tab;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@Slf4j
public abstract class BasicDataFormTab<T extends Serializable> extends Tab {
    protected BasicDataForm<T> form;

    protected final FormLayout layout = new FormLayout();


    public abstract String getI18nKey();


    public void setForm(BasicDataForm<T> form) {
        this.form = form;

        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("100px", 1),
                new FormLayout.ResponsiveStep("200px", 2),
                new FormLayout.ResponsiveStep("600px", 4)
        );

        layout.setSizeFull();
    }


    @Override
    public void onAttach(AttachEvent e) {
        super.onAttach(e);
        log.trace("tab attached. initial={}, form={}, tab={}", e.isInitialAttach(), form, this);

        add(layout);

        attachFields();
    }

    protected abstract void attachFields();

    @Override
    public void onDetach(DetachEvent e) {
        super.onDetach(e);
        log.trace("tab detached. form={}, tab={}", form, this);

        layout.removeAll();
        remove(layout);
    }
}
