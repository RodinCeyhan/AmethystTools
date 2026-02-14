package com.rtc.amethystTools.command;

import com.rtc.amethystTools.AmethystTools;
import com.rtc.amethystTools.menu.AmethystToolMainMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"deprecation", "unused", "FieldCanBeLocal"})
public class AmethystToolCommand extends BukkitCommand {

    private static final List<String> TIER = Arrays.asList(
            "wooden", "stone", "iron", "golden", "diamond", "netherite"
    );
    private static final List<String> TYPE = Arrays.asList(
            "pickaxe", "axe", "shovel"
    );

    private final AmethystTools plugin;

    public AmethystToolCommand(AmethystTools plugin) {
        super("amethysttools");
        this.plugin = plugin;

        setDescription("Amethyst tool command");
        setUsage("/amethysttools");
        setPermission("amethysttools.give");
        setPermissionMessage("§cYou don't have permission for this command.");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String @NonNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.only-players", "&cOnly players can use this command.")));
            return true;
        }

        if (args.length == 0) {
            player.openInventory(new AmethystToolMainMenu(plugin).create(player));
            player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1, 1);
            return true;
        }

        if (args.length == 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.empyt-arg2", "&cPlease enter an argument.")));
            return true;
        }

        if (!args[0].isBlank() && !TIER.contains(args[0])) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.wrong-arg1", "&cPlease enter a valid material.")));
            return true;
        }

        if (!args[1].isBlank() && !TYPE.contains(args[1])) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.wrong-arg2", "&cPlease enter a valid tool.")));
            return true;
        }


        if (player.getInventory().firstEmpty() == -1) {
            String invfullmsgtier = switch (args[0].toLowerCase()) {
                case "wooden" -> "Wooden";
                case "stone" -> "Stone";
                case "iron" -> "Iron";
                case "diamond" -> "Diamond";
                case "netherite" -> "Netherite";
                case "golden" -> "Gold";
                default -> "(Error Material)";
            };

            String invfullmsgtool = switch (args[1].toLowerCase()) {
                case "pickaxe" -> "Pickaxe";
                case "axe" -> "Axe";
                case "shovel" -> "Shovel";
                default -> "(Error Tool)";
            };
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.invfull", "&5{material} {tool} &ccould not be added to your inventory because it is full.")).replace("{material}", invfullmsgtier).replace("{tool}", invfullmsgtool));
            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.invfull-actionbar", "&cYour inventory is full!")));
            return true;
        }

        Material mat = getMaterial(args[0], args[1]);
        assert mat != null;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        pdc.set(AmethystTools.KEY_TOOL, PersistentDataType.BYTE, (byte) 1);
        pdc.set(AmethystTools.KEY_TIER, PersistentDataType.STRING, args[0].toLowerCase());
        pdc.set(AmethystTools.KEY_TYPE, PersistentDataType.STRING, args[1].toLowerCase());

        String toolNameArg = switch (args[1].toLowerCase()) {
            case "pickaxe" -> plugin.getConfig().getString("item.item-pickaxe", "&5ᴘɪᴄᴋᴀхᴇ");
            case "axe" -> plugin.getConfig().getString("item.item-axe", "&5ᴀхᴇ");
            case "shovel" -> plugin.getConfig().getString("item.item-shovel", "&5ѕʜᴏᴠᴇʟ");
            default -> "&cERROR";
        };

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("item.item-name", "&5ᴀᴍᴇᴛʜʏѕᴛ") + " " + toolNameArg));

        meta.setLore(List.of("§7Breaks 9 Blocks at Once"));

        meta.setCustomModelData(2235897);
        item.setItemMeta(meta);

        player.getInventory().addItem(item);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 2.0f);
        return true;
    }

    @Override
    public @NonNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NonNull [] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String partial = args[0].toLowerCase();
            for (String s : TIER) {
                if (s.startsWith(partial)) completions.add(s);
            }
            return completions;
        } else if (args.length == 2) {
            List<String> completions = new ArrayList<>();
            String partial = args[1].toLowerCase();
            for (String s : TYPE) {
                if (s.startsWith(partial)) completions.add(s);
            }
            return completions;
        }
        return Collections.emptyList();
    }

    private Material getMaterial(String tier, String type) {
        return switch (tier.toLowerCase()) {
            case "wooden" -> switch (type) {
                case "pickaxe" -> Material.WOODEN_PICKAXE;
                case "axe" -> Material.WOODEN_AXE;
                case "shovel" -> Material.WOODEN_SHOVEL;
                default -> null;
            };
            case "stone" -> switch (type) {
                case "pickaxe" -> Material.STONE_PICKAXE;
                case "axe" -> Material.STONE_AXE;
                case "shovel" -> Material.STONE_SHOVEL;
                default -> null;
            };
            case "iron" -> switch (type) {
                case "pickaxe" -> Material.IRON_PICKAXE;
                case "axe" -> Material.IRON_AXE;
                case "shovel" -> Material.IRON_SHOVEL;
                default -> null;
            };
            case "golden" -> switch (type) {
                case "pickaxe" -> Material.GOLDEN_PICKAXE;
                case "axe" -> Material.GOLDEN_AXE;
                case "shovel" -> Material.GOLDEN_SHOVEL;
                default -> null;
            };
            case "diamond" -> switch (type) {
                case "pickaxe" -> Material.DIAMOND_PICKAXE;
                case "axe" -> Material.DIAMOND_AXE;
                case "shovel" -> Material.DIAMOND_SHOVEL;
                default -> null;
            };
            case "netherite" -> switch (type) {
                case "pickaxe" -> Material.NETHERITE_PICKAXE;
                case "axe" -> Material.NETHERITE_AXE;
                case "shovel" -> Material.NETHERITE_SHOVEL;
                default -> null;
            };
            default -> null;
        };
    }
}
