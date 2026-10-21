package com.rtc.amethystTools.listener;

import com.rtc.amethystTools.AmethystTools;
import com.rtc.amethystTools.utils.ToolUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MendingListener implements Listener {

    private static final long GUARD_PERIOD_TICKS = 5L;

    private final AmethystTools plugin;

    public MendingListener(AmethystTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMend(PlayerItemMendEvent event) {
        if (ToolUtils.isRepairAllowed()) return;
        if (!ToolUtils.isAmethystTool(event.getItem())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        startGuard(event.getPlayer());
    }

    public void startForOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            startGuard(player);
        }
    }

    private void startGuard(Player player) {
        player.getScheduler().runAtFixedRate(plugin, task -> enforceDurability(player), null,
                GUARD_PERIOD_TICKS, GUARD_PERIOD_TICKS);
    }

    private void enforceDurability(Player player) {
        PlayerInventory inventory = player.getInventory();
        boolean repairAllowed = ToolUtils.isRepairAllowed();
        NamespacedKey floorKey = AmethystTools.KEY_DAMAGE_FLOOR;

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (!ToolUtils.isAmethystTool(item)) continue;
            if (!(item.getItemMeta() instanceof Damageable damageable)) continue;

            PersistentDataContainer pdc = damageable.getPersistentDataContainer();
            Integer floor = pdc.get(floorKey, PersistentDataType.INTEGER);
            int damage = damageable.getDamage();

            if (floor == null || damage > floor || (damage < floor && repairAllowed)) {
                pdc.set(floorKey, PersistentDataType.INTEGER, damage);
                item.setItemMeta(damageable);
                inventory.setItem(slot, item);
            } else if (damage < floor) {
                damageable.setDamage(floor);
                item.setItemMeta(damageable);
                inventory.setItem(slot, item);
            }
        }
    }
}