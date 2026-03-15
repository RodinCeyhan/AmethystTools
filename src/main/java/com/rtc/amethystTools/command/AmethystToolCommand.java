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
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"deprecation", "unused", "FieldCanBeLocal"})
public class AmethystToolCommand extends BukkitCommand {

    private static final List<String> TIER = Arrays.asList(
            "wooden", "stone", "iron", "golden", "copper", "diamond", "netherite"
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
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 1.0F);
            return true;
        }

        String tierInput = args[0].toLowerCase();
        if (args.length == 1 && TIER.contains(tierInput)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.empty-arg2", "&cPlease enter a tool type (pickaxe, axe, shovel).")));
            return true;
        }

        if (!tierInput.isBlank() && !TIER.contains(tierInput)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.wrong-arg1", "&cPlease enter a valid material.")));
            return true;
        }

        String typeInput = args[1].toLowerCase();
        if (!typeInput.isBlank() && !TYPE.contains(typeInput)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.wrong-arg2", "&cPlease enter a valid tool.")));
            return true;
        }

        if (player.getInventory().firstEmpty() == -1) {
            String invfullmsgtier = getTierName(tierInput);
            String invfullmsgtool = getTypeName(typeInput);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.invfull", "&c{material} {tool} could not be added to your inventory because it is full.")).replace("{material}", invfullmsgtier).replace("{tool}", invfullmsgtool));
            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.invfull-actionbar", "&cYour inventory is full!")));
            return true;
        }

        Material mat = getMaterial(tierInput, typeInput);
        if (mat == null) return true;

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return true;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(AmethystTools.KEY_TOOL, PersistentDataType.BYTE, (byte) 1);
        pdc.set(AmethystTools.KEY_TIER, PersistentDataType.STRING, tierInput);
        pdc.set(AmethystTools.KEY_TYPE, PersistentDataType.STRING, typeInput);

        String toolDisplayName = switch (typeInput) {
            case "pickaxe" -> plugin.getConfig().getString("item.item-pickaxe", "&5ᴘɪᴄᴋᴀхᴇ");
            case "axe" -> plugin.getConfig().getString("item.item-axe", "&5ᴀхᴇ");
            case "shovel" -> plugin.getConfig().getString("item.item-shovel", "&5ѕʜᴏᴠᴇʟ");
            default -> "&cERROR";
        };

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("item.item-name", "&5ᴀᴍᴇᴛʜʏѕᴛ") + " " + toolDisplayName));
        meta.setLore(List.of("§7Breaks 9 Blocks at Once"));
        meta.setCustomModelData(2235897);

        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 2.0f);

        return true;
    }

    private String getTierName(String tier) {
        return switch (tier) {
            case "wooden" -> "Wooden";
            case "stone" -> "Stone";
            case "iron" -> "Iron";
            case "copper" -> "Copper";
            case "diamond" -> "Diamond";
            case "netherite" -> "Netherite";
            case "golden" -> "Gold";
            default -> "(Error Material)";
        };
    }

    private String getTypeName(String type) {
        return switch (type) {
            case "pickaxe" -> "Pickaxe";
            case "axe" -> "Axe";
            case "shovel" -> "Shovel";
            default -> "(Error Tool)";
        };
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
            case "copper" -> switch (type) {
                case "pickaxe" -> Material.COPPER_PICKAXE;
                case "axe" -> Material.COPPER_AXE;
                case "shovel" -> Material.COPPER_SHOVEL;
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
