package com.sandbox;

import java.awt.Color;

public class Block {
    public static final Block AIR = new Block("Air", Color.BLACK, Color.BLACK, Color.BLACK, false);
    public static final Block GRASS = new Block("Grass", new Color(85, 170, 85), new Color(139, 90, 43), new Color(105, 70, 30), true);
    public static final Block DIRT = new Block("Dirt", new Color(139, 90, 43), new Color(139, 90, 43), new Color(105, 70, 30), true);
    public static final Block STONE = new Block("Stone", new Color(128, 128, 128), new Color(136, 136, 136), new Color(112, 112, 112), true);
    public static final Block WOOD = new Block("Wood", new Color(101, 67, 33), new Color(120, 80, 40), new Color(85, 55, 25), true);
    public static final Block LEAVES = new Block("Leaves", new Color(34, 139, 34), new Color(29, 120, 29), new Color(24, 100, 24), true);
    public static final Block PLANKS = new Block("Planks", new Color(181, 138, 78), new Color(165, 125, 70), new Color(150, 112, 62), true);
    public static final Block SAND = new Block("Sand", new Color(224, 208, 160), new Color(214, 198, 150), new Color(204, 188, 140), true);
    public static final Block BRICK = new Block("Brick", new Color(200, 80, 60), new Color(180, 70, 50), new Color(160, 60, 45), true);

    public final String name;
    public final Color topColor;
    public final Color sideColor;
    public final Color bottomColor;
    public final boolean solid;

    private Block(String name, Color top, Color side, Color bottom, boolean solid) {
        this.name = name;
        this.topColor = top;
        this.sideColor = side;
        this.bottomColor = bottom;
        this.solid = solid;
    }
}