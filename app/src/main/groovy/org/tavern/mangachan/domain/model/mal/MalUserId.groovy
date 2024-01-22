package org.tavern.mangachan.domain.model.mal

import org.tavern.mangachan.domain.model.Identifier

final class MalUserId extends Identifier {
    private MalUserId() {}

    MalUserId(String id) {
        super(id)
    }
}
