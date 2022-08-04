package net.plazmix.savechristmas.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Panes {
    WHITE(0),
    ORANGE(1),
    LIGHT_PURPLE(2),
    LIGHT_BLUE(3),
    YELLOW(4),
    LIGHT_GREEN(5),
    PINK(6),
    DARK_GRAY(7),
    LIGHT_GRAY(8),
    AQUA(9),
    PURPLE(10),
    BLUE(11),
    BROWN(12),
    GREEN(13),
    RED(14),
    BLACK(15);

    private short data;

    private Panes(int data) {
        this.data = ((short) data);
    }

    public int getData() {
        return this.data;
    }

}
