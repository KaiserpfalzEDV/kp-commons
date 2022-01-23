/*
 * Copyright (c) 2022 Kaiserpfalz EDV-Service, Roland T. Lichti
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.discord;

import de.kaiserpfalzedv.commons.core.resources.Metadata;
import de.kaiserpfalzedv.commons.core.resources.Pointer;
import de.kaiserpfalzedv.commons.core.user.User;
import de.kaiserpfalzedv.commons.core.user.UserStoreService;

import java.util.HashMap;
import java.util.Optional;

import static de.kaiserpfalzedv.commons.discord.guilds.Guild.DISCORD_NAMESPACE;

/**
 * A single command of a plugin. For your plugin define a marker interface from this and let inject the matching
 * classes into your plugin.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
public interface DiscordPluginCommand {
    /**
     * @return The command.
     */
    String getCommand();

    /**
     * @param input The input message.
     * @return returns the argument (without the command).
     */
    default String getArgument(final String input) {
        return input.replaceFirst(getCommand() + "\\s+", "");
    }

    /**
     * @return the help line for this command.
     */
    default String getHelp() {
        return formatHelp("sorry, no help defined.");
    }

    /**
     * Formats the help line if you overwrite {@link #getHelp()} you should use this helper method.
     *
     * @param description The line describing the command.
     * @return the formatted line.
     */
    default String formatHelp(final String description) {
        return String.format("%10s %s", getCommand(), description);
    }

    /**
     * Executes with the message (stripped of the prefixes and the command).
     *
     * @param context The context to run the command.
     * @throws DiscordPluginException If there is a problem.
     */
    void execute(final DiscordPluginContext context) throws DiscordPluginException;

    /**
     * Loads the TOMB user matching the discord user from the UserStoreService.
     *
     * @param context   The command context.
     * @param userStore The userstore to load the user from.
     * @return Either the stored user or a newly created one.
     */
    default User loadUserFromStoreOrCreateNewUser(final DiscordPluginContext context, final UserStoreService userStore) {
        net.dv8tion.jda.api.entities.User user = context.getUser();

        Optional<User> store = userStore.findByNameSpaceAndName(DISCORD_NAMESPACE, user.getName());
        User result;

        if (store.isEmpty()) {
            HashMap<String, String> annotations = new HashMap<>(3);
            annotations.put("discord-id", user.getId());
            annotations.put("discord-avatar-id", user.getAvatarId());
            annotations.put("discord-avatar-url", user.getEffectiveAvatarUrl());

            result = User.builder()
                    .metadata(
                            Metadata.builder()
                                    .identity(
                                            Pointer.builder()
                                                    .kind(User.KIND)
                                                    .apiVersion(User.API_VERSION)
                                                    .nameSpace(DISCORD_NAMESPACE)
                                                    .name(context.getUser().getName())
                                                    .build()
                                    )
                                    .annotations(annotations)
                                    .build()
                    )
                    .build();
        } else {
            result = store.get();
        }

        return result;
    }
}
