package com.rtc.amethystTools.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

@SuppressWarnings({"deprecation", "SizeReplaceableByIsEmpty"})
public class UpdateChecker {

    private final JavaPlugin plugin;
    private final String projectSlug = "amethystools";

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void getLatestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                URL url = new URL("https://api.modrinth.com/v2/project/" + projectSlug + "/version");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Amethystools-UpdateChecker");

                if (connection.getResponseCode() == 200) {
                    try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                        JsonArray versions = JsonParser.parseReader(reader).getAsJsonArray();
                        if (versions.size() > 0) {
                            String latestVersion = versions.get(0).getAsJsonObject().get("version_number").getAsString();
                            Bukkit.getScheduler().runTask(this.plugin, () -> consumer.accept(latestVersion));
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        });
    }
}