package org.tavern.mangachan.app

import groovy.transform.PackageScope

class AppConfig {
    private final Properties properties;

    @PackageScope
    AppConfig(Properties properties) {
        this.properties = properties;
    }

    String getDiscordToken() {
        return properties.getProperty("discordToken");
    }
}
