package com.rtc.amethystTools.command;

import com.rtc.amethystTools.AmethystTools;
import org.bukkit.Bukkit;
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
public class AmethystToolsGiveCommand extends BukkitCommand {

    private static final List<String> TIER = Arrays.asList(
            "wooden", "stone", "iron", "golden", "diamond", "netherite"
    );
    private static final List<String> TYPE = Arrays.asList(
            "pickaxe", "axe", "shovel"
    );

    private final AmethystTools plugin;

    public AmethystToolsGiveCommand(AmethystTools plugin) {
        super("amethysttoolsgive");
        this.plugin = plugin;

        setDescription("Amethyst tool give command");
        setUsage("/amethysttoolsgive");
        setPermission("amethysttools.give");
        setPermissionMessage("§cYou don't have permission for this command.");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String @NonNull [] args) {

        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.use-give", "&cUse: &7/amethysttoolsgive (player) (tier) (type)")));
            if (sender instanceof Player player) {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.use-give", "&cUse: &7/amethysttoolsgive (player) (tier) (type)")));
            }
            return true;
        }

        String argplayer = args[0];
        Player target = Bukkit.getPlayerExact(argplayer);
        if (target != null) {

            if (args.length < 2 || args[1].isBlank()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.enter-tier", "&cPlease enter a tool tier (wooden, stone, iron, golden, diamond, netherite).")));
                if (sender instanceof Player player) {
                    player.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.enter-tier", "&cPlease enter a tool tier (wooden, stone, iron, golden, diamond, netherite).")));
                }
                return true;
            }

            String argtier = args[1].toLowerCase();
            if (!TIER.contains(argtier)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.wrong-material", "&cPlease enter a valid material.")));
                if (sender instanceof Player player) {
                    player.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.wrong-material", "&cPlease enter a valid material.")));
                }
                return true;
            }

            if (args.length < 3 || args[2].isBlank()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.empty-arg2", "&cPlease enter a tool type (pickaxe, axe, shovel).")));
                if (sender instanceof Player player) {
                    player.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.empty-arg2", "&cPlease enter a tool type (pickaxe, axe, shovel).")));
                }
                return true;
            }

            String argtype = args[2].toLowerCase();
            if (!TYPE.contains(argtype)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.wrong-material", "&cPlease enter a valid material.")));
                if (sender instanceof Player player) {
                    player.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.wrong-material", "&cPlease enter a valid material.")));
                }
                return true;
            }

            if (target.getInventory().firstEmpty() == -1) {
                String invfullmsgtier = getTierName(argtier);
                String invfullmsgtool = getTypeName(argtype);

                target.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.invfull", "&c{material} {tool} could not be added to your inventory because it is full.")).replace("{material}", invfullmsgtier).replace("{tool}", invfullmsgtool));
                target.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.invfull-actionbar", "&cYour inventory is full!")));
                return true;
            }

            Material mat = getMaterial(argtier, argtype);
            if (mat == null) return true;

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return true;

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(AmethystTools.KEY_TOOL, PersistentDataType.BYTE, (byte) 1);
            pdc.set(AmethystTools.KEY_TIER, PersistentDataType.STRING, argtier);
            pdc.set(AmethystTools.KEY_TYPE, PersistentDataType.STRING, argtype);

            String toolDisplayName = switch (argtype) {
                case "pickaxe" -> plugin.getConfig().getString("item.item-pickaxe", "&5ᴘɪᴄᴋᴀхᴇ");
                case "axe" -> plugin.getConfig().getString("item.item-axe", "&5ᴀхᴇ");
                case "shovel" -> plugin.getConfig().getString("item.item-shovel", "&5ѕʜᴏᴠᴇʟ");
                default -> "&cERROR";
            };

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("item.item-name", "&5ᴀᴍᴇᴛʜʏѕᴛ") + " " + toolDisplayName));
            meta.setLore(List.of("§7Breaks 9 Blocks at Once"));
            meta.setCustomModelData(2235897);
            item.setItemMeta(meta);

            Player itemplayer = target.getPlayer();

            if (itemplayer != null) {
                itemplayer.getInventory().addItem(item);
                itemplayer.playSound(itemplayer.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 2.0f);
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player-notfound", "&c{player} not found.")).replace("{player}", target.getName()));
                if (sender instanceof Player player) {
                    player.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player-notfound-actionbar", "&cPlayer not found")));
                }
            }
            return true;
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player-notfound", "&c{player} not found.")).replace("{player}", argplayer));
            if (sender instanceof Player player) {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player-notfound-actionbar", "&cPlayer not found")));
            }
        }
        return true;
    }

    private String getTierName(String tier) {
        return switch (tier) {
            case "wooden" -> "Wooden";
            case "stone" -> "Stone";
            case "iron" -> "Iron";
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
            for (Player players : Bukkit.getOnlinePlayers()) {
                String name = players.getName();
                if (name.startsWith(partial)) completions.add(name);
            }
            return completions;
        } else if (args.length == 2) {
            List<String> completions = new ArrayList<>();
            String partial = args[1].toLowerCase();
            for (String s : TIER) {
                if (s.startsWith(partial)) completions.add(s);
            }
            return completions;
        } else if (args.length == 3) {
            List<String> completions = new ArrayList<>();
            String partial = args[2].toLowerCase();
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
