package org.tavern.mangachan.port.adapter.file

import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory
import org.tavern.mangachan.domain.model.Config
import org.tavern.mangachan.domain.model.discord.DiscordMemberId
import org.tavern.mangachan.domain.model.mal.MalUserId
import org.tavern.mangachan.domain.model.user.MangaUser
import org.tavern.mangachan.domain.model.user.MangaUserRepository
import org.tavern.mangachan.json.JacksonJsonParser

import java.nio.file.Files
import java.util.stream.Stream
import java.util.stream.StreamSupport

class FileMangaUserRepository implements MangaUserRepository {
    private static final XLogger logger = XLoggerFactory.getXLogger(FileMangaUserRepository)

    private final Map<DiscordMemberId, MangaUser> discordToUser = new HashMap<>()
    private final Map<MalUserId, MangaUser> malToUser = new HashMap<>()

    private final Config config
    private final JacksonJsonParser jsonParser;

    FileMangaUserRepository(Config config, JacksonJsonParser jsonParser) {
        this.config = config
        this.jsonParser = jsonParser
    }

    void load() {
        if (Files.exists(config.getDbFile())) {
            try (InputStream inputStream = Files.newInputStream(config.getDbFile())) {
                jsonParser.objectMapper.readValue(inputStream, DbFile.class).getUsers().forEach({ user ->
                    upsert(user)
                })
            } catch (IOException ex) {
                logger.error("Failed to persist", ex)
            }
        } else {
            clear()
        }
    }

    private void clear() {
        discordToUser.clear()
        malToUser.clear()
    }

    private void persist() {
        try (OutputStream outputStream = Files.newOutputStream(config.getDbFile())) {
            jsonParser.objectMapper.writeValue(outputStream, new DbFile(malToUser.values()))
        } catch (IOException ex) {
            logger.error "Failed to persist", ex
        }
    }

    @Override
    Iterable<MangaUser> getAll() {
        return malToUser.values()
    }

    @Override
    Stream<MangaUser> getAllAsStream() {
        return StreamSupport.stream(getAll().spliterator(), false)
    }

    @Override
    MangaUser get(DiscordMemberId discordMemberId) {
        return discordToUser.get(discordMemberId)
    }

    @Override
    MangaUser get(MalUserId malUserId) {
        return malToUser.get(malUserId)
    }

    @Override
    void upsert(MangaUser user) {
        discordToUser.put(user.discordMemberId, user)
        malToUser.put(user.malUsername, user)
        persist()
    }

    @Override
    void remove(MangaUser user) {
        discordToUser.remove(user.discordMemberId)
        malToUser.remove(user.malUsername)
        persist()
    }
}
