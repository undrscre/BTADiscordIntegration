package undrscre.bta_discord_integration.server;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.minecraft.server.MinecraftServer;
import undrscre.bta_discord_integration.config;
import net.minecraft.core.net.command.TextFormatting;
import net.dv8tion.jda.api.managers.channel.middleman.StandardGuildMessageChannelManager;
import java.time.Instant;

public class DiscordChatRelay {

    private static final JDAWebhookClient webhookClient = DiscordClient.getWebhook();

    public static void sendToMinecraft(String author, String message) {
        MinecraftServer server = MinecraftServer.getInstance();
        message = "[" + TextFormatting.PURPLE + "✦" + TextFormatting.RESET + "] <" + author + "> " + message;
        undrscre.bta_discord_integration.mod.info(message);
        String[] lines = message.split("\n");
        for (String chatMessage : lines) {
            server.playerList.sendEncryptedChatToAllPlayers(chatMessage);
        }
    }

    @SuppressWarnings("rawtypes")
    public static void updateMemberCount(Boolean status, long start_timestamp) {
        MinecraftServer server = MinecraftServer.getInstance();
        StandardGuildMessageChannelManager manager = DiscordClient.getChannel().getManager();
        String playerCount = Integer.toString(server.playerList.playerEntities.size());
        String maxPlayerCount = Integer.toString(server.maxPlayers);
        try {
            if (!status) {
                manager.setTopic(config.discord_channelTopicMessage
                    .replace("{serverStatus}", "❌")
                    .replace("{playerCount}", playerCount)
                    .replace("{maxPlayers}", maxPlayerCount)
                    .replace("{startTimestamp}", "[CURRENTLY STOPPED]")
                    .replace("{updateTimestamp}", String.format("<t:%s:R>", System.currentTimeMillis() / 1000L))
                ).complete();
            } else {
                manager.setTopic(config.discord_channelTopicMessage
                    .replace("{serverStatus}", "✅")
                    .replace("{playerCount}", playerCount)
                    .replace("{maxPlayers}", maxPlayerCount)
                    .replace("{startTimestamp}", String.format("<t:%s:R>", start_timestamp))
                    .replace("{updateTimestamp}", String.format("<t:%s:R>", System.currentTimeMillis() / 1000L))
                ).queue();
            }
            undrscre.bta_discord_integration.mod.LOGGER.info("updated channel topic!");
        } catch (Exception e) {
            undrscre.bta_discord_integration.mod.LOGGER.warn("unable to change channel topic: ", e);
        }
    }

    public static void sendToDiscord(String author, String message) {
        if (webhookClient == null) return;
        try {
            WebhookMessageBuilder builder = new WebhookMessageBuilder();
            builder.setUsername(author);
            builder.setAvatarUrl("https://www.mc-heads.net/head/" + author + "/" + "150");
            builder.setContent(message);
            webhookClient.send(builder.build());
        } catch (Throwable t) {
            undrscre.bta_discord_integration.mod.LOGGER.info("unable to send webhook: ", t);
        }
    }

    public static void sendJoinLeaveMessage(String username, boolean joined) {
        if (webhookClient == null) return;
        String avatarUrl = "https://www.mc-heads.net/head/" + username;
        String joinLeaveText = (joined ? 
            config.retrieveString("login", "join_message").replace("{username}", username) : 
            config.retrieveString("login", "leave_message").replace("{username}", username)
        );
        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(joined ? 
                    config.retrieveHex("login", "join_color") : 
                    config.retrieveHex("login", "leave_color"))
                .setAuthor(new WebhookEmbed.EmbedAuthor(joinLeaveText, avatarUrl, null))
                .build();
        sendMessage(null, embed);
    }

    public static void sendDeathMessage(String deathMessage) {
        if (webhookClient == null) return;
        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(config.retrieveHex("death", "color"))
                .setAuthor(new WebhookEmbed.EmbedAuthor(
                    deathMessage,
                    config.retrieveString("death", "icon"), 
                    null
                ))
                .build();
        sendMessage(null, embed);
    }

    public static void sendServerStartMessage() {
        if (webhookClient == null) return;
        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(config.retrieveHex("server", "start_color"))
                .setAuthor(new WebhookEmbed.EmbedAuthor(config.retrieveString("server", "start_message"), null, null))
                .setTimestamp(Instant.now())
                .build();
        sendMessage(null, embed);
    }

    public static void sendServerStoppedMessage() {
        if (webhookClient == null) return;
        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(config.retrieveHex("server", "stop_color"))
                .setAuthor(new WebhookEmbed.EmbedAuthor(config.retrieveString("server", "stop_message"), null, null))
                .setTimestamp(Instant.now())
                .build();
        sendMessage(null, embed);
    }

    public static void sendServerSleepMessage() {
        if (webhookClient == null) return;
        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setColor(config.retrieveHex("sleep", "color"))
                .setAuthor(new WebhookEmbed.EmbedAuthor(
                    config.retrieveString("sleep", "message"), 
                    config.retrieveString("sleep", "icon"), 
                    null
                ))
                .build();
        sendMessage(null, embed);
    }
    public static void sendMessage(String content, WebhookEmbed embed) {
        if (webhookClient == null) return;
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(config.discord_servername);
        builder.setAvatarUrl(config.discord_serverpfp_url);
        if (content != null && !content.isEmpty()) {
            builder.setContent(content);
        }
        if (embed != null) {
            builder.addEmbeds(embed);
        }
        webhookClient.send(builder.build());
    }
}
