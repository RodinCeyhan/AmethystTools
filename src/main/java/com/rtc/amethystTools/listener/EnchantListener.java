package com.rtc.amethystTools.listener;

import com.rtc.amethystTools.AmethystTools;
import com.rtc.amethystTools.utils.ToolUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EnchantListener implements Listener {

    private final AmethystTools plugin;

    public EnchantListener(AmethystTools plugin) {
        this.plugin = plugin;
    }

    private boolean enchantingDisabled() {
        return !plugin.getConfig().getBoolean("enable-item-enchant", true);
    }

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        if (enchantingDisabled() && ToolUtils.isAmethystTool(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        if (enchantingDisabled() && ToolUtils.isAmethystTool(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAnvil(PrepareAnvilEvent event) {
        if (!enchantingDisabled()) return;

        ItemStack left = event.getInventory().getItem(0);
        ItemStack result = event.getResult();
        if (result == null || !ToolUtils.isAmethystTool(left)) return;

        for (Map.Entry<Enchantment, Integer> entry : result.getEnchantments().entrySet()) {
            if (entry.getValue() > left.getEnchantmentLevel(entry.getKey())) {
                event.setResult(null);
                return;
            }
        }
    }
}