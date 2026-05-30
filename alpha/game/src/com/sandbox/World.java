package com.sandbox;

import java.util.Random;

public class World {
    public static final int WIDTH = 4;
    public static final int HEIGHT = 8;
    public static final int DEPTH = 4;

    private final Block[][][] blocks;

    public World() {
        blocks = new Block[WIDTH][HEIGHT][DEPTH];
        generate();
    }

    private void generate() {
        Random rand = new Random(42);

        for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < DEPTH; z++) {
                blocks[x][0][z] = Block.STONE;

                Block surfaceBlock;
                int r = rand.nextInt(10);
                if (r < 6) {
                    surfaceBlock = Block.GRASS;
                } else if (r < 8) {
                    surfaceBlock = Block.DIRT;
                } else {
                    surfaceBlock = Block.SAND;
                }
                blocks[x][1][z] = surfaceBlock;

                if (x > 0 && x < WIDTH - 1 && z > 0 && z < DEPTH - 1) {
                    blocks[x][2][z] = Block.PLANKS;
                }

                if (x == 1 && z == 1) {
                    blocks[x][2][z] = Block.BRICK;
                }
            }
        }

        if (WIDTH > 2 && DEPTH > 2) {
            blocks[1][2][2] = Block.WOOD;
            blocks[1][3][2] = Block.WOOD;
            blocks[1][4][2] = Block.WOOD;
            blocks[0][4][2] = Block.LEAVES;
            blocks[1][4][1] = Block.LEAVES;
            blocks[1][4][3] = Block.LEAVES;
            blocks[2][4][2] = Block.LEAVES;
            blocks[1][5][2] = Block.LEAVES;
        }
    }

    public Block getBlock(int x, int y, int z) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT || z < 0 || z >= DEPTH) {
            return null;
        }
        return blocks[x][y][z];
    }

    public void setBlock(int x, int y, int z, Block block) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && z >= 0 && z < DEPTH) {
            blocks[x][y][z] = block;
        }
    }

    public boolean hasNeighbor(int x, int y, int z, int dx, int dy, int dz) {
        Block neighbor = getBlock(x + dx, y + dy, z + dz);
        return neighbor != null && neighbor.solid;
    }
}