package org.tavern.mangachan.app

import groovy.transform.PackageScope
import org.tavern.mangachan.domain.model.Config

import java.nio.file.Path
import java.nio.file.Paths

class AppConfig implements Config {
    private final Properties properties;

    @PackageScope
    AppConfig(Properties properties) {
        this.properties = properties;
    }

    @Override
    String getDiscordToken() {
        return properties.getProperty("discordToken");
    }

    @Override
    String getMalClientId() {
        return properties.getProperty("malClientId");
    }

    @Override
    Path getDbFile() {
        return Paths.get(properties.getProperty("dbFileLocation"))
    }
}
