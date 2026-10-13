package com.rtc.amethystTools;

import com.rtc.amethystTools.command.AmethystToolCommand;
import com.rtc.amethystTools.command.AmethystToolReloadCommand;
import com.rtc.amethystTools.command.AmethystToolsGiveCommand;
import com.rtc.amethystTools.event.AmethystToolBlockBreak;
import com.rtc.amethystTools.listener.AmethystToolHoldSound;
import com.rtc.amethystTools.listener.AnvilListener;
import com.rtc.amethystTools.listener.GrindstoneListener;
import com.rtc.amethystTools.menu.*;
import com.rtc.amethystTools.utils.UpdateChecker;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused", "deprecation", "UnstableApiUsage", "SpellCheckingInspection"})
public final class AmethystTools extends JavaPlugin implements Listener {

    public UpdateChecker updateChecker;

    private static AmethystTools instance;

    public static AmethystTools getInstance() {
        return instance;
    }

    public static NamespacedKey KEY_TOOL;
    public static NamespacedKey KEY_TIER;
    public static NamespacedKey KEY_TYPE;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();


        FileConfiguration config = getConfig();

        File configFile = new File(getDataFolder(), "config.yml");

        try {
            List<String> lines = Files.exists(configFile.toPath())
                    ? Files.readAllLines(configFile.toPath(), StandardCharsets.UTF_8)
                    : new ArrayList<>();

            boolean changed = false;

            if (!config.isSet("block-breaking-mode")) {
                int insertIndex = -1;

                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).contains("Supported Mini Message, RGB and legacy color codes")) {
                        insertIndex = i + 1;
                        break;
                    }
                }

                if (insertIndex == -1) {
                    insertIndex = lines.size();
                }

                List<String> addition = Arrays.asList(
                        "",
                        "# AmethystTools block-breaking mode",
                        "# OLD: Original 3x3 behavior from the GitHub version",
                        "# NEW: 3x3 pickaxe/shovel, tree-capitator axe, effects and plant exclusions",
                        "block-breaking-mode: NEW"
                );

                lines.addAll(insertIndex, addition);
                changed = true;
            }

            if (!config.isSet("blacklist-blocks")) {
                List<String> blacklist = Arrays.asList(
                        "",
                        "blacklist-blocks:",
                        "  - DIAMOND_ORE",
                        "  - DEEPSLATE_DIAMOND_ORE",
                        "  - EMERALD_ORE",
                        "  - DEEPSLATE_EMERALD_ORE",
                        "  - IRON_ORE",
                        "  - DEEPSLATE_IRON_ORE",
                        "  - GOLD_ORE",
                        "  - DEEPSLATE_GOLD_ORE",
                        "  - NETHER_GOLD_ORE",
                        "  - COPPER_ORE",
                        "  - DEEPSLATE_COPPER_ORE",
                        "  - COAL_ORE",
                        "  - DEEPSLATE_COAL_ORE",
                        "  - LAPIS_ORE",
                        "  - DEEPSLATE_LAPIS_ORE",
                        "  - REDSTONE_ORE",
                        "  - DEEPSLATE_REDSTONE_ORE",
                        "  - NETHER_QUARTZ_ORE",
                        "  - ANCIENT_DEBRIS",
                        "  - AMETHYST_CLUSTER",
                        "  - LARGE_AMETHYST_BUD",
                        "  - MEDIUM_AMETHYST_BUD",
                        "  - SMALL_AMETHYST_BUD",
                        "  - GILDED_BLACKSTONE",
                        "  - RAW_IRON_BLOCK",
                        "  - RAW_GOLD_BLOCK",
                        "  - RAW_COPPER_BLOCK",
                        "",
                        "  - CHEST",
                        "  - TRAPPED_CHEST",
                        "  - SHULKER_BOX",
                        "  - BLACK_SHULKER_BOX",
                        "  - BLUE_SHULKER_BOX",
                        "  - BROWN_SHULKER_BOX",
                        "  - CYAN_SHULKER_BOX",
                        "  - GRAY_SHULKER_BOX",
                        "  - GREEN_SHULKER_BOX",
                        "  - LIGHT_BLUE_SHULKER_BOX",
                        "  - LIGHT_GRAY_SHULKER_BOX",
                        "  - LIME_SHULKER_BOX",
                        "  - MAGENTA_SHULKER_BOX",
                        "  - ORANGE_SHULKER_BOX",
                        "  - PINK_SHULKER_BOX",
                        "  - PURPLE_SHULKER_BOX",
                        "  - RED_SHULKER_BOX",
                        "  - WHITE_SHULKER_BOX",
                        "  - YELLOW_SHULKER_BOX",
                        "  - BEDROCK",
                        "  - OBSIDIAN",
                        "  - CRYING_OBSIDIAN",
                        "  - COMMAND_BLOCK",
                        "  - CHAIN_COMMAND_BLOCK",
                        "  - REPEATING_COMMAND_BLOCK",
                        "  - COPPER_CHEST",
                        "  - EXPOSED_COPPER_CHEST",
                        "  - WEATHERED_COPPER_CHEST",
                        "  - OXIDIZED_COPPER_CHEST",
                        "  - VAULT",
                        "  - WAXED_COPPER_CHEST",
                        "  - WAXED_EXPOSED_COPPER_CHEST",
                        "  - WAXED_WEATHERED_COPPER_CHEST",
                        "  - WAXED_OXIDIZED_COPPER_CHEST"
                );

                lines.addAll(blacklist);
                changed = true;
            }

            if (changed) {
                Files.write(configFile.toPath(), lines, StandardCharsets.UTF_8);
                reloadConfig();
            }
        } catch (IOException exception) {
            getLogger().warning("Could not update config.yml: " + exception.getMessage());
        }


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

        getServer().getPluginManager().registerEvents(new AmethystToolMainMenu(this), this);

        getServer().getPluginManager().registerEvents(new AmethystToolNetheriteMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolDiamondMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolGoldenMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolIronMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolCopperMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolStoneMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolWoodenMenu(this), this);

        getServer().getPluginManager().registerEvents(new AmethystToolHoldSound(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolBlockBreak(this), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            var commands = event.registrar();

            var mainCommandNode = new AmethystToolCommand(this).register().build();
            commands.register(mainCommandNode, "AmethystTools Command", List.of());

            var reloadCommandNode = new AmethystToolReloadCommand(this).register().build();
            commands.register(reloadCommandNode, "AmethystTools Reload Command", List.of());

            var giveCommandNode = new AmethystToolsGiveCommand(this).register().build();
            commands.register(giveCommandNode, "AmethystTools Give Command", List.of());
        });


        getServer().getPluginManager().registerEvents(new AnvilListener(), this);
        getServer().getPluginManager().registerEvents(new GrindstoneListener(), this);
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        if (updateChecker.latestVersion != null && updateChecker.isUpdateAvailable(updateChecker.currentVersion, updateChecker.latestVersion)) {
            if (event.getPlayer().isOp()) {
                String prefix = "&7[&dAmethystTools&7]";

                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        prefix + " " + "&aThere is a newer plugin version available: &n" + updateChecker.latestVersion + "&r&a, you're on: &n" + updateChecker.currentVersion
                ));

                Component updateLink = Component.text(ChatColor.translateAlternateColorCodes('&', prefix + " &aClick here to download the new version."))
                        .clickEvent(ClickEvent.openUrl("https://modrinth.com/plugin/amethystools/versions"))
                        .hoverEvent(HoverEvent.showText(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Click to go to the Modrinth download page."))));

                event.getPlayer().sendMessage(updateLink);
            }
        }
    }
}
