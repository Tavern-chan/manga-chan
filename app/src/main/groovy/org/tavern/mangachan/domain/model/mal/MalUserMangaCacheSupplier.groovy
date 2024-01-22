package org.tavern.mangachan.domain.model.mal

import java.util.function.Supplier

interface MalUserMangaCacheSupplier extends Supplier<MalUserMangaCache> {
    void update()
}