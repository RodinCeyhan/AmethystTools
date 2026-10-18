package com.rtc.amethystTools.listener;

import com.rtc.amethystTools.AmethystTools;
import com.rtc.amethystTools.utils.ToolUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class AmethystToolHoldSound implements Listener {

    private final AmethystTools plugin;

    public AmethystToolHoldSound(AmethystTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHotbarSwitch(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (ToolUtils.isAmethystTool(item)) {
            playSound(player);
        }
    }

    @EventHandler
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        player.getScheduler().run(plugin, task -> {
            if (ToolUtils.isAmethystTool(player.getInventory().getItemInMainHand())) {
                playSound(player);
            }
        }, null);
    }

    private void playSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 2.0f);
    }
}