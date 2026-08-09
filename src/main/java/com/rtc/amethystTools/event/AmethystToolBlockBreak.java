package com.rtc.amethystTools.event;

import com.rtc.amethystTools.AmethystTools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"FieldCanBeLocal", "unused", "RedundantIfStatement"})
public class AmethystToolBlockBreak implements Listener {

    private final AmethystTools plugin;

    public AmethystToolBlockBreak(AmethystTools plugin) {
        this.plugin = plugin;
    }

    private final Set<UUID> processingPlayers = new HashSet<>();

    private static final Set<Material> BLACKLIST = new HashSet<>();

    static {

        add("CHEST");
        add("TRAPPED_CHEST");

        add("SHULKER_BOX");
        add("BLACK_SHULKER_BOX");
        add("BLUE_SHULKER_BOX");
        add("BROWN_SHULKER_BOX");
        add("CYAN_SHULKER_BOX");
        add("GRAY_SHULKER_BOX");
        add("GREEN_SHULKER_BOX");
        add("LIGHT_BLUE_SHULKER_BOX");
        add("LIGHT_GRAY_SHULKER_BOX");
        add("LIME_SHULKER_BOX");
        add("MAGENTA_SHULKER_BOX");
        add("ORANGE_SHULKER_BOX");
        add("PINK_SHULKER_BOX");
        add("PURPLE_SHULKER_BOX");
        add("RED_SHULKER_BOX");
        add("WHITE_SHULKER_BOX");
        add("YELLOW_SHULKER_BOX");

        add("BEDROCK");
        add("OBSIDIAN");
        add("CRYING_OBSIDIAN");

        add("COMMAND_BLOCK");
        add("CHAIN_COMMAND_BLOCK");
        add("REPEATING_COMMAND_BLOCK");

        // 1.21.9+
        add("COPPER_CHEST");
        add("EXPOSED_COPPER_CHEST");
        add("WEATHERED_COPPER_CHEST");
        add("OXIDIZED_COPPER_CHEST");

        add("WAXED_COPPER_CHEST");
        add("WAXED_EXPOSED_COPPER_CHEST");
        add("WAXED_WEATHERED_COPPER_CHEST");
        add("WAXED_OXIDIZED_COPPER_CHEST");
    }

    private static void add(String materialName) {

        Material material = Material.matchMaterial(materialName);

        if (material != null) {
            BLACKLIST.add(material);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (processingPlayers.contains(player.getUniqueId())) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!isAmethystTool(tool)) {
            return;
        }

        Block center = event.getBlock();

        if (BLACKLIST.contains(center.getType())) {
            return;
        }

        UUID uuid = player.getUniqueId();
        processingPlayers.add(uuid);

        try {
            float pitch = player.getLocation().getPitch();

            if (pitch > 45 || pitch < -45) {
                breakVerticalArea(center, player);
            } else {
                breakHorizontalArea(center, player);
            }

        } finally {
            processingPlayers.remove(uuid);
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

                if (target.equals(center)) {
                    continue;
                }

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

                    if (target.equals(center)) {
                        continue;
                    }

                    breakBlockSafe(target, player);
                }
            }

            return;
        }

        for (int s = -1; s <= 1; s++) {

            for (int d = 1; d <= 2; d++) {

                Block target;

                if (face == BlockFace.NORTH) {
                    target = center.getRelative(s, 0, -d);
                } else if (face == BlockFace.SOUTH) {
                    target = center.getRelative(s, 0, d);
                } else if (face == BlockFace.EAST) {
                    target = center.getRelative(d, 0, s);
                } else {
                    target = center.getRelative(-d, 0, s);
                }

                breakBlockSafe(target, player);
            }
        }
    }

    private void breakBlockSafe(Block block, Player player) {

        if (block.getType().isAir()) {
            return;
        }

        if (BLACKLIST.contains(block.getType())) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!block.isPreferredTool(tool)) {
            return;
        }

        BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);

        Bukkit.getPluginManager().callEvent(breakEvent);

        if (breakEvent.isCancelled()) {
            return;
        }

        block.breakNaturally(tool);
    }

    private boolean isAmethystTool(ItemStack item) {

        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta.getPersistentDataContainer()
                .has(AmethystTools.KEY_TOOL, PersistentDataType.BYTE)) {
            return true;
        }

        if (meta.hasCustomModelData() && meta.getCustomModelData() == 2235897) {
            return true;
        }

        return false;
    }
}