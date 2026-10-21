package com.rtc.amethystTools.listener;

import com.rtc.amethystTools.utils.ToolUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class AnvilListener implements Listener {

    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        ItemStack left = e.getInventory().getItem(0);
        ItemStack right = e.getInventory().getItem(1);
        ItemStack result = e.getResult();
        if (result == null || !ToolUtils.isAmethystTool(left)) return;

        if (!ToolUtils.isRepairAllowed() && ToolUtils.isRepair(left, result)) {
            e.setResult(null);
            return;
        }

        Component originalName = left.getItemMeta().displayName();
        Component resultName = result.getItemMeta().displayName();

        if (Objects.equals(originalName, resultName)) {
            e.setResult(result);
            return;
        }
        String renameText = e.getView().getRenameText();
        boolean realRename = ToolUtils.isRenameAllowed() && renameText != null && !renameText.isBlank();

        if (realRename) {
            result.editMeta(ToolUtils::markAsRenamed);
            e.setResult(result);
            return;
        }

        boolean hasSecondItem = right != null && !right.getType().isAir();

        if (!hasSecondItem) {
            e.setResult(null);
            return;
        }

        result.editMeta(meta -> meta.displayName(originalName));
        e.setResult(result);
    }
}