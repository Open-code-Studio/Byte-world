
package com.byteworld.block;

public enum BlockType {
    AIR(0, "air", false, 0),
    GRASS(1, "grass", true, 0),
    DIRT(2, "dirt", true, 1),
    STONE(3, "stone", true, 2),
    WOOD(4, "wood", true, 3),
    LEAVES(5, "leaves", true, 4),
    WATER(6, "water", false, 5),
    SAND(7, "sand", true, 6),
    GRAVEL(8, "gravel", true, 7),
    COAL_ORE(9, "coal_ore", true, 8),
    IRON_ORE(10, "iron_ore", true, 9),
    DIAMOND_ORE(11, "diamond_ore", true, 10),
    PLANK(12, "plank", true, 11),
    BRICK(13, "brick", true, 12),
    GLASS(14, "glass", true, 13);

    private final int id;
    private final String name;
    private final boolean solid;
    private final int textureIndex;

    BlockType(int id, String name, boolean solid, int textureIndex) {
        this.id = id;
        this.name = name;
        this.solid = solid;
        this.textureIndex = textureIndex;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSolid() {
        return solid;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public static BlockType fromId(int id) {
        for (BlockType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return AIR;
    }
}
