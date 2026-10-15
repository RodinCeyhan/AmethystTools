package com.rtc.amethystTools.menu;

import com.rtc.amethystTools.AmethystTools;
import com.rtc.amethystTools.utils.MessageUtils;
import com.rtc.amethystTools.utils.ToolUtils;
import com.rtc.amethystTools.utils.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings({"unused", "ClassCanBeRecord", "FieldCanBeLocal", "SpellCheckingInspection"})
public class AmethystToolMainMenu implements Listener {

    private final AmethystTools plugin;

    public AmethystToolMainMenu(AmethystTools plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings({"NullableProblems", "DataFlowIssue"})
    public static class MainMenuHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public Inventory create(Player player) {
        int size = 27;
        String MENU_TITLE = plugin.getConfig().getString("menus.menu-main", "<dark_purple>Amethyst Tools");
        Inventory inv = Bukkit.createInventory(new MainMenuHolder(), size, MessageUtils.parse(MENU_TITLE));

        boolean hasCopper = VersionUtil.isAtLeast("1.21.9");

        inv.setItem(10, buildWoodenCategory(player));
        inv.setItem(11, buildStoneCategory(player));
        inv.setItem(12, buildIronCategory(player));

        if (hasCopper) {
            inv.setItem(13, buildCopperCategory(player));
            inv.setItem(14, buildGoldCategory(player));
            inv.setItem(15, buildDiamondCategory(player));
            inv.setItem(16, buildNetheriteCategory(player));
        } else {
            inv.setItem(13, buildGoldCategory(player));
            inv.setItem(14, buildDiamondCategory(player));
            inv.setItem(15, buildNetheriteCategory(player));
        }

        return inv;
    }

    private ItemStack buildWoodenCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.OAK_PLANKS));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String itemname = plugin.getConfig().getString("menus.menu-wooden", "<#A0522D>Wooden Tools");
        meta.displayName(MessageUtils.parse(itemname));

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildStoneCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.STONE));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String itemname = plugin.getConfig().getString("menus.menu-stone", "<#7D7D7D>Stone Tools");
        meta.displayName(MessageUtils.parse(itemname));

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildIronCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.IRON_INGOT));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String itemname = plugin.getConfig().getString("menus.menu-iron", "<#DADADA>Iron Tools");
        meta.displayName(MessageUtils.parse(itemname));

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildCopperCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.COPPER_INGOT));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String itemname = plugin.getConfig().getString("menus.menu-copper", "<#C87533>Copper Tools");
        meta.displayName(MessageUtils.parse(itemname));

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildGoldCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.GOLD_INGOT));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String itemname = plugin.getConfig().getString("menus.menu-golden", "<#FFD700>Golden Tools");
        meta.displayName(MessageUtils.parse(itemname));

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildDiamondCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.DIAMOND));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String itemname = plugin.getConfig().getString("menus.menu-diamond", "<#4EE1D8>Diamond Tools");
        meta.displayName(MessageUtils.parse(itemname));

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildNetheriteCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.NETHERITE_INGOT));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String itemname = plugin.getConfig().getString("menus.menu-netherite", "<#2B2B2F>Netherite Tools");
        meta.displayName(MessageUtils.parse(itemname));

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof MainMenuHolder)) return;
        if (event.getClickedInventory() == null) return;

        event.setCancelled(true);

        boolean hasCopper = ToolUtils.hasCopperTools();
        switch (event.getSlot()) {

            case 10 -> open(MenuType.WOODEN, player);
            case 11 -> open(MenuType.STONE, player);
            case 12 -> open(MenuType.IRON, player);

            case 13 -> {
                if (hasCopper) open(MenuType.COPPER, player);
                else open(MenuType.GOLD, player);
            }

            case 14 -> {
                if (hasCopper) open(MenuType.GOLD, player);
                else open(MenuType.DIAMOND, player);
            }

            case 15 -> {
                if (hasCopper) open(MenuType.DIAMOND, player);
                else open(MenuType.NETHERITE, player);
            }

            case 16 -> {
                if (hasCopper) open(MenuType.NETHERITE, player);
            }
        }
    }


    private void open(MenuType type, Player player) {

        switch (type) {

            case WOODEN -> player.openInventory(new AmethystToolWoodenMenu(plugin).create(player));
            case STONE -> player.openInventory(new AmethystToolStoneMenu(plugin).create(player));
            case IRON -> player.openInventory(new AmethystToolIronMenu(plugin).create(player));
            case COPPER -> player.openInventory(new AmethystToolCopperMenu(plugin).create(player));
            case GOLD -> player.openInventory(new AmethystToolGoldenMenu(plugin).create(player));
            case DIAMOND -> player.openInventory(new AmethystToolDiamondMenu(plugin).create(player));
            case NETHERITE -> player.openInventory(new AmethystToolNetheriteMenu(plugin).create(player));
        }
    }

    public enum MenuType {WOODEN, STONE, IRON, COPPER, GOLD, DIAMOND, NETHERITE}
}