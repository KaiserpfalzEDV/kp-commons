/*
 * Copyright (c) 2022-2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.vaadin.notifications;

import com.github.rjeschke.txtmark.Processor;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.quarkus.annotation.VaadinServiceEnabled;
import lombok.extern.slf4j.Slf4j;

@VaadinServiceEnabled
@Slf4j
public class ErrorNotification extends Notification {
    public static ErrorNotification show(final String message) {
        log.info("Display error to user. error='{}'", message);

        ErrorNotification result = new ErrorNotification();
        result.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div text = new Div(new Text(message));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            log.info("User acknowledged error. error='{}'", message);
            result.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        result.add(layout);
        result.open();
        return result;
    }

    public static ErrorNotification showMarkdown(final String markdown) {
        return show(Processor.process(markdown));
    }
}
