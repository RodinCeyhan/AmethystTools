package com.rtc.amethystTools.menu;

import com.rtc.amethystTools.AmethystTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"deprecation", "unused", "ClassCanBeRecord", "FieldCanBeLocal"})
public class AmethystToolMainMenu implements Listener {

    private final AmethystTools plugin;

    public AmethystToolMainMenu(AmethystTools plugin) {
        this.plugin = plugin;
    }

    public Inventory create(Player player) {
        int size = 27;

        Inventory inv = Bukkit.createInventory(null, size, MENU_TITLE);

        inv.setItem(10, buildWoodenCategory(player));
        inv.setItem(11, buildStoneCategory(player));
        inv.setItem(12, buildIronCategory(player));
        inv.setItem(13, buildCopperCategory(player));
        inv.setItem(14, buildGoldCategory(player));
        inv.setItem(15, buildDiamondCategory(player));
        inv.setItem(16, buildNetheriteCategory(player));

        return inv;
    }

    private ItemStack buildWoodenCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.OAK_PLANKS));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = "&#A0522DWooden Tools";
        meta.setDisplayName(color(name));

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildStoneCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.STONE));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = "&#7D7D7DStone Tools";
        meta.setDisplayName(color(name));

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildIronCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.IRON_INGOT));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = "&#DADADAIron Tools";
        meta.setDisplayName(color(name));

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildCopperCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.COPPER_INGOT));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = "&#C87533Copper Tools";
        meta.setDisplayName(color(name));

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildGoldCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.GOLD_INGOT));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = "&#FFD700Golden Tools";
        meta.setDisplayName(color(name));

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildDiamondCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.DIAMOND));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = "&#4EE1D8Diamond Tools";
        meta.setDisplayName(color(name));

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildNetheriteCategory(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.NETHERITE_INGOT));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = "&#2B2B2FNetherite Tools";
        meta.setDisplayName(color(name));

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(MENU_TITLE)) return;
        Player player = (Player) event.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, 1.0f, 1.0f);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().equals(MENU_TITLE)) return;
        if (e.getClickedInventory() == null) return;

        e.setCancelled(true);

        switch (e.getSlot()) {
            case 10 -> player.openInventory(new AmethystToolWoodenMenu(plugin).create(player));
            case 11 -> player.openInventory(new AmethystToolStoneMenu(plugin).create(player));
            case 12 -> player.openInventory(new AmethystToolIronMenu(plugin).create(player));
            case 13 -> player.openInventory(new AmethystToolCopperMenu(plugin).create(player));
            case 14 -> player.openInventory(new AmethystToolGoldenMenu(plugin).create(player));
            case 15 -> player.openInventory(new AmethystToolDiamondMenu(plugin).create(player));
            case 16 -> player.openInventory(new AmethystToolNetheriteMenu(plugin).create(player));
            default -> {
            }
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

    public static final String MENU_TITLE = color("&5Amethyst Tools");
}