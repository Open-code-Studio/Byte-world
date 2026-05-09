
package com.byteworld.world;

import com.byteworld.block.BlockType;

import java.util.Random;

public class WorldGenerator {
    private static final int SEA_LEVEL = 64;
    private static final int TREE_HEIGHT = 5;
    
    private final Random random;
    private long seed;

    public WorldGenerator(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    public void generateChunk(Chunk chunk) {
        int chunkX = chunk.getX() * Chunk.CHUNK_SIZE;
        int chunkZ = chunk.getZ() * Chunk.CHUNK_SIZE;

        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                int worldX = chunkX + x;
                int worldZ = chunkZ + z;
                
                int height = generateHeight(worldX, worldZ);
                
                for (int y = 0; y <= height; y++) {
                    BlockType blockType;
                    if (y == height) {
                        blockType = BlockType.GRASS;
                    } else if (y > height - 3) {
                        blockType = BlockType.DIRT;
                    } else {
                        blockType = BlockType.STONE;
                    }
                    chunk.setBlock(x, y, z, blockType);
                }
                
                for (int y = height + 1; y <= SEA_LEVEL; y++) {
                    chunk.setBlock(x, y, z, BlockType.WATER);
                }
            }
        }
        
        generateTrees(chunk, chunkX, chunkZ);
        generateOres(chunk);
    }

    private int generateHeight(int x, int z) {
        float noise1 = noise(x * 0.01f, z * 0.01f) * 10;
        float noise2 = noise(x * 0.02f, z * 0.02f) * 5;
        float noise3 = noise(x * 0.05f, z * 0.05f) * 2;
        
        return SEA_LEVEL + (int) (noise1 + noise2 + noise3);
    }

    private float noise(float x, float y) {
        return (float) Math.sin(x) * (float) Math.cos(y) * 0.5f + 0.5f;
    }

    private void generateTrees(Chunk chunk, int chunkX, int chunkZ) {
        Random chunkRandom = new Random(seed + chunkX + chunkZ * 782341);
        
        for (int i = 0; i < 5; i++) {
            int x = chunkRandom.nextInt(Chunk.CHUNK_SIZE);
            int z = chunkRandom.nextInt(Chunk.CHUNK_SIZE);
            
            int groundHeight = getGroundHeight(chunk, x, z);
            if (groundHeight > SEA_LEVEL + 5 && chunkRandom.nextFloat() < 0.05f) {
                generateTree(chunk, x, groundHeight, z);
            }
        }
    }

    private int getGroundHeight(Chunk chunk, int x, int z) {
        for (int y = Chunk.CHUNK_HEIGHT - 1; y >= 0; y--) {
            if (chunk.getBlock(x, y, z).isSolid()) {
                return y + 1;
            }
        }
        return 0;
    }

    private void generateTree(Chunk chunk, int x, int y, int z) {
        for (int dy = 0; dy < TREE_HEIGHT; dy++) {
            chunk.setBlock(x, y + dy, z, BlockType.WOOD);
        }
        
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 2; dy++) {
                    if (Math.abs(dx) + Math.abs(dz) + Math.abs(dy) <= 3) {
                        int treeX = x + dx;
                        int treeY = y + TREE_HEIGHT - 1 + dy;
                        int treeZ = z + dz;
                        if (treeX >= 0 && treeX < Chunk.CHUNK_SIZE && treeZ >= 0 && treeZ < Chunk.CHUNK_SIZE) {
                            if (treeY < Chunk.CHUNK_HEIGHT && chunk.getBlock(treeX, treeY, treeZ).isAir()) {
                                chunk.setBlock(treeX, treeY, treeZ, BlockType.LEAVES);
                            }
                        }
                    }
                }
            }
        }
    }

    private void generateOres(Chunk chunk) {
        Random oreRandom = new Random(seed + chunk.getX() * 34567 + chunk.getZ() * 98765);
        
        generateOre(chunk, oreRandom, BlockType.COAL_ORE, 10, 16, 0, 128);
        generateOre(chunk, oreRandom, BlockType.IRON_ORE, 8, 8, 0, 64);
        generateOre(chunk, oreRandom, BlockType.DIAMOND_ORE, 4, 1, 0, 32);
    }

    private void generateOre(Chunk chunk, Random random, BlockType ore, int veinSize, int count, int minY, int maxY) {
        for (int i = 0; i < count; i++) {
            int x = random.nextInt(Chunk.CHUNK_SIZE);
            int y = random.nextInt(maxY - minY) + minY;
            int z = random.nextInt(Chunk.CHUNK_SIZE);
            
            if (chunk.getBlock(x, y, z).getType() == BlockType.STONE) {
                for (int j = 0; j < veinSize; j++) {
                    int dx = random.nextInt(3) - 1;
                    int dy = random.nextInt(3) - 1;
                    int dz = random.nextInt(3) - 1;
                    
                    int oreX = x + dx;
                    int oreY = y + dy;
                    int oreZ = z + dz;
                    
                    if (oreX >= 0 && oreX < Chunk.CHUNK_SIZE && oreY >= 0 && oreY < Chunk.CHUNK_HEIGHT 
                        && oreZ >= 0 && oreZ < Chunk.CHUNK_SIZE) {
                        if (chunk.getBlock(oreX, oreY, oreZ).getType() == BlockType.STONE) {
                            chunk.setBlock(oreX, oreY, oreZ, ore);
                        }
                    }
                }
            }
        }
    }
}
