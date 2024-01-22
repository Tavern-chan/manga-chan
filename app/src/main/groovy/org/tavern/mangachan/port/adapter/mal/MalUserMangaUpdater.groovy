package org.tavern.mangachan.port.adapter.mal

import com.fasterxml.jackson.core.type.TypeReference
import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory
import org.tavern.mangachan.domain.model.Constants
import org.tavern.mangachan.domain.model.mal.*
import org.tavern.mangachan.domain.model.user.MangaUser
import org.tavern.mangachan.domain.model.user.MangaUserRepository
import org.tavern.mangachan.json.JacksonJsonParser
import org.tavern.mangachan.port.adapter.mal.model.v2.MalV2PagingResponse
import org.tavern.mangachan.port.adapter.mal.model.v2.usermanga.MalV2UserManga

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MalUserMangaUpdater implements MalUserMangaCacheSupplier {
    private static final XLogger logger = XLoggerFactory.getXLogger(MalUserMangaUpdater)

    private final Executor executor = Executors.newSingleThreadExecutor()
    private final JacksonJsonParser jsonParser
    private final MalApi malApi
    private final MangaUserRepository mangaUserRepository
    private volatile MalUserMangaCache cache = new MalUserMangaCache(new HashMap<>())
    private volatile boolean updating

    MalUserMangaUpdater(JacksonJsonParser jsonParser, MalApi malApi, MangaUserRepository mangaUserRepository) {
        this.jsonParser = jsonParser
        this.malApi = malApi
        this.mangaUserRepository = mangaUserRepository
    }

    @Override
    void update() {
        if (updating) {
            return;
        }

        executor.submit({ ->
            try {
                updating = true
                logger.info "Updating user manga cache"

                Map<MalUserId, MalUserManga> userMangaMap = new HashMap<>()
                for (MangaUser mangaUser: mangaUserRepository.getAll()) {
                    logger.debug "Updating cache for user ${mangaUser.malUsername}"
                    MalUserManga.Builder mangaBuilder = MalUserManga.builder()

                    URI uri = URI.create("https://api.myanimelist.net/v2/users/${URLEncoder.encode(mangaUser.malUsername.id, Constants.DEFAULT_CHARSET)}/mangalist?fields=manga_title,list_status&limit=100")
                    MalV2PagingResponse<MalV2UserManga> response = query(uri)
                    addManga(mangaBuilder, response)

                    while (null != response.paging && null != response.paging.next) {
                        response = query(URI.create(response.paging.next))
                        addManga(mangaBuilder, response)
                    }

                    userMangaMap.put(mangaUser.malUsername, mangaBuilder.build())
                }

                setCache(new MalUserMangaCache(userMangaMap))
                logger.info "Finished updating user manga cache"
            } finally {
                updating = false
            }
        })
    }

    private void addManga(MalUserManga.Builder mangaBuilder, MalV2PagingResponse<MalV2UserManga> response) {
        for (MalV2UserManga v2Manga: response.data) {
            mangaBuilder.add(new MalUserMangaData(
                new MalMangaId(v2Manga.node.id),
                v2Manga.node.title,
                URI.create(v2Manga.node.picture.medium),
                v2Manga.status?.status,
                v2Manga.status?.startDate,
                v2Manga.status?.endDate,
                v2Manga.status?.numChaptersRead
            ))
        }
    }

    private MalV2PagingResponse<MalV2UserManga> query(URI uri) {
        def data = malApi.query(uri)
        return jsonParser.objectMapper.readValue(
            data,
            new TypeReference<MalV2PagingResponse<MalV2UserManga>>() {}
        )
    }

    @Override
    synchronized MalUserMangaCache get() {
        return cache
    }

    private synchronized void setCache(MalUserMangaCache cache) {
        this.cache = cache
    }
}
