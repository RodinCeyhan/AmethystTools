package com.rtc.amethystTools;

import com.rtc.amethystTools.command.AmethystToolCommand;
import com.rtc.amethystTools.command.AmethystToolReloadCommand;
import com.rtc.amethystTools.event.AmethystToolBlockBreak;
import com.rtc.amethystTools.listener.AmethystToolHoldSound;
import com.rtc.amethystTools.listener.AnvilListener;
import com.rtc.amethystTools.listener.GrindstoneListener;
import com.rtc.amethystTools.menu.*;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class AmethystTools extends JavaPlugin {

    private static AmethystTools instance;

    public static AmethystTools getInstance() {
        return instance;
    }

    public static NamespacedKey KEY_TOOL;
    public static NamespacedKey KEY_TIER;
    public static NamespacedKey KEY_TYPE;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        KEY_TOOL = new NamespacedKey(this, "amethyst_tool");
        KEY_TIER = new NamespacedKey(this, "amethyst_tier");
        KEY_TYPE = new NamespacedKey(this, "amethyst_type");

        getServer().getPluginManager().registerEvents(new AmethystToolMainMenu(this), this);

        getServer().getPluginManager().registerEvents(new AmethystToolNetheriteMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolDiamondMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolGoldenMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolIronMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolCopperMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolStoneMenu(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolWoodenMenu(this), this);

        getServer().getPluginManager().registerEvents(new AmethystToolHoldSound(this), this);
        getServer().getPluginManager().registerEvents(new AmethystToolBlockBreak(this), this);


        this.getServer().getCommandMap().register("amethysttools", new AmethystToolCommand(this));
        this.getServer().getCommandMap().register("amethysttoolsreload", new AmethystToolReloadCommand(this));

        getServer().getPluginManager().registerEvents(new AnvilListener(), this);
        getServer().getPluginManager().registerEvents(new GrindstoneListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
