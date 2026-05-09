
package com.byteworld.block;

public class BlockState {
    private BlockType type;
    private int metadata;

    public BlockState(BlockType type) {
        this(type, 0);
    }

    public BlockState(BlockType type, int metadata) {
        this.type = type;
        this.metadata = metadata;
    }

    public BlockType getType() {
        return type;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public int getMetadata() {
        return metadata;
    }

    public void setMetadata(int metadata) {
        this.metadata = metadata;
    }

    public boolean isSolid() {
        return type.isSolid();
    }

    public boolean isAir() {
        return type == BlockType.AIR;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BlockState other = (BlockState) obj;
        return type == other.type && metadata == other.metadata;
    }

    @Override
    public int hashCode() {
        return type.getId() * 16 + metadata;
    }
}
