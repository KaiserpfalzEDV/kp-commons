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

package de.kaiserpfalzedv.commons.discord;

/**
 * IgnoreBotsException -- The plugin does not work on events created by bots.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
public class IgnoreBotsException extends DiscordPluginException {
    /**
     * @param plugin the plugin throwing this exception.
     */
    public IgnoreBotsException(@SuppressWarnings("CdiInjectionPointsInspection") final DiscordPlugin plugin) {
        super(plugin, "Don't work on messages of bots");
    }
}
