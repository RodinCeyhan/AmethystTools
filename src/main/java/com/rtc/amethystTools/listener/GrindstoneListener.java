package com.rtc.amethystTools.listener;

import com.rtc.amethystTools.utils.ToolUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GrindstoneListener implements Listener {

    @EventHandler
    public void onGrindstone(PrepareGrindstoneEvent e) {
        ItemStack result = e.getResult();
        if (!ToolUtils.isAmethystTool(result)) return;

        ItemStack upper = e.getInventory().getItem(0);
        ItemStack lower = e.getInventory().getItem(1);
        ItemStack source = ToolUtils.isAmethystTool(upper) ? upper : ToolUtils.isAmethystTool(lower) ? lower : null;
        if (source == null) return;

        if (!ToolUtils.isRepairAllowed() && ToolUtils.isRepair(source, result)) {
            e.setResult(null);
            return;
        }

        ItemMeta original = source.getItemMeta();
        result.editMeta(meta -> {
            meta.displayName(original.displayName());
            meta.lore(original.lore());
            if (original.hasCustomModelData()) {
                meta.setCustomModelData(original.getCustomModelData());
            }
        });
        e.setResult(result);
    }
}