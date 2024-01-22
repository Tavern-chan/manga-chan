package org.tavern.mangachan.domain.model

import java.nio.file.Path

interface Config {
    String getDiscordToken()
    String getMalClientId()
    Path getDbFile()
}