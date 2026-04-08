package com.rtc.amethystTools.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings({"deprecation"})
public class UpdateChecker implements Listener {

    private final JavaPlugin plugin;
    public final String currentVersion;
    public String latestVersion;

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public void check() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.modrinth.com/v2/project/YTkZHbOm/version");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JsonArray array = JsonParser.parseString(response.toString()).getAsJsonArray();

                    if (array.isEmpty()) return;

                    JsonElement first = array.get(0);
                    latestVersion = first.getAsJsonObject().get("version_number").getAsString();

                    if (isUpdateAvailable(currentVersion, latestVersion)) {
                        plugin.getLogger().warning("There is a newer plugin version available: " + latestVersion + ", you're on: " + currentVersion);
                        plugin.getLogger().warning("Go to its page to download: §ahttps://modrinth.com/plugin/amethystools/versions");
                    }

                } catch (Exception e) {
                    plugin.getLogger().warning("An error occurred during the UpdateChecker check!");
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public boolean isUpdateAvailable(String current, String latest) {
        String[] currentParts = current.split("\\.");
        String[] latestParts = latest.split("\\.");

        int length = Math.max(currentParts.length, latestParts.length);

        for (int i = 0; i < length; i++) {
            int cur = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            int lat = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;

            if (lat > cur) return true;
            if (lat < cur) return false;
        }
        return false;
    }
}