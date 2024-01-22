package org.tavern.mangachan.domain.model.mal

class MalUserManga {
    private final Map<MalMangaId, MalUserMangaData> idToManga
    private final Map<String, MalUserMangaData> titleToManga

    private MalUserManga(Map<MalMangaId, MalUserMangaData> idToManga, Map<String, MalUserMangaData> titleToManga) {
        this.idToManga = idToManga
        this.titleToManga = titleToManga
    }

    MalUserMangaData get(MalMangaId mangaId) {
        return idToManga.get(mangaId)
    }

    MalUserMangaData get(String title) {
        return titleToManga.get(title)
    }

    boolean contains(MalMangaId mangaId) {
        return idToManga.containsKey(mangaId)
    }

    static Builder builder() {
        return new Builder()
    }

    static class Builder {
        private final Map<MalMangaId, MalUserMangaData> idToManga = new HashMap<>()
        private final Map<String, MalUserMangaData> titleToManga = new HashMap<>()

        private Builder() { }

        Builder add(MalUserMangaData mangaData) {
            idToManga.put(mangaData.id, mangaData)
            titleToManga.put(mangaData.title, mangaData)
            return this
        }

        MalUserManga build() {
            return new MalUserManga(
                idToManga,
                titleToManga
            )
        }
    }

}
