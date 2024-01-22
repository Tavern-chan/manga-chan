package org.tavern.mangachan.domain.model.discord

import org.tavern.mangachan.domain.model.Identifier

final class DiscordMemberId extends Identifier {

    private DiscordMemberId() {}

    DiscordMemberId(String id) {
        super(id)
    }
}
