package com.rtc.amethystTools.listener;

import com.rtc.amethystTools.AmethystTools;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings("deprecation")
public class AnvilListener implements Listener {

    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        ItemStack left = e.getInventory().getItem(0);
        ItemStack result = e.getResult();
        if (left == null || result == null) return;

        ItemMeta lMeta = left.getItemMeta();
        ItemMeta rMeta = result.getItemMeta();
        if (lMeta == null || rMeta == null) return;

        if (!lMeta.getPersistentDataContainer()
                .has(AmethystTools.KEY_TOOL, PersistentDataType.BYTE)) return;

        rMeta.setDisplayName(lMeta.getDisplayName());
        result.setItemMeta(rMeta);
        e.setResult(result);
    }
}
