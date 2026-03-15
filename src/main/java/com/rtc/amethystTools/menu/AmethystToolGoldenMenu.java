package com.rtc.amethystTools.menu;

import com.rtc.amethystTools.AmethystTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"deprecation", "unused", "ClassCanBeRecord", "FieldCanBeLocal"})
public class AmethystToolGoldenMenu implements Listener {

    private final AmethystTools plugin;

    public AmethystToolGoldenMenu(AmethystTools plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings({"NullableProblems", "DataFlowIssue"})
    public static class GoldenMenuHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public Inventory create(Player player) {
        int size = 27;
        String MENU_TITLE = plugin.getConfig().getString("menus.menu-golden", "&#FFD700Golden Tools");
        Inventory inv = Bukkit.createInventory(new GoldenMenuHolder(), size, color(MENU_TITLE));

        inv.setItem(12, buildPickaxe(player));
        inv.setItem(13, buildShovel(player));
        inv.setItem(14, buildAxe(player));
        inv.setItem(18, buildBackPage(player));

        return inv;
    }

    private ItemStack buildPickaxe(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.GOLDEN_PICKAXE));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String materialname = plugin.getConfig().getString("item.item-name", "&5ᴀᴍᴇᴛʜʏѕᴛ");
        String materialpickaxe = plugin.getConfig().getString("item.item-pickaxe", "&5ᴘɪᴄᴋᴀхᴇ");

        String name = color(materialname + " " + materialpickaxe);
        meta.setDisplayName(name);
        meta.setLore(List.of("§7Breaks 9 Blocks at Once",
                "",
                color("&#FFFF00Click to get")));
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildShovel(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.GOLDEN_SHOVEL));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String materialname = plugin.getConfig().getString("item.item-name", "&5ᴀᴍᴇᴛʜʏѕᴛ");
        String materialshovel = plugin.getConfig().getString("item.item-shovel", "&5ѕʜᴏᴠᴇʟ");

        String name = color(materialname + " " + materialshovel);
        meta.setDisplayName(name);
        meta.setLore(List.of("§7Breaks 9 Blocks at Once",
                "",
                color("&#FFFF00Click to get")));
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildAxe(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.GOLDEN_AXE));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String materialname = plugin.getConfig().getString("item.item-name", "&5ᴀᴍᴇᴛʜʏѕᴛ");
        String materialaxe = plugin.getConfig().getString("item.item-axe", "&5ᴀхᴇ");

        String name = color(materialname + " " + materialaxe);
        meta.setDisplayName(name);
        meta.setLore(List.of("§7Breaks 9 Blocks at Once",
                "",
                color("&#FFFF00Click to get")));
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildBackPage(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.ARROW));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String previouspage = plugin.getConfig().getString("menus.item-previous", "&e<- ᴘʀᴇᴠɪᴏᴜѕ ᴘᴀɢᴇ");

        String name = color(previouspage);
        meta.setDisplayName(name);

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof GoldenMenuHolder)) return;
        if (event.getClickedInventory() == null) return;

        event.setCancelled(true);

        switch (event.getSlot()) {
            case 12 -> {
                player.performCommand("amethysttools golden pickaxe");
                player.closeInventory();
            }
            case 13 -> {
                player.performCommand("amethysttools golden shovel");
                player.closeInventory();
            }
            case 14 -> {
                player.performCommand("amethysttools golden axe");
                player.closeInventory();
            }
            case 18 -> player.openInventory(new AmethystToolMainMenu(plugin).create(player));
        }
    }


    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String color(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            String hex = matcher.group(1);
            text = text.replace("&#" + hex,
                    net.md_5.bungee.api.ChatColor.of("#" + hex).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}