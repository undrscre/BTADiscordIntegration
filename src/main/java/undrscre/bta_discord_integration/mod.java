package undrscre.bta_discord_integration;

import net.fabricmc.api.ModInitializer;
import undrscre.bta_discord_integration.server.DiscordChatRelay;
import undrscre.bta_discord_integration.server.DiscordClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class mod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("BTA Discord Integration");
    private static ScheduledExecutorService scheduler;

    @Override
    public void onInitialize() {
        long timestamp = System.currentTimeMillis() / 1000L;
        new Thread(() -> {
            if (DiscordClient.init()) {
                DiscordChatRelay.sendServerStartMessage();
                if (config.discord_changeChannelTopic) {
                    scheduler = Executors.newScheduledThreadPool(1);
                    scheduler.scheduleAtFixedRate(() -> {
                        DiscordChatRelay.updateMemberCount(true, timestamp);
                    }, 0, 6, TimeUnit.MINUTES);
                }
            } else {
                LOGGER.error("Failed to initialize mod");
            }
        }).start();
    }

    public static void shutdownScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
    public static void info(String s) {
        LOGGER.info(s);
    }
}
