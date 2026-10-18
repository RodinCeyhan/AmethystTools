package com.rtc.amethystTools.listener;

import com.rtc.amethystTools.utils.ToolUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class AnvilListener implements Listener {

    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        ItemStack left = e.getInventory().getItem(0);
        ItemStack result = e.getResult();
        if (result == null || !ToolUtils.isAmethystTool(left)) return;

        var name = left.getItemMeta().displayName();
        result.editMeta(meta -> meta.displayName(name));
        e.setResult(result);
    }
}