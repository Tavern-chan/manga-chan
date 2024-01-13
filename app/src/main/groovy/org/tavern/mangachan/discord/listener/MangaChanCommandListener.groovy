package org.tavern.mangachan.discord.listener

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.annotations.NotNull
import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory

import javax.annotation.Nonnull

class MangaChanCommandListener extends ListenerAdapter {
    private static final XLogger logger = XLoggerFactory.getXLogger(MangaChanCommandListener)

    @Override
    void onSlashCommandInteraction(@NotNull @Nonnull SlashCommandInteractionEvent event) {
        logger.info 'Hello World!'
        event.interaction.channel.sendMessage('Hello!').queue()
    }

}
