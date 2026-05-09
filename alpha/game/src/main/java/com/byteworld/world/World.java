
package com.byteworld.world;

import com.byteworld.block.BlockState;
import com.byteworld.block.BlockType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class World {
    private static final Logger logger = LoggerFactory.getLogger(World.class);
    
    private static final int RENDER_DISTANCE = 8;
    
    private final Map<Long, Chunk> chunks = new HashMap<>();
    private final WorldGenerator generator;
    private long seed;

    public World() {
        this.seed = System.currentTimeMillis();
        this.generator = new WorldGenerator(seed);
        logger.info("World created with seed: {}", seed);
    }

    public Chunk getChunk(int x, int z) {
        long key = getChunkKey(x, z);
        
        if (!chunks.containsKey(key)) {
            Chunk chunk = new Chunk(x, z);
            generator.generateChunk(chunk);
            chunks.put(key, chunk);
            logger.debug("Generated chunk at ({}, {})", x, z);
        }
        
        return chunks.get(key);
    }

    public BlockState getBlock(int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        int blockX = x & 15;
        int blockZ = z & 15;
        
        Chunk chunk = getChunk(chunkX, chunkZ);
        return chunk.getBlock(blockX, y, blockZ);
    }

    public void setBlock(int x, int y, int z, BlockType type) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        int blockX = x & 15;
        int blockZ = z & 15;
        
        Chunk chunk = getChunk(chunkX, chunkZ);
        chunk.setBlock(blockX, y, z, type);
    }

    public void update(float deltaTime) {
    }

    public void cleanup() {
        chunks.clear();
    }

    public Map<Long, Chunk> getChunks() {
        return chunks;
    }

    public int getRenderDistance() {
        return RENDER_DISTANCE;
    }

    public long getSeed() {
        return seed;
    }

    private long getChunkKey(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }
}
