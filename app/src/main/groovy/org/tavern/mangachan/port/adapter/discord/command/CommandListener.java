package org.tavern.mangachan.port.adapter.discord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;

public interface CommandListener {
    CommandData getCommand();
    void handleCommand(@Nonnull SlashCommandInteractionEvent event);
}
