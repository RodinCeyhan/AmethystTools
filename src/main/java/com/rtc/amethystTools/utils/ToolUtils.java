package com.rtc.amethystTools.utils;

import com.rtc.amethystTools.AmethystTools;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Locale;

@SuppressWarnings("ALL")
public class ToolUtils {

    private ToolUtils() {
    }

    public static boolean isAmethystTool(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) return false;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();

        if (!pdc.has(AmethystTools.KEY_TOOL, PersistentDataType.BYTE)) return false;

        Byte flag = pdc.get(AmethystTools.KEY_TOOL, PersistentDataType.BYTE);
        return flag != null && flag == (byte) 1;
    }

    public static String getToolTier(ItemStack item) {
        return readString(item, AmethystTools.KEY_TIER);
    }

    public static String getToolType(ItemStack item) {
        return readString(item, AmethystTools.KEY_TYPE);
    }

    private static String readString(ItemStack item, NamespacedKey key) {
        if (!isAmethystTool(item)) return null;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(key, PersistentDataType.STRING) ? pdc.get(key, PersistentDataType.STRING) : null;
    }

    @SuppressWarnings("deprecation")
    public static void markAsAmethystTool(ItemMeta meta, String tier, String type) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(AmethystTools.KEY_TOOL, PersistentDataType.BYTE, (byte) 1);
        pdc.set(AmethystTools.KEY_TIER, PersistentDataType.STRING, tier);
        pdc.set(AmethystTools.KEY_TYPE, PersistentDataType.STRING, type);
    }

    public static String getTierName(String tier) {
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

    public static String getTypeName(String type) {
        return switch (type) {
            case "pickaxe" -> "Pickaxe";
            case "axe" -> "Axe";
            case "shovel" -> "Shovel";
            default -> "(Error Tool)";
        };
    }

    public static Material getMaterial(String tier, String type) {
        String materialName = tier.toUpperCase(Locale.ENGLISH) + "_" + type.toUpperCase(Locale.ENGLISH);
        return Material.matchMaterial(materialName);
    }

    public static boolean hasCopperTools() {
        return Material.matchMaterial("COPPER_PICKAXE") != null;
    }
}