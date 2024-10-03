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

package de.kaiserpfalzedv.commons.api.components.model;

/**
 * <p>Imprint -- The data for the imprint Vaadin component</p>
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-19
 */
public interface Imprint {
    /**
     * @return the name of the responsible person for the web page.
     */
    String getResponsiblePerson();

    /**
     * @return the name and full address of the person responsible according to the Mediendienste Staatsvertrag.
     */
    String getResponsibleMStV();

    /**
     * @return the name and full address of the person responsible according to the Rundfunkstaatsvertrag.
     */
    String getResponsibleRStV();

    /**
     * @return Full address of the owner/owning organisation of this web page.
     */
    String getContact();

    /**
     * @return Additional text to be displayed below the imprint.
     */
    String getAdditionalNotes();
}
