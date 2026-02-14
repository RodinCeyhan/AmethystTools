package com.rtc.amethystTools.listener;

import com.rtc.amethystTools.AmethystTools;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class GrindstoneListener implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onGrindstone(PrepareGrindstoneEvent e) {
        ItemStack result = e.getResult();
        if (result == null) return;

        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        if (!meta.getPersistentDataContainer()
                .has(AmethystTools.KEY_TOOL, PersistentDataType.BYTE)) return;

        ItemStack left = e.getInventory().getItem(0);
        if (left == null || !left.hasItemMeta()) return;

        ItemMeta original = left.getItemMeta();
        meta.setDisplayName(original.getDisplayName());
        meta.setLore(original.getLore());
        meta.setCustomModelData(original.getCustomModelData());

        result.setItemMeta(meta);
        e.setResult(result);
    }
}
