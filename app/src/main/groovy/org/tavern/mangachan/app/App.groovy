package org.tavern.mangachan.app

import com.google.common.util.concurrent.ThreadFactoryBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory
import org.tavern.mangachan.domain.model.user.MangaUserRepository
import org.tavern.mangachan.json.JacksonJsonParser
import org.tavern.mangachan.port.adapter.discord.MangaChanCommandListener
import org.tavern.mangachan.port.adapter.discord.command.CommandListener
import org.tavern.mangachan.port.adapter.discord.command.RegisterCommandListener
import org.tavern.mangachan.port.adapter.discord.command.SearchCommandListener
import org.tavern.mangachan.port.adapter.discord.command.UpdateListsCommandListener
import org.tavern.mangachan.port.adapter.discord.command.UserActivityCommandListener
import org.tavern.mangachan.port.adapter.mal.MalApi
import org.tavern.mangachan.port.adapter.mal.MalUserMangaUpdater
import org.tavern.mangachan.port.adapter.file.FileMangaUserRepository

import java.net.http.HttpClient
import java.util.concurrent.Executors
import java.util.function.Function
import java.util.stream.Collectors

class App {
    public static final XLogger logger = XLoggerFactory.getXLogger(App);

    static void main(String[] args) {
        logger.info 'Starting'

        AppConfig config
        try {
             config = loadAppConfig(args)
        } catch (Exception ex) {
            logger.error("Failed to load app properties file", ex)
            System.exit(1)
            return
        }

        logger.info "Initializing Services"
        HttpClient httpsClient = HttpClient.newBuilder().build()
        JacksonJsonParser jsonParser = new JacksonJsonParser()
        MalApi malApi = new MalApi(config, httpsClient, jsonParser)

        FileMangaUserRepository mangaUserRepository = new FileMangaUserRepository(config, jsonParser)
        mangaUserRepository.load()
        MalUserMangaUpdater malUserMangaUpdater = new MalUserMangaUpdater(jsonParser, malApi, mangaUserRepository)
        malUserMangaUpdater.update()

        logger.debug 'Creating command data'
        List<CommandListener> commands = [
            new RegisterCommandListener(malUserMangaUpdater, mangaUserRepository),
            new SearchCommandListener(malApi, mangaUserRepository, malUserMangaUpdater),
            new UpdateListsCommandListener(malUserMangaUpdater),
            new UserActivityCommandListener(malApi, mangaUserRepository)
        ]

        logger.info 'Connecting to Discord'
        JDA jda = JDABuilder.createLight(config.discordToken, Collections.emptyList())
            .setActivity(Activity.customStatus('Reading'))
            .setEventPool(Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat('JDA Thread %d').build()))
            .addEventListeners(new MangaChanCommandListener(commands.stream().collect(Collectors.toMap(
                { c -> c.command.name },
                Function.identity()
            ))))
            .setAutoReconnect(true)
            // Apparently need intents for slash commands even though JDA Docs say we don't
            .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
            .build()

        logger.info 'Registering Commands'
        jda.updateCommands().addCommands(
            commands.stream()
                .map(CommandListener::getCommand)
                .collect(Collectors.toList())
        ).queue()

        logger.info 'Done init'
    }

    private static AppConfig loadAppConfig(String[] args) {
        String appConfigLocation = './manga-chan.properties'

        if (null != args && 1 == args.length) {
            appConfigLocation = args[0]
        }

        Properties properties = new Properties()
        properties.load(new FileInputStream(new File(appConfigLocation)))
        return new AppConfig(properties)
    }

}
