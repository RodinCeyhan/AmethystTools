
package com.rtc.amethystTools.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.rtc.amethystTools.AmethystTools;
import com.rtc.amethystTools.utils.MessageUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class AmethystToolReloadCommand {

    private final AmethystTools plugin;

    public AmethystToolReloadCommand(AmethystTools plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("UnstableApiUsage")
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("amethysttoolsreload")
                .requires(source -> source.getSender().hasPermission("amethysttools.admin"))
                .executes(this::executeReload);
    }

    @SuppressWarnings("UnstableApiUsage")
    private int executeReload(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();

        plugin.reloadConfig();

        int updated = updateExistingItemNames();

        String message = plugin.getConfig().getString("messages.reloadplugin", "&aPlugin reload is successfully complete.");

        if (sender instanceof Player player) {
            player.sendMessage(MessageUtils.parse(message));
            player.sendActionBar(MessageUtils.parse(plugin.getConfig().getString("messages.reloadplugin-actionbar", "&aReloaded Plugin")));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        } else {
            sender.sendMessage(MessageUtils.parse(message));
        }

        return Command.SINGLE_SUCCESS;
    }

    private int updateExistingItemNames() {
        int updated = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType().isAir()) {
                    continue;
                }

                ItemMeta meta = item.getItemMeta();
                if (meta == null) {
                    continue;
                }

                PersistentDataContainer pdc = meta.getPersistentDataContainer();

                Byte isTool = pdc.get(AmethystTools.KEY_TOOL, PersistentDataType.BYTE);

                if (isTool == null || isTool != (byte) 1) {
                    continue;
                }

                String type = pdc.get(AmethystTools.KEY_TYPE, PersistentDataType.STRING);

                if (type == null) {
                    continue;
                }

                String toolDisplayName = switch (type.toLowerCase()) {
                    case "pickaxe" -> plugin.getConfig().getString("item.item-pickaxe", "<dark_purple>ᴘɪᴄᴋᴀхᴇ");
                    case "axe" -> plugin.getConfig().getString("item.item-axe", "<dark_purple>ᴀхᴇ");
                    case "shovel" -> plugin.getConfig().getString("item.item-shovel", "<dark_purple>ѕʜᴏᴠᴇʟ");
                    default -> null;
                };

                if (toolDisplayName == null) {
                    continue;
                }

                String itemName = plugin.getConfig().getString("item.item-name", "<dark_purple>ᴀᴍᴇᴛʜʏѕᴛ");

                meta.displayName(MessageUtils.parse(itemName + " " + toolDisplayName));

                item.setItemMeta(meta);
                updated++;
            }
        }

        return updated;
    }
}
