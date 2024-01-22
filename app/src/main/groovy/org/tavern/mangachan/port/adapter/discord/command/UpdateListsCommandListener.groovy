package org.tavern.mangachan.port.adapter.discord.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.tavern.mangachan.domain.model.mal.MalUserMangaCacheSupplier

import javax.annotation.Nonnull

class UpdateListsCommandListener implements CommandListener {
    private final MalUserMangaCacheSupplier cacheSupplier

    UpdateListsCommandListener(MalUserMangaCacheSupplier cacheSupplier) {
        this.cacheSupplier = cacheSupplier
    }

    @Override
    CommandData getCommand() {
        Commands.slash('update', "Force update user manga cache. Otherwise lists are updated once a day")
    }

    @Override
    void handleCommand(@Nonnull SlashCommandInteractionEvent event) {
        cacheSupplier.update()
        event.interaction.reply("Updated Cache").queue()
    }
}
