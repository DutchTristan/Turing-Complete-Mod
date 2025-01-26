package kosmek.turing_complete;

import net.minecraft.util.StringIdentifiable;

public enum BLOCK_PART implements StringIdentifiable {
    TOP("top"),
    MIDDLE("middle"),
    BOTTOM("bottom");

    private final String name;

    BLOCK_PART(String name) {
        this.name = name;

    }
    public String toString() {
            return this.name;
    }

    @Override
    public String asString() {
            return this.name;
    }
}

