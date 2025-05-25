package undrscre.bta_discord_integration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class config {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static String discord_token = "SUPER SECRET TOKEN";
    public static String discord_channel = "CHANNEL ID";
    public static String discord_serverpfp_url = "https://i.imgur.com/dJUId0O.png";
    public static String discord_servername = "Server";
    public static Boolean discord_changeChannelTopic = false;
    public static String discord_channelTopicMessage = "{serverStatus} {playerCount}/{maxPlayers} player(s) online. | Server started {startTimestamp} | Last updated {updateTimestamp}";
    public static Map<String, Map<String, String>> discord_embeds = new HashMap<>();
    
    static {
        Map<String, String> loginEmbed = new HashMap<>();
        Map<String, String> deathEmbed = new HashMap<>();
        Map<String, String> serverStateEmbed = new HashMap<>();
        Map<String, String> sleepEmbed = new HashMap<>();

        loginEmbed.put("join_message", "{username} joined the server!");
        loginEmbed.put("leave_message", "{username} left the server!");
        loginEmbed.put("join_color", "#00FF00");
        loginEmbed.put("leave_color", "#FF0000");

        deathEmbed.put("color","0xFF0000");
        deathEmbed.put("icon", "https://i.imgur.com/l1EJ6fx.png");

        serverStateEmbed.put("start_message", "✅ Server started!");
        serverStateEmbed.put("start_color", "#4ae485");
        serverStateEmbed.put("stop_message", "❌ Server stopped!");
        serverStateEmbed.put("stop_color", "#f92f60");
        serverStateEmbed.put("icon", "");

        sleepEmbed.put("color","#222d5a");
        sleepEmbed.put("message","The Night was Skipped");
        sleepEmbed.put("icon","https://i.imgur.com/R1e1sJS.png");

        discord_embeds.put("login", loginEmbed);
        discord_embeds.put("server", serverStateEmbed);
        discord_embeds.put("death", deathEmbed);
        discord_embeds.put("sleep", sleepEmbed);
    }

    public static void load(){
        File file = getFilePath();

        if (!file.exists()) {
            initFile(file);
        }

        try {
            FileReader reader = new FileReader(file);
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            reader.close();

            updateValues(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        save();
    }

    public static void save() {
        File file = getFilePath();
        JsonObject obj = new JsonObject();
        updateValues(obj);

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(GSON.toJson(obj));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initFile(File file) {
        try {
            @SuppressWarnings("unused")
            boolean ign = file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("{}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T get(JsonObject object, String key, T defaultValue) {
        JsonElement element = object.get(key);
        if (element == null) {
            object.add(key, GSON.toJsonTree(defaultValue));
            return defaultValue;
        }
        return GSON.fromJson(element, (Class<T>)defaultValue.getClass());
    }

    public static void updateValues(JsonObject object) {
        discord_token = get(object, "token", discord_token);
        discord_channel = get(object, "channel", discord_channel);
        discord_serverpfp_url = get(object, "serverpfp_url", discord_serverpfp_url);
        discord_servername = get(object, "servername", discord_servername);
        discord_changeChannelTopic = get(object,"change_topic", discord_changeChannelTopic);
        discord_channelTopicMessage = get(object,"topic_message", discord_channelTopicMessage);
        discord_embeds = get(object, "embeds", discord_embeds);
    }

    public static int retrieveHex(String embed, String key) {
        String colorStr = discord_embeds.get(embed).get(key);
        if (colorStr.startsWith("#")) {
            return Integer.parseInt(colorStr.substring(1), 16);
        } else if (colorStr.startsWith("0x")) {
            return Integer.parseInt(colorStr.substring(2), 16);
        }
        return Integer.parseInt(colorStr, 16);
    }

    public static String retrieveString(String embed, String key) {
        return discord_embeds.get(embed).get(key);
    }

    public static File getFilePath() {
        return FabricLoader.getInstance().getConfigDir().resolve("bta_discord.json").toFile();
    }

    static {
        load();
    }
}
