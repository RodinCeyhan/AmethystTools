package com.rtc.amethystTools.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.rtc.amethystTools.AmethystTools;
import com.rtc.amethystTools.menu.Tier;
import com.rtc.amethystTools.menu.ToolType;
import com.rtc.amethystTools.utils.MessageUtils;
import com.rtc.amethystTools.utils.ToolUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"deprecation", "UnstableApiUsage"})
public class AmethystToolCommand {

    private static final String PERM_GIVE = "amethysttools.give";
    private static final String PERM_ADMIN = "amethysttools.admin";

    private static final String DEFAULT_ITEM_NAME = "<dark_purple>ᴀᴍᴇᴛʜʏѕᴛ";
    private static final Pattern LEADING_STYLE = Pattern.compile("^(?:<[^<>]+>|&#[0-9A-Fa-f]{6}|&[0-9A-Fa-fK-Ok-oRr])+");

    private final AmethystTools plugin;

    public AmethystToolCommand(AmethystTools plugin) {
        this.plugin = plugin;
    }

    public LiteralArgumentBuilder<CommandSourceStack> register() {
        LiteralArgumentBuilder<CommandSourceStack> give = Commands.literal("give")
                .requires(source -> has(source, PERM_GIVE));

        for (Tier tier : Tier.listAvailable()) {
            LiteralArgumentBuilder<CommandSourceStack> tierNode = Commands.literal(tier.id);

            for (ToolType type : ToolType.values()) {
                tierNode.then(Commands.literal(type.id)
                        .executes(context -> giveToSelf(context, tier, type))
                        .then(Commands.argument("players", ArgumentTypes.players())
                                .executes(context -> giveToPlayers(context, tier, type))));
            }

            give.then(tierNode);
        }

        return Commands.literal("amethysttools")
                .requires(source -> has(source, PERM_GIVE) || has(source, PERM_ADMIN))
                .executes(this::openMenu)
                .then(give)
                .then(Commands.literal("reload")
                        .requires(source -> has(source, PERM_ADMIN))
                        .executes(this::reload));
    }

    private static boolean has(CommandSourceStack source, String permission) {
        return source.getSender().hasPermission(permission);
    }


    private int openMenu(CommandContext<CommandSourceStack> context) {
        Player player = playerOrNull(context);
        if (player == null) return 0;

        if (!player.hasPermission(PERM_GIVE)) {
            player.sendMessage(Component.translatable("command.unknown.command", NamedTextColor.RED));
            return 0;
        }

        player.openInventory(plugin.getToolMenus().mainMenu());
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0F, 1.0F);
        return Command.SINGLE_SUCCESS;
    }

    private int giveToSelf(CommandContext<CommandSourceStack> context, Tier tier, ToolType type) {
        Player player = playerOrNull(context);
        if (player == null) return 0;

        giveTool(player, tier, type);
        return Command.SINGLE_SUCCESS;
    }

    private int giveToPlayers(CommandContext<CommandSourceStack> context, Tier tier, ToolType type)
            throws CommandSyntaxException {

        List<Player> targets = context.getArgument("players", PlayerSelectorArgumentResolver.class)
                .resolve(context.getSource());

        if (targets.isEmpty()) {
            context.getSource().getSender()
                    .sendMessage(Component.translatable("argument.entity.notfound.player", NamedTextColor.RED));
            return 0;
        }

        for (Player target : targets) {
            giveTool(target, tier, type);
        }
        return targets.size();
    }

    private void giveTool(Player target, Tier tier, ToolType type) {
        if (target.getInventory().firstEmpty() == -1) {
            String material = styleOf(cfg("item.item-name", DEFAULT_ITEM_NAME))
                    + ToolUtils.getTierName(tier.id) + "<reset>";
            String tool = styleOf(toolName(type))
                    + ToolUtils.getTypeName(type.id) + "<reset>";

            target.sendMessage(MessageUtils.parse(cfg("messages.invfull",
                    "{material} {tool} <red>could not be added to your inventory because it is full.")
                    .replace("{material}", material)
                    .replace("{tool}", tool)));
            target.sendActionBar(MessageUtils.parse(cfg("messages.invfull-actionbar", "<red>Your inventory is full!")));
            return;
        }

        Material material = ToolUtils.getMaterial(tier.id, type.id);
        if (material == null) return;

        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> {
            meta.displayName(MessageUtils.parse(cfg("item.item-name", DEFAULT_ITEM_NAME) + " " + toolName(type)));
            if (type != ToolType.AXE) {
                meta.setLore(List.of("§7Breaks 9 Blocks at Once"));
            }
            ToolUtils.markAsAmethystTool(meta, tier.id, type.id);
        });

        target.getInventory().addItem(item);
        target.playSound(target.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 2.0f);
    }

    private int reload(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();

        plugin.reloadConfig();
        updateExistingItemNames();

        Component message = MessageUtils.parse(cfg("messages.reloadplugin", "&aPlugin reload is successfully complete."));

        if (sender instanceof Player player) {
            player.sendMessage(message);
            player.sendActionBar(MessageUtils.parse(cfg("messages.reloadplugin-actionbar", "&aReloaded Plugin")));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        } else {
            sender.sendMessage(message);
        }
        return Command.SINGLE_SUCCESS;
    }

    private void updateExistingItemNames() {
        String itemName = cfg("item.item-name", "<dark_purple>ᴀᴍᴇᴛʜʏѕᴛ");

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : player.getInventory().getContents()) {
                ToolType type = ToolType.fromId(ToolUtils.getToolType(item));
                if (type == null) continue;

                assert item != null;
                item.editMeta(meta -> meta.displayName(MessageUtils.parse(itemName + " " + toolName(type))));
            }
        }
    }

    private String toolName(ToolType type) {
        return cfg(type.nameKey(), "<dark_purple>" + type.id);
    }

    private String cfg(String key, String def) {
        return plugin.getConfig().getString(key, def);
    }

    private Player playerOrNull(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        if (sender instanceof Player player) return player;

        sender.sendMessage(MessageUtils.parse(cfg("messages.only-players", "<red>Only players can use this command.")));
        return null;
    }

    private static String styleOf(String configValue) {
        Matcher matcher = LEADING_STYLE.matcher(configValue);
        return matcher.find() ? matcher.group() : "";
    }
}