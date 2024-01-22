package org.tavern.mangachan.port.adapter.mal

import com.fasterxml.jackson.core.type.TypeReference
import org.apache.http.client.utils.URIBuilder
import org.tavern.mangachan.domain.model.Config
import org.tavern.mangachan.domain.model.Constants
import org.tavern.mangachan.json.JacksonJsonParser
import org.tavern.mangachan.port.adapter.mal.model.v2.MalV2PagingResponse
import org.tavern.mangachan.port.adapter.mal.model.v2.usermanga.MalV2MangaNode
import org.tavern.mangachan.port.adapter.mal.model.v2.usermanga.MalV2Node
import org.tavern.mangachan.port.adapter.mal.model.v2.usermanga.MalV2UserManga

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class MalApi {
    private final Config config
    private final HttpClient httpsClient
    private final JacksonJsonParser jsonParser

    MalApi(Config config, HttpClient httpsClient, JacksonJsonParser jsonParser) {
        this.config = config
        this.httpsClient = httpsClient
        this.jsonParser = jsonParser
    }

    MalV2PagingResponse<MalV2Node<MalV2MangaNode>> searchManga(final String query, final Integer limit, Collection<String> fields) {
        URIBuilder uri = uri()
            .setPath('/v2/manga')
            .setParameter('limit', normalizeLimit(limit, 1, 10).toString())
        if (query) {
            uri.setParameter("q", query)
        }
        if (fields) {
            uri.setParameter("fields", String.join(",", fields))
        }

        String bodyString = httpsClient.send(
            requestBuilder().GET()
                .uri(uri.build())
                .build(),
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        ).body()

        return jsonParser.objectMapper.readValue(
            bodyString,
            new TypeReference<MalV2PagingResponse<MalV2Node<MalV2MangaNode>>>() {}
        )
    }

    String searchUserManga(final String username, final String sort, final Integer limit) {
        URIBuilder uri = uri()
            .setPath("/v2/users/${username}/mangalist")
            .setParameter('limit', normalizeLimit(limit, 5, 10).toString())
        if (sort) {
            uri.setParameter('fields', sort)
        }

        return httpsClient.send(
            requestBuilder().GET()
            .uri(uri.build())
            .build(),
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        )
    }

    String query(URI uri) {
        return httpsClient.send(
            requestBuilder().GET()
                .uri(uri)
                .build(),
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        ).body()
    }

    private HttpRequest.Builder requestBuilder() {
        return HttpRequest.newBuilder()
            .header('X-MAL-CLIENT_ID', config.malClientId)
    }

    private URIBuilder uri() {
        return new URIBuilder()
            .setCharset(Constants.DEFAULT_CHARSET)
            .setScheme("https")
            .setHost("api.myanimelist.net")
            .setPort(443)
    }

    private int normalizeOffset(final Integer offset, final Integer defaultOffset) {
        if (!offset) {
            return defaultOffset;
        } else if (offset < 0) {
            return 0
        } else {
            return offset
        }
    }

    private int normalizeLimit(final Integer limit, final Integer defaultLimit, final Integer maxLimit) {
        if (!limit) {
            return defaultLimit;
        } else if (limit <= 0) {
            return 1
        } else if (maxLimit < limit) {
            return maxLimit
        } else {
            return limit
        }
    }
}
