
package com.byteworld.item;

import com.byteworld.block.BlockType;

public enum ItemType {
    AIR(0, "air", 0, null),
    GRASS_BLOCK(1, "grass_block", 64, BlockType.GRASS),
    DIRT(2, "dirt", 64, BlockType.DIRT),
    STONE(3, "stone", 64, BlockType.STONE),
    WOOD(4, "wood", 64, BlockType.WOOD),
    LEAVES(5, "leaves", 64, BlockType.LEAVES),
    WATER_BUCKET(6, "water_bucket", 1, null),
    SAND(7, "sand", 64, BlockType.SAND),
    GRAVEL(8, "gravel", 64, BlockType.GRAVEL),
    COAL(9, "coal", 64, null),
    IRON_INGOT(10, "iron_ingot", 64, null),
    DIAMOND(11, "diamond", 64, null),
    WOODEN_PLANK(12, "wooden_plank", 64, BlockType.PLANK),
    BRICK(13, "brick", 64, BlockType.BRICK),
    GLASS(14, "glass", 64, BlockType.GLASS),
    WOODEN_SWORD(15, "wooden_sword", 1, null),
    WOODEN_PICKAXE(16, "wooden_pickaxe", 1, null),
    STONE_SWORD(17, "stone_sword", 1, null),
    STONE_PICKAXE(18, "stone_pickaxe", 1, null),
    IRON_SWORD(19, "iron_sword", 1, null),
    IRON_PICKAXE(20, "iron_pickaxe", 1, null);

    private final int id;
    private final String name;
    private final int maxStackSize;
    private final BlockType placeableBlock;

    ItemType(int id, String name, int maxStackSize, BlockType placeableBlock) {
        this.id = id;
        this.name = name;
        this.maxStackSize = maxStackSize;
        this.placeableBlock = placeableBlock;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public BlockType getPlaceableBlock() {
        return placeableBlock;
    }

    public static ItemType fromId(int id) {
        for (ItemType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return AIR;
    }

    public static ItemType fromBlockType(BlockType blockType) {
        for (ItemType type : values()) {
            if (type.placeableBlock == blockType) {
                return type;
            }
        }
        return AIR;
    }
}
