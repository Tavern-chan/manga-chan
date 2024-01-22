package org.tavern.mangachan.domain.model.user

import org.tavern.mangachan.domain.model.discord.DiscordMemberId
import org.tavern.mangachan.domain.model.mal.MalUserId

class MangaUser {
    private final DiscordMemberId discordMemberId
    private final MalUserId malUsername

    private MangaUser() {}

    MangaUser(DiscordMemberId discordMemberId, MalUserId malUsername) {
        this.discordMemberId = discordMemberId
        this.malUsername = malUsername
    }

    DiscordMemberId getDiscordMemberId() {
        return discordMemberId
    }

    MalUserId getMalUsername() {
        return malUsername
    }
}
