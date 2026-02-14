package com.rtc.amethystTools.menu;

import com.rtc.amethystTools.AmethystTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
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

    public Inventory create(Player player) {
        int size = 27;

        Inventory inv = Bukkit.createInventory(null, size, MENU_TITLE);

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

        String name = "&5ᴀᴍᴇᴛʜʏѕᴛ ᴘɪᴄᴋᴀхᴇ";
        meta.setDisplayName(color(name));
        meta.setLore(List.of("§7Breaks 9 Blocks at Once"));

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildShovel(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.GOLDEN_SHOVEL));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = "&5ᴀᴍᴇᴛʜʏѕᴛ ѕʜᴏᴠᴇʟ";
        meta.setDisplayName(color(name));
        meta.setLore(List.of("§7Breaks 9 Blocks at Once"));

        item.setItemMeta(meta);
        return item;
    }


    private ItemStack buildAxe(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.GOLDEN_AXE));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = "&5ᴀᴍᴇᴛʜʏѕᴛ ᴀхᴇ";
        meta.setDisplayName(color(name));
        meta.setLore(List.of("§7Breaks 9 Blocks at Once"));

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildBackPage(Player player) {

        Material material = Material.valueOf(String.valueOf(Material.ARROW));

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = "&e<- ᴘʀᴇᴠɪᴏᴜѕ ᴘᴀɢᴇ";
        meta.setDisplayName(color(name));

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().equals(MENU_TITLE)) return;
        if (e.getClickedInventory() == null) return;

        e.setCancelled(true);

        switch (e.getSlot()) {
            case 12 ->  {
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

    private static final String MENU_TITLE = color("&#FFD700Golden Tools");
}