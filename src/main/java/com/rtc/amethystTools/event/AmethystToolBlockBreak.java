
package com.rtc.amethystTools.event;

import com.rtc.amethystTools.AmethystTools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Tag;
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

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnnecessaryReturnStatement"})
public class AmethystToolBlockBreak implements Listener {

    private final AmethystTools plugin;

    private final Set<UUID> processingPlayers = new HashSet<>();

    private static final int MAX_TREE_BLOCKS = 64;
    private static final int MAX_TREE_SCAN = 4096;


    private enum ToolKind {
        PICKAXE, SHOVEL, AXE, OTHER
    }

    public AmethystToolBlockBreak(AmethystTools plugin) {
        this.plugin = plugin;
    }

    private boolean isNewMode() {
        return "NEW".equalsIgnoreCase(
                plugin.getConfig().getString("block-breaking-mode", "NEW")
        );
    }

    private boolean isBlacklisted(Material material) {
        for (String name : plugin.getConfig().getStringList("blacklist-blocks")) {
            Material blacklisted = Material.matchMaterial(name.trim());

            if (blacklisted == material) {
                return true;
            }
        }

        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (processingPlayers.contains(uuid)) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!isAmethystTool(tool)) {
            return;
        }

        Block center = event.getBlock();

        if (isBlacklisted(center.getType())) {
            return;
        }

        if (isPlantOrGrass(center.getType())) {
            return;
        }

        processingPlayers.add(uuid);

        try {
            ToolKind kind = getToolKind(tool.getType());

            if (!isNewMode()) {
                float pitch = player.getLocation().getPitch();

                if (pitch > 45 || pitch < -45) {
                    breakVerticalAreaOld(center, player);
                } else {
                    breakHorizontalAreaOld(center, player);
                }

                return;
            }

            if (kind == ToolKind.AXE) {
                if (!isLog(center.getType())) {
                    return;
                }

                event.setCancelled(true);
                breakTree(center, player);
                return;
            }

            if (kind != ToolKind.PICKAXE && kind != ToolKind.SHOVEL) {
                return;
            }

            float pitch = player.getLocation().getPitch();

            if (pitch > 45.0f || pitch < -45.0f) {
                breakVerticalArea(center, player);
            } else {
                breakHorizontalArea(center, player);
            }

        } finally {
            processingPlayers.remove(uuid);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOriginalBlockBreakEffects(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (processingPlayers.contains(player.getUniqueId())) {
            return;
        }

        if (!isAmethystTool(player.getInventory().getItemInMainHand())) {
            return;
        }

        Block block = event.getBlock();

        if (isBlacklisted(block.getType())) {
            return;
        }
    }

    private void breakHorizontalAreaOld(Block center, Player player) {
        BlockFace face = player.getFacing();

        for (int y = -1; y <= 1; y++) {
            for (int side = -1; side <= 1; side++) {
                Block target;

                if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
                    target = center.getRelative(side, y, 0);
                } else {
                    target = center.getRelative(0, y, side);
                }

                if (!target.equals(center)) {
                    breakBlockSafeOld(target, player);
                }
            }
        }
    }

    private void breakVerticalAreaOld(Block center, Player player) {
        BlockFace face = player.getFacing();
        Vector direction = player.getLocation().getDirection();

        if (Math.abs(direction.getY()) > 0.7) {
            int depth = direction.getY() < 0 ? -1 : 1;

            for (int y = 0; y < 2; y++) {
                for (int side = -1; side <= 1; side++) {
                    Block target;

                    if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
                        target = center.getRelative(side, depth * y, 0);
                    } else {
                        target = center.getRelative(0, depth * y, side);
                    }

                    if (!target.equals(center)) {
                        breakBlockSafeOld(target, player);
                    }
                }
            }

            return;
        }

        for (int side = -1; side <= 1; side++) {
            for (int depth = 1; depth <= 2; depth++) {
                Block target;

                if (face == BlockFace.NORTH) {
                    target = center.getRelative(side, 0, -depth);
                } else if (face == BlockFace.SOUTH) {
                    target = center.getRelative(side, 0, depth);
                } else if (face == BlockFace.EAST) {
                    target = center.getRelative(depth, 0, side);
                } else {
                    target = center.getRelative(-depth, 0, side);
                }

                breakBlockSafeOld(target, player);
            }
        }
    }

    private void breakBlockSafeOld(Block block, Player player) {
        if (block.getType().isAir() || isBlacklisted(block.getType())) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!block.isPreferredTool(tool)) {
            return;
        }

        if (fireBreakEventAndBreak(block, player, tool)) {
            playAmethystEffects(block);
        }
    }

    private void breakHorizontalArea(Block center, Player player) {
        BlockFace face = player.getFacing();

        for (int y = -1; y <= 1; y++) {
            for (int side = -1; side <= 1; side++) {
                if (y == 0 && side == 0) {
                    continue;
                }

                Block target;

                if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
                    target = center.getRelative(side, y, 0);
                } else {
                    target = center.getRelative(0, y, side);
                }

                breakBlockSafe(target, player);
            }
        }
    }

    private void breakVerticalArea(Block center, Player player) {
        Vector direction = player.getLocation().getDirection();

        if (Math.abs(direction.getY()) > 0.7) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) {
                        continue;
                    }

                    breakBlockSafe(center.getRelative(x, 0, z), player);
                }
            }

            return;
        }

        BlockFace face = player.getFacing();

        for (int y = -1; y <= 1; y++) {
            for (int side = -1; side <= 1; side++) {
                if (y == 0 && side == 0) {
                    continue;
                }

                Block target;

                if (face == BlockFace.NORTH) {
                    target = center.getRelative(side, y, -1);
                } else if (face == BlockFace.SOUTH) {
                    target = center.getRelative(side, y, 1);
                } else if (face == BlockFace.EAST) {
                    target = center.getRelative(1, y, side);
                } else {
                    target = center.getRelative(-1, y, side);
                }

                breakBlockSafe(target, player);
            }
        }
    }

    private void breakBlockSafe(Block block, Player player) {
        if (block.getType().isAir() || isBlacklisted(block.getType()) || isPlantOrGrass(block.getType())) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!block.isPreferredTool(tool)) {
            return;
        }

        if (fireBreakEventAndBreak(block, player, tool)) {
            playAmethystEffects(block);
        }
    }


    private void breakTree(Block start, Player player) {
        if (!isLog(start.getType())) {
            return;
        }

        ArrayDeque<Block> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Set<Block> logs = new HashSet<>();

        queue.add(start);
        visited.add(blockKey(start));

        int scanned = 0;

        while (!queue.isEmpty() && logs.size() < MAX_TREE_BLOCKS && scanned < MAX_TREE_SCAN) {

            Block current = queue.removeFirst();
            scanned++;

            boolean currentIsLog = isLog(current.getType());
            boolean currentIsLeaves = Tag.LEAVES.isTagged(current.getType());

            if (currentIsLog) {
                logs.add(current);
            } else if (!currentIsLeaves) {
                continue;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }

                        Block next = current.getRelative(dx, dy, dz);
                        boolean nextIsLog = isLog(next.getType());
                        boolean nextIsLeaves = Tag.LEAVES.isTagged(next.getType());

                        if (currentIsLeaves && !nextIsLog) {
                            continue;
                        }

                        if (!nextIsLog && !nextIsLeaves) {
                            continue;
                        }

                        if (!visited.add(blockKey(next))) {
                            continue;
                        }

                        queue.addLast(next);
                    }
                }
            }
        }

        ItemStack tool = player.getInventory().getItemInMainHand();

        if (isLog(start.getType()) && fireBreakEventAndBreak(start, player, tool)) {
            playAmethystEffects(start);
        }

        for (Block log : logs) {
            if (log.equals(start) || !isLog(log.getType())) {
                continue;
            }

            if (fireBreakEventAndBreak(log, player, tool)) {
                playAmethystEffects(log);
            }
        }
    }


    private boolean fireBreakEventAndBreak(Block block, Player player, ItemStack tool) {
        if (block.getType().isAir() || isBlacklisted(block.getType())) {
            return false;
        }

        BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(breakEvent);

        if (breakEvent.isCancelled()) {
            return false;
        }

        return block.breakNaturally(tool);
    }

    private void playAmethystEffects(Block block) {
        var location = block.getLocation().add(0.5, 0.5, 0.5);

        block.getWorld().playSound(location, Sound.BLOCK_AMETHYST_CLUSTER_HIT, 1.0f, 1.0f);
        block.getWorld().spawnParticle(Particle.PORTAL, location, 20, 0.35, 0.35, 0.35, 0.0);
    }

    private boolean isAmethystTool(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        return meta.getPersistentDataContainer().has(AmethystTools.KEY_TOOL, PersistentDataType.BYTE) || (meta.hasCustomModelData() && meta.getCustomModelData() == 2235897);
    }

    private ToolKind getToolKind(Material material) {
        String name = material.name();

        if (name.endsWith("_PICKAXE")) {
            return ToolKind.PICKAXE;
        }

        if (name.endsWith("_SHOVEL")) {
            return ToolKind.SHOVEL;
        }

        if (name.endsWith("_AXE")) {
            return ToolKind.AXE;
        }

        return ToolKind.OTHER;
    }

    private boolean isLog(Material material) {
        return Tag.LOGS.isTagged(material)
                || material.name().endsWith("_STEM")
                || material.name().endsWith("_HYPHAE")
                || material == Material.MUSHROOM_STEM;
    }


    private boolean isPlantOrGrass(Material material) {
        if (material == null) {
            return false;
        }

        return material == Material.SHORT_GRASS
                || material == Material.TALL_GRASS
                || material == Material.FERN
                || material == Material.LARGE_FERN
                || material == Material.DEAD_BUSH
                || material == Material.PINK_PETALS
                || material == Material.MOSS_CARPET
                || material == Material.VINE
                || material == Material.GLOW_LICHEN
                || material == Material.HANGING_ROOTS
                || material == Material.SUGAR_CANE
                || material == Material.BAMBOO
                || material == Material.CACTUS
                || material == Material.KELP
                || material == Material.KELP_PLANT
                || material == Material.SEAGRASS
                || material == Material.TALL_SEAGRASS
                || material == Material.LILY_PAD
                || material == Material.BIG_DRIPLEAF
                || material == Material.SMALL_DRIPLEAF
                || material == Material.WEEPING_VINES
                || material == Material.TWISTING_VINES
                || material == Material.NETHER_WART
                || material == Material.COCOA
                || material == Material.SWEET_BERRY_BUSH
                || material.name().contains("FLOWER")
                || material.name().contains("SAPLING")
                || material.name().contains("CROP")
                || material.name().contains("SEED")
                || material.name().contains("LEAVES");
    }


    private String blockKey(Block block) {
        return block.getWorld().getUID()
                + ":" + block.getX()
                + ":" + block.getY()
                + ":" + block.getZ();
    }
}
