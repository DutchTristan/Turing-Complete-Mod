package name.turingcomplete.blocks;

import net.minecraft.util.StringIdentifiable;

public enum BLOCK_PART implements StringIdentifiable {
    // A bit silly. Left/right from the cardinal direction of the **output**.
    LEFT("left"),
    MIDDLE("middle"),
    RIGHT("right");

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

