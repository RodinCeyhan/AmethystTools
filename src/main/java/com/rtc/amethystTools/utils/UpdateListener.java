package com.rtc.amethystTools.utils;

import com.rtc.amethystTools.AmethystTools;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@SuppressWarnings({"deprecation"})
public class UpdateListener implements Listener {

    private final AmethystTools plugin;

    public UpdateListener(AmethystTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.isOp()) {
            new UpdateChecker(plugin).getLatestVersion(latestVersion -> {
                String currentVersion = plugin.getDescription().getVersion();
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    player.sendMessage("§e[§dAmethystTools§e] §aA new update is available! §7(v" + latestVersion + ")");

                    TextComponent message = new TextComponent("https://modrinth.com/plugin/amethystools/versions");

                    message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/plugin/amethystools/versions"));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§fGo to Modrinth Page")));

                    player.sendMessage("§e[§dAmethystTools§e] §aDownload here:" + message);
                }
            });
        }
    }
}