
package com.byteworld.engine.renderer;

import com.byteworld.block.BlockState;
import com.byteworld.block.BlockType;
import com.byteworld.entity.Player;
import com.byteworld.world.Chunk;
import com.byteworld.world.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ChunkRenderer {
    private static final float TEXTURE_SIZE = 16.0f;
    
    private int vaoId;
    private int vboId;
    private int textureId;
    
    private final List<Float> vertexData = new ArrayList<>();

    public void init(Shader shader) {
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
        
        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 8 * Float.BYTES, 0);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 8 * Float.BYTES, 5 * Float.BYTES);
        
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        
        loadTextures(shader);
    }

    private void loadTextures(Shader shader) {
        textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        
        int textureSize = 256;
        int[] pixels = new int[textureSize * textureSize * 4];
        
        int[][] colors = {
            {255, 255, 255, 0},
            {34, 139, 34, 255},
            {139, 90, 43, 255},
            {128, 128, 128, 255},
            {139, 90, 43, 255},
            {34, 139, 34, 255},
            {0, 100, 255, 128},
            {244, 164, 96, 255},
            {169, 169, 169, 255},
            {50, 50, 50, 255},
            {211, 211, 211, 255},
            {0, 255, 255, 255},
            {139, 90, 43, 255},
            {178, 34, 34, 255},
            {200, 200, 255, 200}
        };
        
        for (int blockId = 0; blockId < colors.length; blockId++) {
            int tx = (blockId % 16) * 16;
            int ty = (blockId / 16) * 16;
            
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    int px = tx + x;
                    int py = textureSize - 1 - (ty + y);
                    int[] color = colors[blockId];
                    
                    for (int i = 0; i < 4; i++) {
                        pixels[(py * textureSize + px) * 4 + i] = color[i];
                    }
                }
            }
        }
        
        FloatBuffer buffer = BufferUtils.createFloatBuffer(pixels.length);
        for (int pixel : pixels) {
            buffer.put(pixel / 255.0f);
        }
        buffer.flip();
        
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, textureSize, textureSize, 0, GL11.GL_RGBA, GL11.GL_FLOAT, buffer);
        
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        
        shader.bind();
        GL20.glUniform1i(GL20.glGetUniformLocation(shader.getProgramId(), "textureSampler"), 0);
        shader.unbind();
    }

    public void render(World world, Player player) {
        vertexData.clear();
        
        int playerChunkX = (int) Math.floor(player.getPosition().x / Chunk.CHUNK_SIZE);
        int playerChunkZ = (int) Math.floor(player.getPosition().z / Chunk.CHUNK_SIZE);
        
        int renderDistance = world.getRenderDistance();
        
        for (int dx = -renderDistance; dx <= renderDistance; dx++) {
            for (int dz = -renderDistance; dz <= renderDistance; dz++) {
                Chunk chunk = world.getChunk(playerChunkX + dx, playerChunkZ + dz);
                if (chunk != null) {
                    buildChunkMesh(chunk);
                }
            }
        }
        
        uploadAndRender();
    }

    private void buildChunkMesh(Chunk chunk) {
        BlockState[][][] blocks = chunk.getBlocks();
        int chunkX = chunk.getX() * Chunk.CHUNK_SIZE;
        int chunkZ = chunk.getZ() * Chunk.CHUNK_SIZE;
        
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                    BlockState state = blocks[x][y][z];
                    if (!state.isAir()) {
                        addBlockMesh(x + chunkX, y, z + chunkZ, state.getType());
                    }
                }
            }
        }
    }

    private void addBlockMesh(int x, int y, int z, BlockType type) {
        float texX = (type.getTextureIndex() % 16) / TEXTURE_SIZE;
        float texY = (type.getTextureIndex() / 16) / TEXTURE_SIZE;
        float texSize = 1.0f / TEXTURE_SIZE;
        
        // Front face
        addFace(x, y, z, 0, 0, -1, texX, texY, texSize);
        // Back face
        addFace(x, y, z, 0, 0, 1, texX, texY, texSize);
        // Left face
        addFace(x, y, z, -1, 0, 0, texX, texY, texSize);
        // Right face
        addFace(x, y, z, 1, 0, 0, texX, texY, texSize);
        // Top face
        addFace(x, y, z, 0, 1, 0, texX, texY, texSize);
        // Bottom face
        addFace(x, y, z, 0, -1, 0, texX, texY, texSize);
    }

    private void addFace(int x, int y, int z, int nx, int ny, int nz, float texX, float texY, float texSize) {
        float[] positions = {
            x, y, z,
            x + 1, y, z,
            x + 1, y + 1, z,
            x, y + 1, z
        };
        
        float[] texCoords = {
            texX, texY + texSize,
            texX + texSize, texY + texSize,
            texX + texSize, texY,
            texX, texY
        };
        
        int[][] indices = {
            {0, 1, 2}, {2, 3, 0}
        };
        
        for (int[] tri : indices) {
            for (int i : tri) {
                vertexData.add(positions[i * 3]);
                vertexData.add(positions[i * 3 + 1]);
                vertexData.add(positions[i * 3 + 2]);
                vertexData.add(texCoords[i * 2]);
                vertexData.add(texCoords[i * 2 + 1]);
                vertexData.add((float) nx);
                vertexData.add((float) ny);
                vertexData.add((float) nz);
            }
        }
    }

    private void uploadAndRender() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertexData.size());
        for (float f : vertexData) {
            buffer.put(f);
        }
        buffer.flip();
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STREAM_DRAW);
        
        GL30.glBindVertexArray(vaoId);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexData.size() / 8);
    }

    public void cleanup() {
        GL30.glDeleteVertexArrays(vaoId);
        GL15.glDeleteBuffers(vboId);
        GL11.glDeleteTextures(textureId);
    }
}
