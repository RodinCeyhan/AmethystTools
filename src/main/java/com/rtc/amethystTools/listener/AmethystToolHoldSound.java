package com.rtc.amethystTools.listener;

import com.rtc.amethystTools.AmethystTools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings({"RedundantIfStatement", "deprecation"})
public class AmethystToolHoldSound implements Listener {

    private final AmethystTools plugin;

    public AmethystToolHoldSound(AmethystTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHotbarSwitch(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (isAmethystTool(item)) {
            playSound(player);
        }
    }

    @EventHandler
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = event.getPlayer();
            ItemStack mainHand = player.getInventory().getItemInMainHand();

            if (isAmethystTool(mainHand)) {
                playSound(player);
            }
        });
    }

    private void playSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 2.0f);
    }

    private boolean isAmethystTool(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();

        if (meta.getPersistentDataContainer().has(AmethystTools.KEY_TOOL, PersistentDataType.BYTE)) {
            return true;
        }

        if (meta.hasCustomModelData() && meta.getCustomModelData() == 2235897) {
            return true;
        }

        return false;
    }
}