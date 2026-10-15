package com.rtc.amethystTools.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.rtc.amethystTools.AmethystTools;
import com.rtc.amethystTools.menu.AmethystToolMainMenu;
import com.rtc.amethystTools.utils.MessageUtils;
import com.rtc.amethystTools.utils.ToolUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"deprecation", "unused", "SameReturnValue", "SpellCheckingInspection", "UnstableApiUsage"})
public class AmethystToolCommand {

    private static final List<String> TIER = new ArrayList<>();

    static {
        TIER.add("wooden");
        TIER.add("stone");
        TIER.add("iron");
        TIER.add("golden");
        TIER.add("diamond");
        TIER.add("netherite");

        if (Material.matchMaterial("COPPER_PICKAXE") != null) {
            TIER.add("copper");
        }
    }

    private static final List<String> TYPE = Arrays.asList(
            "pickaxe", "axe", "shovel"
    );

    private final AmethystTools plugin;

    public AmethystToolCommand(AmethystTools plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("UnstableApiUsage")
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("amethysttools")
                .requires(source -> source.getSender().hasPermission("amethysttools.give"))
                .executes(this::openMenu)

                .then(Commands.argument("tier", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            String remaining = builder.getRemaining().toLowerCase();
                            TIER.stream()
                                    .filter(tier -> tier.startsWith(remaining))
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            Player player = getPlayerOrSendError(context);
                            if (player == null) return Command.SINGLE_SUCCESS;

                            String tierInput = StringArgumentType.getString(context, "tier").toLowerCase();
                            if (TIER.contains(tierInput)) {
                                player.sendMessage(MessageUtils.parse(plugin.getConfig().getString("messages.empty-arg2", "&cPlease enter a tool type (pickaxe, axe, shovel).")));
                            } else {
                                player.sendMessage(MessageUtils.parse(plugin.getConfig().getString("messages.wrong-material", "&cPlease enter a valid material.")));
                            }
                            return Command.SINGLE_SUCCESS;
                        })

                        .then(Commands.argument("type", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    String remaining = builder.getRemaining().toLowerCase();
                                    TYPE.stream()
                                            .filter(type -> type.startsWith(remaining))
                                            .forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(this::giveTool)
                        )
                );
    }

    @SuppressWarnings("SameReturnValue")
    private int openMenu(CommandContext<CommandSourceStack> context) {
        Player player = getPlayerOrSendError(context);
        if (player == null) return Command.SINGLE_SUCCESS;

        player.openInventory(new AmethystToolMainMenu(plugin).create(player));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 1.0F);
        return Command.SINGLE_SUCCESS;
    }

    private int giveTool(CommandContext<CommandSourceStack> context) {
        Player player = getPlayerOrSendError(context);
        if (player == null) return Command.SINGLE_SUCCESS;

        String tierInput = StringArgumentType.getString(context, "tier").toLowerCase();
        String typeInput = StringArgumentType.getString(context, "type").toLowerCase();

        if (!TIER.contains(tierInput)) {
            player.sendMessage(MessageUtils.parse(plugin.getConfig().getString("messages.wrong-material", "&cPlease enter a valid material.")));
            return Command.SINGLE_SUCCESS;
        }

        if (!TYPE.contains(typeInput)) {
            player.sendMessage(MessageUtils.parse(plugin.getConfig().getString("messages.wrong-tool", "&cPlease enter a valid tool.")));
            return Command.SINGLE_SUCCESS;
        }

        if (player.getInventory().firstEmpty() == -1) {
            String invfullmsgtier = ToolUtils.getTierName(tierInput);
            String invfullmsgtool = ToolUtils.getTypeName(typeInput);

            player.sendMessage(MessageUtils.parse(plugin.getConfig().getString("messages.invfull", "&c{material} {tool} could not be added to your inventory because it is full.").replace("{material}", invfullmsgtier).replace("{tool}", invfullmsgtool)));
            player.sendActionBar(MessageUtils.parse(plugin.getConfig().getString("messages.invfull-actionbar", "&cYour inventory is full!")));
            return Command.SINGLE_SUCCESS;
        }

        Material mat = ToolUtils.getMaterial(tierInput, typeInput);
        if (mat == null) return Command.SINGLE_SUCCESS;

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return Command.SINGLE_SUCCESS;

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

        meta.displayName(MessageUtils.parse(plugin.getConfig().getString("item.item-name", "&5ᴀᴍᴇᴛʜʏѕᴛ") + " " + toolDisplayName));
        meta.setLore(List.of("§7Breaks 9 Blocks at Once"));
        meta.setCustomModelData(2235897);


        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 2.0f);

        return Command.SINGLE_SUCCESS;
    }

    private Player getPlayerOrSendError(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            context.getSource().getSender().sendMessage(MessageUtils.parse(plugin.getConfig().getString("messages.only-players", "&cOnly players can use this command.")));
            return null;
        }
        return player;
    }
}