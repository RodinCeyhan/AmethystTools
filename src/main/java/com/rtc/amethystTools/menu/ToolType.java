package com.rtc.amethystTools.menu;

public enum ToolType {
    PICKAXE("pickaxe", 12),
    SHOVEL("shovel", 13),
    AXE("axe", 14);

    public final String id;
    public final int slot;

    ToolType(String id, int slot) {
        this.id = id;
        this.slot = slot;
    }

    public String nameKey() {
        return "item.item-" + id;
    }

    public static ToolType fromId(String id) {
        if (id == null) return null;
        for (ToolType type : values()) {
            if (type.id.equalsIgnoreCase(id)) return type;
        }
        return null;
    }
}