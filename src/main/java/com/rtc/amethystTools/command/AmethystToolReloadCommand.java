package com.rtc.amethystTools.command;

import com.rtc.amethystTools.AmethystTools;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"deprecation", "unused", "FieldCanBeLocal"})
public class AmethystToolReloadCommand extends BukkitCommand {

    private final AmethystTools plugin;

    public AmethystToolReloadCommand(AmethystTools plugin) {
        super("amethysttoolsreload");
        this.plugin = plugin;
        setDescription("Reload plugin config");
        setUsage("/amethysttoolsreload");
        setPermission("amethysttools.admin");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {
        Player player = (Player) commandSender;
        if (commandSender.hasPermission("amethysttools.admin")) {
            plugin.reloadConfig();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.reloadplugin", "&aPlugin reload is succesfuly complete.")));
            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.reloadplugin-actionbar", "&aReloaded Plugin")));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.nopermission", "&cYou don't have permission!")));
            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.nopermission-actionbar", "&cYou don't have permission!")));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {
        return Collections.emptyList();
    }
}