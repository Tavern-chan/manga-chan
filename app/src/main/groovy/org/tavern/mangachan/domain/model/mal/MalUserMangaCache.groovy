package org.tavern.mangachan.domain.model.mal

class MalUserMangaCache {
    private final Map<MalUserId, MalUserManga> userManga

    MalUserMangaCache(Map<MalUserId, MalUserManga> userManga) {
        this.userManga = userManga
    }

    MalUserManga get(MalUserId userId) {
        return userManga.get(userId)
    }
}
