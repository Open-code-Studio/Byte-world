
package com.byteworld.world;

import com.byteworld.block.BlockState;
import com.byteworld.block.BlockType;

public class Chunk {
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_HEIGHT = 256;
    
    private final BlockState[][][] blocks;
    private final int x, z;
    private boolean modified = false;

    public Chunk(int x, int z) {
        this.x = x;
        this.z = z;
        this.blocks = new BlockState[CHUNK_SIZE][CHUNK_HEIGHT][CHUNK_SIZE];
        initializeBlocks();
    }

    private void initializeBlocks() {
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    blocks[x][y][z] = new BlockState(BlockType.AIR);
                }
            }
        }
    }

    public BlockState getBlock(int x, int y, int z) {
        if (x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_SIZE) {
            return new BlockState(BlockType.AIR);
        }
        return blocks[x][y][z];
    }

    public void setBlock(int x, int y, int z, BlockType type) {
        if (x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_HEIGHT && z >= 0 && z < CHUNK_SIZE) {
            blocks[x][y][z].setType(type);
            modified = true;
        }
    }

    public void setBlock(int x, int y, int z, BlockState state) {
        if (x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_HEIGHT && z >= 0 && z < CHUNK_SIZE) {
            blocks[x][y][z] = state;
            modified = true;
        }
    }

    public BlockState[][][] getBlocks() {
        return blocks;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean hasSolidNeighbor(int x, int y, int z) {
        return hasSolidBlock(x - 1, y, z) || hasSolidBlock(x + 1, y, z)
            || hasSolidBlock(x, y - 1, z) || hasSolidBlock(x, y + 1, z)
            || hasSolidBlock(x, y, z - 1) || hasSolidBlock(x, y, z + 1);
    }

    private boolean hasSolidBlock(int x, int y, int z) {
        if (x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_HEIGHT || z < 0 || z >= CHUNK_SIZE) {
            return false;
        }
        return blocks[x][y][z].isSolid();
    }
}
