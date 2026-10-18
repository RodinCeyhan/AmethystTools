package com.rtc.amethystTools.menu;

import com.rtc.amethystTools.utils.ToolUtils;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum Tier {
    WOODEN("wooden", Material.OAK_PLANKS, "<#A0522D>Wooden Tools"),
    STONE("stone", Material.STONE, "<#7D7D7D>Stone Tools"),
    IRON("iron", Material.IRON_INGOT, "<#DADADA>Iron Tools"),
    COPPER("copper", Material.COPPER_INGOT, "<#C87533>Copper Tools"),
    GOLDEN("golden", Material.GOLD_INGOT, "<#FFD700>Golden Tools"),
    DIAMOND("diamond", Material.DIAMOND, "<#4EE1D8>Diamond Tools"),
    NETHERITE("netherite", Material.NETHERITE_INGOT, "<#2B2B2F>Netherite Tools");

    public final String id;
    public final Material icon;
    public final String defaultTitle;

    Tier(String id, Material icon, String defaultTitle) {
        this.id = id;
        this.icon = icon;
        this.defaultTitle = defaultTitle;
    }

    public String configKey() {
        return "menus.menu-" + id;
    }

    public boolean isAvailable() {
        return this != COPPER || ToolUtils.hasCopperTools();
    }

    public static List<Tier> listAvailable() {
        return Arrays.stream(values()).filter(Tier::isAvailable).toList();
    }
}