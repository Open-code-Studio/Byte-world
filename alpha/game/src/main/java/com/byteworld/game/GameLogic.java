
package com.byteworld.game;

import com.byteworld.block.BlockType;
import com.byteworld.engine.input.Input;
import com.byteworld.entity.Player;
import com.byteworld.item.Inventory;
import com.byteworld.item.ItemStack;
import com.byteworld.item.ItemType;
import com.byteworld.world.World;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

public class GameLogic {
    private static final float REACH_DISTANCE = 5.0f;
    
    private final Input input;
    private final Player player;
    private final World world;
    private final Inventory inventory;
    
    private boolean leftClick = false;
    private boolean rightClick = false;

    public GameLogic(Input input, Player player, World world, Inventory inventory) {
        this.input = input;
        this.player = player;
        this.world = world;
        this.inventory = inventory;
    }

    public void update(float deltaTime) {
        checkInput();
        
        if (leftClick) {
            breakBlock();
        }
        
        if (rightClick) {
            placeBlock();
        }
    }

    private void checkInput() {
        leftClick = input.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT);
        rightClick = input.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
        
        if (input.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            input.setCursorLocked(!input.isCursorLocked());
        }
        
        for (int i = 0; i < 9; i++) {
            if (input.isKeyPressed(GLFW.GLFW_KEY_1 + i)) {
                inventory.setSelectedSlot(i);
            }
        }
    }

    private void breakBlock() {
        Vector3d hit = rayTrace(REACH_DISTANCE);
        if (hit != null) {
            int x = (int) Math.floor(hit.x);
            int y = (int) Math.floor(hit.y);
            int z = (int) Math.floor(hit.z);
            
            BlockType type = world.getBlock(x, y, z).getType();
            if (type != BlockType.AIR) {
                world.setBlock(x, y, z, BlockType.AIR);
                
                ItemType itemType = ItemType.fromBlockType(type);
                if (itemType != ItemType.AIR) {
                    inventory.addItem(new ItemStack(itemType));
                }
            }
        }
    }

    private void placeBlock() {
        ItemStack selected = inventory.getSelectedItem();
        if (selected.isEmpty()) {
            return;
        }
        
        BlockType blockType = selected.getType().getPlaceableBlock();
        if (blockType == null) {
            return;
        }
        
        Vector3d hit = rayTrace(REACH_DISTANCE);
        if (hit != null) {
            Vector3d normal = getHitNormal(hit);
            int x = (int) Math.floor(hit.x + normal.x);
            int y = (int) Math.floor(hit.y + normal.y);
            int z = (int) Math.floor(hit.z + normal.z);
            
            if (world.getBlock(x, y, z).isAir()) {
                world.setBlock(x, y, z, blockType);
                selected.decrement(1);
            }
        }
    }

    private Vector3d rayTrace(float maxDistance) {
        Vector3d pos = player.getPosition();
        Vector3d front = player.getFront();
        
        for (float t = 0; t < maxDistance; t += 0.1f) {
            int x = (int) Math.floor(pos.x + front.x * t);
            int y = (int) Math.floor(pos.y + front.y * t + 1.8f);
            int z = (int) Math.floor(pos.z + front.z * t);
            
            if (world.getBlock(x, y, z).isSolid()) {
                return new Vector3d(x, y, z);
            }
        }
        
        return null;
    }

    private Vector3d getHitNormal(Vector3d hit) {
        Vector3d pos = player.getPosition();
        Vector3d front = player.getFront();
        
        double dx = hit.x + 0.5 - pos.x;
        double dy = hit.y + 0.5 - pos.y - 1.8;
        double dz = hit.z + 0.5 - pos.z;
        
        double dotX = Math.abs(dx);
        double dotY = Math.abs(dy);
        double dotZ = Math.abs(dz);
        
        double max = Math.max(Math.max(dotX, dotY), dotZ);
        
        if (max == dotX) {
            return new Vector3d(dx > 0 ? 1 : -1, 0, 0);
        } else if (max == dotY) {
            return new Vector3d(0, dy > 0 ? 1 : -1, 0);
        } else {
            return new Vector3d(0, 0, dz > 0 ? 1 : -1);
        }
    }
}
