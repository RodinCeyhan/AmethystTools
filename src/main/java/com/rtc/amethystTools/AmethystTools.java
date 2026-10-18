package com.rtc.amethystTools;

import com.rtc.amethystTools.command.AmethystToolCommand;
import com.rtc.amethystTools.event.AmethystToolBlockBreak;
import com.rtc.amethystTools.listener.AmethystToolHoldSound;
import com.rtc.amethystTools.listener.AnvilListener;
import com.rtc.amethystTools.listener.EnchantListener;
import com.rtc.amethystTools.listener.GrindstoneListener;
import com.rtc.amethystTools.menu.ToolMenus;
import com.rtc.amethystTools.utils.ConfigMigrator;
import com.rtc.amethystTools.utils.UpdateChecker;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "deprecation", "UnstableApiUsage", "SpellCheckingInspection"})
public final class AmethystTools extends JavaPlugin implements Listener {

    public UpdateChecker updateChecker;

    private static AmethystTools instance;

    public static AmethystTools getInstance() {
        return instance;
    }

    private void migrateConfig() {
        File configFile = new File(getDataFolder(), "config.yml");

        try (InputStream resource = getResource("config.yml")) {
            if (resource == null) return;

            List<String> defaults = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).lines().toList();
            List<String> lines = new ArrayList<>(Files.readAllLines(configFile.toPath(), StandardCharsets.UTF_8));

            if (ConfigMigrator.migrate(lines, defaults)) {
                Files.write(configFile.toPath(), lines, StandardCharsets.UTF_8);
                reloadConfig();
            }
        } catch (IOException exception) {
            getLogger().warning("Could not update config.yml: " + exception.getMessage());
        }
    }

    public static NamespacedKey KEY_TOOL;
    public static NamespacedKey KEY_TIER;
    public static NamespacedKey KEY_TYPE;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        migrateConfig();

        KEY_TOOL = new NamespacedKey(this, "amethyst_tool");
        KEY_TIER = new NamespacedKey(this, "amethyst_tier");
        KEY_TYPE = new NamespacedKey(this, "amethyst_type");

        getServer().getPluginManager().registerEvents(this, this);

        Bukkit.getConsoleSender().sendMessage("§d                          _   _               _ _______          _     ");
        Bukkit.getConsoleSender().sendMessage("§d     /\\                  | | | |             | |__   __|        | |    ");
        Bukkit.getConsoleSender().sendMessage("§d    /  \\   _ __ ___   ___| |_| |__  _   _ ___| |_ | | ___   ___ | |___ ");
        Bukkit.getConsoleSender().sendMessage("§d   / /\\ \\ | '_ ` _ \\ / _ \\ __| '_ \\| | | / __| __|| |/ _ \\ / _ \\| / __|");
        Bukkit.getConsoleSender().sendMessage("§d  / ____ \\| | | | | |  __/ |_| | | | |_| \\__ \\ |_ | | (_) | (_) | \\__ \\");
        Bukkit.getConsoleSender().sendMessage("§d /_/    \\_\\_| |_| |_|\\___|\\__|_| |_|\\__, |___/\\__||_|\\___/ \\___/|_|___/");
        Bukkit.getConsoleSender().sendMessage("§d                                     __/ |                             ");
        Bukkit.getConsoleSender().sendMessage("§d                                    |___/                              ");

        updateChecker = new UpdateChecker(this);
        updateChecker.check();

        getServer().getPluginManager().registerEvents(new AmethystToolHoldSound(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolBlockBreak(this), this);

        getServer().getPluginManager().registerEvents(toolMenus, this);

        getServer().getPluginManager().registerEvents(new EnchantListener(this), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            var commands = event.registrar();

            var commandNode = new AmethystToolCommand(this).register().build();
            commands.register(commandNode, "AmethystTools Command", List.of());
        });

        getServer().getPluginManager().registerEvents(new AnvilListener(), this);
        getServer().getPluginManager().registerEvents(new GrindstoneListener(), this);
    }

    private final ToolMenus toolMenus = new ToolMenus(this);

    public ToolMenus getToolMenus() {
        return toolMenus;
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        if (!event.getPlayer().isOp() || !event.getPlayer().hasPermission("amethysttools.admin") || !updateChecker.updateAvailable)
            return;
        String prefix = "&7[&dAmethystTools&7]";

        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + "&aThere is a newer plugin version available: &n" + updateChecker.latestVersion + "&r&a, you're on: &n" + updateChecker.currentVersion));

        Component updateLink = Component.text(ChatColor.translateAlternateColorCodes('&', prefix + " &aClick here to download the new version."))
                .clickEvent(ClickEvent.openUrl("https://modrinth.com/plugin/amethystools/versions"))
                .hoverEvent(HoverEvent.showText(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Click to go to the Modrinth download page."))));

        event.getPlayer().sendMessage(updateLink);
    }
}