package com.rtc.amethystTools.event;

import com.rtc.amethystTools.AmethystTools;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.Set;

@SuppressWarnings({"FieldCanBeLocal", "unused", "RedundantIfStatement", "deprecation"})
public class AmethystToolBlockBreak implements Listener {

    private final AmethystTools plugin;

    public AmethystToolBlockBreak(AmethystTools plugin) {
        this.plugin = plugin;
    }

    private static final Set<Material> BLACKLIST = EnumSet.of(
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.SHULKER_BOX,
            Material.BLACK_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.PINK_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX,
            Material.BEDROCK,
            Material.OBSIDIAN,
            Material.CRYING_OBSIDIAN,
            Material.COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK,
            Material.REPEATING_COMMAND_BLOCK,
            Material.COPPER_CHEST,
            Material.EXPOSED_COPPER_CHEST,
            Material.WEATHERED_COPPER_CHEST,
            Material.OXIDIZED_COPPER_CHEST,
            Material.WAXED_COPPER_CHEST,
            Material.WAXED_EXPOSED_COPPER_CHEST,
            Material.WAXED_WEATHERED_COPPER_CHEST,
            Material.WAXED_OXIDIZED_COPPER_CHEST
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!isAmethystTool(tool)) return;
        Block center = event.getBlock();
        if (BLACKLIST.contains(center.getType())) return;
        float pitch = player.getLocation().getPitch();
        if (pitch > 45 || pitch < -45) {
            breakVerticalArea(center, player);
        } else {
            breakHorizontalArea(center, player);
        }
    }

    private void breakHorizontalArea(Block center, Player player) {
        BlockFace face = player.getFacing();
        for (int y = -1; y <= 1; y++) {
            for (int s = -1; s <= 1; s++) {
                Block target;
                if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
                    target = center.getRelative(s, y, 0);
                } else {
                    target = center.getRelative(0, y, s);
                }
                if (target.equals(center)) continue;
                breakBlockSafe(target, player);
            }
        }
    }

    private void breakVerticalArea(Block center, Player player) {
        BlockFace face = player.getFacing();
        Vector dir = player.getLocation().getDirection();
        if (Math.abs(dir.getY()) > 0.7) {
            int depth = dir.getY() < 0 ? -1 : 1;
            for (int y = 0; y < 2; y++) {
                for (int side = -1; side <= 1; side++) {
                    Block target;
                    if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
                        target = center.getRelative(side, depth * y, 0);
                    } else {
                        target = center.getRelative(0, depth * y, side);
                    }
                    if (target.equals(center)) continue;
                    breakBlockSafe(target, player);
                }
            }
            return;
        }

        for (int s = -1; s <= 1; s++) {
            for (int d = 1; d <= 2; d++) {
                Block target;
                if (face == BlockFace.NORTH)
                    target = center.getRelative(s, 0, -d);
                else if (face == BlockFace.SOUTH)
                    target = center.getRelative(s, 0, d);
                else if (face == BlockFace.EAST)
                    target = center.getRelative(d, 0, s);
                else
                    target = center.getRelative(-d, 0, s);
                breakBlockSafe(target, player);
            }
        }
    }


    private void breakBlockSafe(Block block, Player player) {
        if (block.getType().isAir() || BLACKLIST.contains(block.getType())) return;
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!block.isPreferredTool(tool)) return;
        block.breakNaturally(tool);
    }

    private boolean isAmethystTool(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta.getPersistentDataContainer().has(AmethystTools.KEY_TOOL, PersistentDataType.BYTE)) {
            return true;
        }
        if (meta.hasCustomModelData() && meta.getCustomModelData() == 2235897) {
            return true;
        }
        return false;
    }
}