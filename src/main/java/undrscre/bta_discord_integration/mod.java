package undrscre.bta_discord_integration;

import net.fabricmc.api.ModInitializer;
import undrscre.bta_discord_integration.server.DiscordChatRelay;
import undrscre.bta_discord_integration.server.DiscordClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class mod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("BTA Discord Integration");

    @Override
    public void onInitialize() {
        new Thread(() -> {
            if (DiscordClient.init()) {
                DiscordChatRelay.sendServerStartMessage();
            } else {
                LOGGER.error("FUCKK FUCKK FUCK FUCK FUCKKKKKK");
            }
        }).start();
    }

    public static void info(String s) {
        LOGGER.info(s);
    }
}
