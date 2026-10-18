package com.rtc.amethystTools.menu;

import com.rtc.amethystTools.AmethystTools;
import com.rtc.amethystTools.utils.MessageUtils;
import com.rtc.amethystTools.utils.ToolUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ToolMenus implements Listener {

    private static final int SIZE = 27;
    private static final int MAIN_START_SLOT = 10;
    private static final int BACK_SLOT = 18;

    private final AmethystTools plugin;

    public ToolMenus(AmethystTools plugin) {
        this.plugin = plugin;
    }

    private static final class Holder implements InventoryHolder {
        private final Tier tier;
        private Inventory inventory;

        private Holder(Tier tier) {
            this.tier = tier;
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }

    private String cfg(String key, String def) {
        return plugin.getConfig().getString(key, def);
    }

    private Inventory createInventory(Tier tier, String title) {
        Holder holder = new Holder(tier);
        Inventory inv = Bukkit.createInventory(holder, SIZE, MessageUtils.parse(title));
        holder.inventory = inv;
        return inv;
    }

    public Inventory mainMenu() {
        Inventory inv = createInventory(null, cfg("menus.menu-main", "<dark_purple>Amethyst Tools"));

        List<Tier> tiers = Tier.listAvailable();
        for (int i = 0; i < tiers.size(); i++) {
            Tier tier = tiers.get(i);
            inv.setItem(MAIN_START_SLOT + i, simpleItem(tier.icon, cfg(tier.configKey(), tier.defaultTitle)));
        }
        return inv;
    }

    public Inventory tier(Tier tier) {
        Inventory inv = createInventory(tier, cfg(tier.configKey(), tier.defaultTitle));

        for (ToolType type : ToolType.values()) {
            inv.setItem(type.slot, toolItem(tier, type));
        }
        inv.setItem(BACK_SLOT, simpleItem(Material.ARROW, cfg("menus.item-previous", "<yellow><- ᴘʀᴇᴠɪᴏᴜѕ ᴘᴀɢᴇ")));
        return inv;
    }

    private ItemStack simpleItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> meta.displayName(MessageUtils.parse(name)));
        return item;
    }

    private ItemStack toolItem(Tier tier, ToolType type) {
        ItemStack item = new ItemStack(ToolUtils.getMaterial(tier.id, type.id));

        String name = cfg("item.item-name", "<dark_purple>ᴀᴍᴇᴛʜʏѕᴛ") + " " + cfg(type.nameKey(), "<dark_purple>" + type.id);

        item.editMeta(meta -> {
            meta.displayName(MessageUtils.parse(name));
            List<Component> lore = new ArrayList<>();
            if (type != ToolType.AXE) {
                lore.add(MessageUtils.parse("<gray>Breaks 9 Blocks at Once"));
            }
            lore.add(Component.empty());
            lore.add(MessageUtils.parse("<#FFFF00>Click to get"));
            meta.lore(lore);
            meta.setEnchantmentGlintOverride(true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        });
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof Holder holder)) return;

        event.setCancelled(true);

        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        int slot = event.getSlot();

        if (holder.tier == null) {
            List<Tier> tiers = Tier.listAvailable();
            int index = slot - MAIN_START_SLOT;
            if (index >= 0 && index < tiers.size()) {
                player.openInventory(tier(tiers.get(index)));
            }
            return;
        }

        if (slot == BACK_SLOT) {
            player.openInventory(mainMenu());
            return;
        }

        for (ToolType type : ToolType.values()) {
            if (type.slot == slot) {
                player.performCommand("amethysttools give " + holder.tier.id + " " + type.id);
                player.closeInventory();
                return;
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof Holder) {
            event.setCancelled(true);
        }
    }
}