package org.tavern.mangachan.domain.model.user

import org.tavern.mangachan.domain.model.discord.DiscordMemberId
import org.tavern.mangachan.domain.model.mal.MalUserId

import java.util.stream.Stream

interface MangaUserRepository {
    Iterable<MangaUser> getAll();
    Stream<MangaUser> getAllAsStream();
    MangaUser get(DiscordMemberId discordMemberId)
    MangaUser get(MalUserId malUserId)
    void upsert(MangaUser user)
    void remove(MangaUser user)
}