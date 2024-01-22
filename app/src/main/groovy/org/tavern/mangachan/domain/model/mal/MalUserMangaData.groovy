package org.tavern.mangachan.domain.model.mal

final class MalUserMangaData {
    private final MalMangaId id
    private final String title
    private final URI pictureUri

    private final String status
    private final String startDate
    private final String endDate
    private final Integer chaptersRead

    private MalUserMangaData() {}

    MalUserMangaData(MalMangaId id, String title, URI pictureUri, String status, String startDate, String endDate, Integer chaptersRead) {
        this.id = id
        this.title = title
        this.pictureUri = pictureUri
        this.status = status
        this.startDate = startDate
        this.endDate = endDate
        this.chaptersRead = chaptersRead
    }

    MalMangaId getId() {
        return id
    }

    String getTitle() {
        return title
    }

    URI getPictureUri() {
        return pictureUri
    }

    String getStatus() {
        return status
    }

    String getStartDate() {
        return startDate
    }

    String getEndDate() {
        return endDate
    }

    Integer getChaptersRead() {
        return chaptersRead
    }
}
