package com.rtc.amethystTools.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {

    private static final String API_URL = "https://api.modrinth.com/v2/project/YTkZHbOm/version";
    private static final String DOWNLOAD_URL = "https://modrinth.com/plugin/amethystools/versions";

    private static final Pattern VERSION = Pattern.compile("^\\D*(\\d+(?:\\.\\d+)*)");

    private final JavaPlugin plugin;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    public final String currentVersion;
    public volatile String latestVersion;
    public volatile boolean updateAvailable;

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getPluginMeta().getVersion();
    }

    public void check() {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> runCheck());
    }

    private void runCheck() {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(5))
                    .header("User-Agent", "RodinCeyhan/AmethystTools/" + currentVersion)
                    .GET()
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                plugin.getLogger().warning("Update check failed (HTTP " + response.statusCode() + ").");
                return;
            }

            String newest = null;
            JsonArray versions = JsonParser.parseString(response.body()).getAsJsonArray();
            for (JsonElement element : versions) {
                JsonObject version = element.getAsJsonObject();
                if (!"release".equals(version.get("version_type").getAsString())) continue;

                String number = version.get("version_number").getAsString();
                if (newest == null || compare(number, newest) > 0) newest = number;
            }
            if (newest == null) return;

            latestVersion = newest;
            updateAvailable = compare(newest, currentVersion) > 0;

            if (updateAvailable) {
                plugin.getLogger().warning("There is a newer plugin version available: " + newest + ", you're on: " + currentVersion);
                plugin.getLogger().warning("Go to its page to download: " + DOWNLOAD_URL);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            plugin.getLogger().warning("Update check failed: " + e.getMessage());
        }
    }

    private static int compare(String a, String b) {
        int[] x = parse(a);
        int[] y = parse(b);

        for (int i = 0; i < Math.max(x.length, y.length); i++) {
            int p = i < x.length ? x[i] : 0;
            int q = i < y.length ? y[i] : 0;
            if (p != q) return Integer.compare(p, q);
        }
        return 0;
    }

    private static int[] parse(String version) {
        Matcher matcher = VERSION.matcher(version);
        if (!matcher.find()) return new int[0];

        String[] parts = matcher.group(1).split("\\.");
        int[] numbers = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            numbers[i] = Integer.parseInt(parts[i]);
        }
        return numbers;
    }
}