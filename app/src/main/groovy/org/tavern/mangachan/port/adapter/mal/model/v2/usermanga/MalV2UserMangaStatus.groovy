package org.tavern.mangachan.port.adapter.mal.model.v2.usermanga

import com.fasterxml.jackson.annotation.JsonProperty

import java.time.Instant

class MalV2UserMangaStatus {
    String status
    @JsonProperty("is_rereading")
    Boolean isRereading
    @JsonProperty("num_volumnes_read")
    Integer numVolumesRead
    @JsonProperty("num_chapters_read")
    Integer numChaptersRead
    Integer score
    @JsonProperty("updated_at")
    String updatedAt
    @JsonProperty("start_date")
    String startDate
    @JsonProperty("end_date")
    String endDate

}
