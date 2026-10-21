package com.rtc.amethystTools.listener;

import com.rtc.amethystTools.utils.ToolUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class CraftingListener implements Listener {

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (!event.isRepair()) return;

        CraftingInventory inventory = event.getInventory();
        ItemStack result = inventory.getResult();
        if (result == null) return;

        ItemStack source = null;
        for (ItemStack ingredient : inventory.getMatrix()) {
            if (ToolUtils.isAmethystTool(ingredient)) {
                source = ingredient;
                break;
            }
        }
        if (source == null) return;

        if (!ToolUtils.isRepairAllowed()) {
            inventory.setResult(null);
            return;
        }

        ToolUtils.copyToolData(source, result);
        inventory.setResult(result);
    }
}