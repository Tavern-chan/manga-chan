package org.tavern.mangachan.domain.model.mal

import org.tavern.mangachan.domain.model.Identifier

final class MalMangaId extends Identifier {
    MalMangaId(String id) {
        super(id)
    }

    MalMangaId(Integer id) {
        super(Objects.toString(id, "-1"))
    }
}
