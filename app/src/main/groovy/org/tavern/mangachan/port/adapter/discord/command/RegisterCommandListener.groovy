package org.tavern.mangachan.port.adapter.discord.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory
import org.tavern.mangachan.domain.model.discord.DiscordMemberId
import org.tavern.mangachan.domain.model.mal.MalMangaId
import org.tavern.mangachan.domain.model.mal.MalUserId
import org.tavern.mangachan.domain.model.mal.MalUserMangaCacheSupplier
import org.tavern.mangachan.domain.model.user.MangaUser
import org.tavern.mangachan.domain.model.user.MangaUserRepository

import javax.annotation.Nonnull

class RegisterCommandListener implements CommandListener {
    private static final XLogger logger = XLoggerFactory.getXLogger(RegisterCommandListener)

    private final MalUserMangaCacheSupplier cacheSupplier
    private final MangaUserRepository mangaUserRepository

    RegisterCommandListener(MalUserMangaCacheSupplier cacheSupplier, MangaUserRepository mangaUserRepository) {
        this.cacheSupplier = cacheSupplier
        this.mangaUserRepository = mangaUserRepository
    }

    @Override
    CommandData getCommand() {
        Commands.slash('register', 'Register MAL username')
            .addOption(OptionType.STRING, 'username', 'MyAnimeList Username', true)
            .addOption(OptionType.USER, 'user', 'Discord user. Defaults to you', false)
    }

    @Override
    void handleCommand(@Nonnull SlashCommandInteractionEvent event) {
        def optionalUserArg = event.interaction.getOption('user')?.getAsMember()
        def discordName = optionalUserArg ? optionalUserArg.effectiveName : event.member.effectiveName
        def memberId = optionalUserArg ? optionalUserArg.id : event.member.id
        mangaUserRepository.upsert(new MangaUser(new DiscordMemberId(memberId), new MalUserId(event.interaction.getOption('username').getAsString())))
        cacheSupplier.update()
        event.interaction.reply("Registered MAL username ${event.getOption('username').getAsString()}").queue()
    }
}
