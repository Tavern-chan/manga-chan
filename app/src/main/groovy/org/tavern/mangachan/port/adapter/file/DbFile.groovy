package org.tavern.mangachan.port.adapter.file

import org.tavern.mangachan.domain.model.user.MangaUser

class DbFile {
    List<MangaUser> users = new ArrayList<>()

    DbFile() { }

    DbFile(Collection<MangaUser> users) {
        this.users = new ArrayList<>(users)
    }
}
