package org.tavern.mangachan.port.adapter.mal.model.v2.usermanga

import com.fasterxml.jackson.annotation.JsonProperty

class MalV2MangaNode {
    String id
    String title
    @JsonProperty("main_picture")
    MalV2UserMangaNodePicture picture
    String synopsis
    @JsonProperty("num_chapters")
    Integer chapters
}
