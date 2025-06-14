/*
 * Copyright (c) 2023-2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.services.sms77.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

/**
 * <p>SmsResult -- .</p>
 *
 * <pre>{
 *           "success": "100",
 *           "total_price": 0.075,
 *           "balance": 0.125,
 *           "debug": "false",
 *           "sms_type": "direct",
 *           "messages": [
 *             {
 *               "id": "77196885479",
 *               "sender": "D12",
 *               "recipient": "491234567890",
 *               "text": "T02",
 *               "encoding": "gsm",
 *               "label": null,
 *               "parts": 1,
 *               "udh": null,
 *               "is_binary": false,
 *               "price": 0.075,
 *               "success": true,
 *               "error": null,
 *               "error_text": null
 *             },
 *             {
 *               "id": "77196885479",
 *               "sender": "D12",
 *               "recipient": "492345678901",
 *               "text": "T02",
 *               "encoding": "gsm",
 *               "label": null,
 *               "parts": 1,
 *               "udh": null,
 *               "is_binary": false,
 *               "price": 0.075,
 *               "success": true,
 *               "error": null,
 *               "error_text": null
 *             }
 *           ]
 * }</pre>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-22
 */
@Jacksonized
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class SmsResult {
    @ToString.Include
    private String success;
    @ToString.Include
    private Double total_price;
    @ToString.Include
    private Double balance;
    private String debug;
    @ToString.Include
    private String sms_type;
    
    @ToString.Include
    private Set<MessageResult> messages;

    /**
     * <p>MessageResult -- The resulf of a single SMS.</p>
     *
     * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
     * @since 1.0.0  2023-01-22
     */
    @Jacksonized
    @SuperBuilder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString(onlyExplicitlyIncluded = true)
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    public static class MessageResult {
        @ToString.Include
        private String id;
        private String sender;
        private String recipient;
        private String text;
        private String encoding;
        @ToString.Include
        private String label;
        private Integer parts;
        private String udh;
        private boolean is_binary;
        @ToString.Include
        private Double price;
        @ToString.Include
        private boolean success;
        @ToString.Include
        private Integer error;
        private String error_text;
    }
}
