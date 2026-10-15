package com.rtc.amethystTools.menu;

import com.rtc.amethystTools.AmethystTools;
import com.rtc.amethystTools.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@SuppressWarnings({"unused", "ClassCanBeRecord", "FieldCanBeLocal", "SpellCheckingInspection"})
public class AmethystToolStoneMenu implements Listener {

    private final AmethystTools plugin;

    public AmethystToolStoneMenu(AmethystTools plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings({"NullableProblems", "DataFlowIssue"})
    public static class StoneMenuHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public Inventory create(Player player) {
        int size = 27;
        String MENU_TITLE = plugin.getConfig().getString("menus.menu-stone", "<#7D7D7D>Stone Tools");
        Inventory inv = Bukkit.createInventory(new StoneMenuHolder(), size, MessageUtils.parse(MENU_TITLE));

        inv.setItem(12, buildPickaxe(player));
        inv.setItem(13, buildShovel(player));
        inv.setItem(14, buildAxe(player));
        inv.setItem(18, buildBackPage(player));

        return inv;
    }

    private ItemStack buildPickaxe(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.STONE_PICKAXE));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String materialname = plugin.getConfig().getString("item.item-name", "&5ᴀᴍᴇᴛʜʏѕᴛ");
        String materialpickaxe = plugin.getConfig().getString("item.item-pickaxe", "&5ᴘɪᴄᴋᴀхᴇ");

        Component name = MessageUtils.parse(materialname + " " + materialpickaxe);
        meta.displayName(name);
        meta.lore(List.of(
                MessageUtils.parse("<gray>Breaks 9 Blocks at Once"),
                Component.empty(),
                MessageUtils.parse("<#FFFF00>Click to get")));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildShovel(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.STONE_SHOVEL));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String materialname = plugin.getConfig().getString("item.item-name", "&5ᴀᴍᴇᴛʜʏѕᴛ");
        String materialshovel = plugin.getConfig().getString("item.item-shovel", "&5ѕʜᴏᴠᴇʟ");

        Component name = MessageUtils.parse(materialname + " " + materialshovel);
        meta.displayName(name);
        meta.lore(List.of(
                MessageUtils.parse("<gray>Breaks 9 Blocks at Once"),
                Component.empty(),
                MessageUtils.parse("<#FFFF00>Click to get")));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildAxe(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.STONE_AXE));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String materialname = plugin.getConfig().getString("item.item-name", "&5ᴀᴍᴇᴛʜʏѕᴛ");
        String materialaxe = plugin.getConfig().getString("item.item-axe", "&5ᴀхᴇ");

        Component name = MessageUtils.parse(materialname + " " + materialaxe);
        meta.displayName(name);
        meta.lore(List.of(
                MessageUtils.parse("<gray>Breaks 9 Blocks at Once"),
                Component.empty(),
                MessageUtils.parse("<#FFFF00>Click to get")));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildBackPage(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.ARROW));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String previouspage = plugin.getConfig().getString("menus.item-previous", "&e<- ᴘʀᴇᴠɪᴏᴜѕ ᴘᴀɢᴇ");

        Component name = MessageUtils.parse(previouspage);
        meta.displayName(name);

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof StoneMenuHolder)) return;
        if (event.getClickedInventory() == null) return;

        event.setCancelled(true);

        switch (event.getSlot()) {
            case 12 ->  {
                player.performCommand("amethysttools stone pickaxe");
                player.closeInventory();
            }
            case 13 -> {
                player.performCommand("amethysttools stone shovel");
                player.closeInventory();
            }
            case 14 -> {
                player.performCommand("amethysttools stone axe");
                player.closeInventory();
            }
            case 18 -> player.openInventory(new AmethystToolMainMenu(plugin).create(player));
        }
    }
}