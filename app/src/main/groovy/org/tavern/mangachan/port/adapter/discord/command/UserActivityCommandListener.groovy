package org.tavern.mangachan.port.adapter.discord.command

import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory
import org.tavern.mangachan.domain.model.discord.DiscordMemberId
import org.tavern.mangachan.domain.model.user.MangaUser
import org.tavern.mangachan.domain.model.user.MangaUserRepository
import org.tavern.mangachan.port.adapter.mal.MalApi

import javax.annotation.Nonnull

class UserActivityCommandListener implements CommandListener {
    private static final XLogger logger = XLoggerFactory.getXLogger(UserActivityCommandListener)

    private final MalApi malApi
    private final MangaUserRepository mangaUserRepository

    UserActivityCommandListener(MalApi malApi, MangaUserRepository mangaUserRepository) {
        this.malApi = malApi
        this.mangaUserRepository = mangaUserRepository
    }

    @Override
    CommandData getCommand() {
        Commands.slash('activity', 'Get the most recent 5 manga the user has updated')
            .addOption(OptionType.USER, 'user', 'The user to get the activity of', true)
    }

    @Override
    void handleCommand(@Nonnull SlashCommandInteractionEvent event) {
        User discordUser = event.interaction.getOption('user').getAsUser()
        if (!discordUser) {
            event.interaction.reply("Invalid user").queue()
            return
        }

        MangaUser mangaUser = mangaUserRepository.get(new DiscordMemberId(discordUser.getId()))
        if (!mangaUser) {
            event.interaction.reply("User ${discordUser.effectiveName} is not registered").queue()
            return
        }

        String response = malApi.searchUserManga(mangaUser.malUsername, "list_updated_at", 5)

        logger.info "MAL Response ${response}"

        // TODO: EMM Format response message

        event.interaction.reply("Not implemented").queue()
    }
}
