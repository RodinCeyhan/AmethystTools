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

@SuppressWarnings({"unused", "deprecation"})
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
        getServer().getPluginManager().registerEvents(new AmethystToolStoneMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolWoodenMenu(this), this);

        getServer().getPluginManager().registerEvents(new AmethystToolHoldSound(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolBlockBreak(this), this);

        this.getServer().getCommandMap().register("amethysttools", new AmethystToolCommand(this));
        this.getServer().getCommandMap().register("amethysttoolsgive", new AmethystToolsGiveCommand(this));
        this.getServer().getCommandMap().register("amethysttoolsreload", new AmethystToolReloadCommand(this));

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
