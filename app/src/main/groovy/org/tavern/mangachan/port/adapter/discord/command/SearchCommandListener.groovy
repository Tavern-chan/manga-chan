package org.tavern.mangachan.port.adapter.discord.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory
import org.tavern.mangachan.domain.model.mal.MalMangaId
import org.tavern.mangachan.domain.model.mal.MalUserMangaCacheSupplier
import org.tavern.mangachan.domain.model.user.MangaUserRepository
import org.tavern.mangachan.port.adapter.mal.MalApi
import org.tavern.mangachan.port.adapter.mal.model.v2.MalV2PagingResponse
import org.tavern.mangachan.port.adapter.mal.model.v2.usermanga.MalV2MangaNode
import org.tavern.mangachan.port.adapter.mal.model.v2.usermanga.MalV2Node

import javax.annotation.Nonnull
import java.util.stream.Collectors

class SearchCommandListener implements CommandListener {
    private static final XLogger logger = XLoggerFactory.getXLogger(SearchCommandListener)

    private final MalApi malApi
    private final MangaUserRepository mangaUserRepository
    private final MalUserMangaCacheSupplier cacheSupplier

    SearchCommandListener(MalApi malApi, MangaUserRepository mangaUserRepository, MalUserMangaCacheSupplier cacheSupplier) {
        this.malApi = malApi
        this.mangaUserRepository = mangaUserRepository
        this.cacheSupplier = cacheSupplier
    }

    @Override
    CommandData getCommand() {
        Commands.slash('search', 'Search for a manga')
            .addOption(OptionType.STRING, 'query', 'Search MAL for a manga', true)
            .addOption(OptionType.INTEGER, 'limit', 'Max number of return results. 1-10. Default 1')
            .addOption(OptionType.BOOLEAN, 'ephemeral', 'Do not show response to other users')
    }

    @Override
    void handleCommand(@Nonnull SlashCommandInteractionEvent event) {
        def ephemeral = event.getOption('ephemeral')
        if (ephemeral) {
            ephemeral = ephemeral.getAsBoolean()
        } else {
            ephemeral = true
        }

        MalV2PagingResponse<MalV2Node<MalV2MangaNode>> response = malApi.searchManga(
            event.getOption('query', OptionMapping::getAsString),
            event.getOption('limit', OptionMapping::getAsInt),
            ['id', 'title', 'main_picture', 'synopsis', 'num_chapters']
        )

        logger.info "MAL Returned ${response.data.size()} manga"

        event.interaction.replyEmbeds(
            response.data.stream()
                .map(MalV2Node::getNode)
                .map(this::embedFromManga)
                .collect(Collectors.toList())
        ).setEphemeral(ephemeral).queue()
    }

    private MessageEmbed embedFromManga(MalV2MangaNode manga) {
        String malUserData = ''

        mangaUserRepository.getAllAsStream().forEach({ user ->
            def userMangaData = cacheSupplier.get().get(user.malUsername)
            def mangaId = new MalMangaId(manga.id)
            if (userMangaData && userMangaData.contains(mangaId)) {
                def mangaData = userMangaData.get(mangaId)

                if (mangaData.status == "reading") {
                    malUserData += "${user.malUsername} = ${mangaData.status} (${mangaData.chaptersRead}/${manga.chapters ?: "??"})\n"
                } else {
                    malUserData += "${user.malUsername} = ${mangaData.status}\n"
                }
            }
        })

        String url = "https://myanimelist.net/manga/${manga.id}"
        return new EmbedBuilder()
            .setTitle(manga.title, url)
            .setImage(manga?.picture?.medium)
            .setUrl(url)
            .setDescription(malUserData)
            // .setDescription(manga.synopsis)
            .build()
    }

}
