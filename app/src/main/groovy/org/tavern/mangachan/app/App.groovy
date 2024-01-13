package org.tavern.mangachan.app

import com.google.common.util.concurrent.ThreadFactoryBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory
import org.tavern.mangachan.discord.listener.MangaChanCommandListener

import java.util.concurrent.Executors

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

        logger.info 'Connecting to Discord'
        JDA jda = JDABuilder.createLight(config.discordToken, Collections.emptyList())
            .setActivity(Activity.customStatus('Reading'))
            .setEventPool(Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat('JDA Thread %d').build()))
            .addEventListeners(new MangaChanCommandListener())
            .setAutoReconnect(true)
            // Apparently need intents for slash commands even though JDA Docs say we don't
            .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
            .build()

        logger.info 'Registering Commands'
        jda.updateCommands().addCommands(
            Commands.slash("test", "testing")
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
