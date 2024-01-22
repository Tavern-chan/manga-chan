package org.tavern.mangachan.port.adapter.mal.model.v2.usermanga

import com.fasterxml.jackson.annotation.JsonProperty

class MalV2UserManga {
    MalV2MangaNode node
    @JsonProperty("list_status")
    MalV2UserMangaStatus status
}
