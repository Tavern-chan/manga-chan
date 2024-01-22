package org.tavern.mangachan.port.adapter.discord

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.annotations.NotNull
import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory
import org.tavern.mangachan.port.adapter.discord.command.CommandListener

import javax.annotation.Nonnull

class MangaChanCommandListener extends ListenerAdapter {
    private static final XLogger logger = XLoggerFactory.getXLogger(MangaChanCommandListener)

    private final Map<String, CommandListener> commands;

    MangaChanCommandListener(Map<String, CommandListener> commands) {
        this.commands = commands
    }

    @Override
    void onSlashCommandInteraction(@NotNull @Nonnull SlashCommandInteractionEvent event) {
        String commandId = event.interaction.getName()
        logger.info "User ${event.interaction.member.effectiveName} submitted command ${commandId}"

        CommandListener command = this.commands.get(commandId)
        if (command) {
            logger.trace 'Found command handler'
            command.handleCommand(event)
        } else {
            logger.error "No command found for ${commandId}"
            event.interaction.reply("No command found for ${commandId}")
        }
    }

}
