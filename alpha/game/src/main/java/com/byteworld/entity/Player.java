
package com.byteworld.entity;

import com.byteworld.engine.input.Input;
import com.byteworld.world.World;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

public class Player {
    private static final float MOVEMENT_SPEED = 5.0f;
    private static final float SENSITIVITY = 0.002f;
    private static final float GRAVITY = -9.8f;
    private static final float JUMP_FORCE = 3.0f;
    private static final float PLAYER_HEIGHT = 1.8f;
    private static final float PLAYER_WIDTH = 0.6f;
    
    private final Input input;
    private final Vector3d position = new Vector3d(0, 100, 0);
    private final Vector3d front = new Vector3d(0, 0, -1);
    private final Vector3d up = new Vector3d(0, 1, 0);
    
    private float pitch = 0.0f;
    private float yaw = -90.0f;
    
    private double velocityY = 0;
    private boolean isOnGround = false;

    public Player(Input input) {
        this.input = input;
    }

    public void update(float deltaTime, World world) {
        handleMouseInput();
        handleKeyboardInput(deltaTime);
        applyGravity(deltaTime);
        checkCollisions(world);
    }

    private void handleMouseInput() {
        if (input.isCursorLocked()) {
            double deltaX = input.getDeltaX() * SENSITIVITY;
            double deltaY = input.getDeltaY() * SENSITIVITY;
            
            yaw += deltaX;
            pitch -= deltaY;
            
            pitch = Math.max(-89.0f, Math.min(89.0f, pitch));
            
            updateFrontVector();
        }
    }

    private void updateFrontVector() {
        float pitchRad = (float) Math.toRadians(pitch);
        float yawRad = (float) Math.toRadians(yaw);
        
        front.x = Math.cos(yawRad) * Math.cos(pitchRad);
        front.y = Math.sin(pitchRad);
        front.z = Math.sin(yawRad) * Math.cos(pitchRad);
        front.normalize();
    }

    private void handleKeyboardInput(float deltaTime) {
        float speed = MOVEMENT_SPEED * deltaTime;
        
        Vector3d right = new Vector3d();
        front.cross(up, right).normalize();
        
        if (input.isKeyPressed(GLFW.GLFW_KEY_W)) {
            position.x += front.x * speed;
            position.z += front.z * speed;
        }
        if (input.isKeyPressed(GLFW.GLFW_KEY_S)) {
            position.x -= front.x * speed;
            position.z -= front.z * speed;
        }
        if (input.isKeyPressed(GLFW.GLFW_KEY_A)) {
            position.x -= right.x * speed;
            position.z -= right.z * speed;
        }
        if (input.isKeyPressed(GLFW.GLFW_KEY_D)) {
            position.x += right.x * speed;
            position.z += right.z * speed;
        }
        if (input.isKeyPressed(GLFW.GLFW_KEY_SPACE) && isOnGround) {
            velocityY = JUMP_FORCE;
            isOnGround = false;
        }
        if (input.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            speed *= 0.5f;
        }
    }

    private void applyGravity(float deltaTime) {
        velocityY += GRAVITY * deltaTime;
        position.y += velocityY * deltaTime;
    }

    private void checkCollisions(World world) {
        isOnGround = false;
        
        double minX = position.x - PLAYER_WIDTH / 2;
        double maxX = position.x + PLAYER_WIDTH / 2;
        double minY = position.y;
        double maxY = position.y + PLAYER_HEIGHT;
        double minZ = position.z - PLAYER_WIDTH / 2;
        double maxZ = position.z + PLAYER_WIDTH / 2;
        
        int startX = (int) Math.floor(minX);
        int endX = (int) Math.ceil(maxX);
        int startY = (int) Math.floor(minY);
        int endY = (int) Math.ceil(maxY);
        int startZ = (int) Math.floor(minZ);
        int endZ = (int) Math.ceil(maxZ);
        
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    if (world.getBlock(x, y, z).isSolid()) {
                        double blockMinX = x;
                        double blockMaxX = x + 1;
                        double blockMinY = y;
                        double blockMaxY = y + 1;
                        double blockMinZ = z;
                        double blockMaxZ = z + 1;
                        
                        Vector3d mtv = getCollisionMTV(
                            minX, maxX, minY, maxY, minZ, maxZ,
                            blockMinX, blockMaxX, blockMinY, blockMaxY, blockMinZ, blockMaxZ
                        );
                        
                        if (mtv != null) {
                            position.x += mtv.x;
                            position.y += mtv.y;
                            position.z += mtv.z;
                            
                            if (mtv.y > 0) {
                                isOnGround = true;
                                velocityY = 0;
                            } else if (mtv.y < 0) {
                                velocityY = 0;
                            }
                        }
                    }
                }
            }
        }
        
        if (position.y < 0) {
            position.y = 100;
            velocityY = 0;
        }
    }

    private Vector3d getCollisionMTV(
        double aMinX, double aMaxX, double aMinY, double aMaxY, double aMinZ, double aMaxZ,
        double bMinX, double bMaxX, double bMinY, double bMaxY, double bMinZ, double bMaxZ
    ) {
        double dx = overlap(aMinX, aMaxX, bMinX, bMaxX);
        double dy = overlap(aMinY, aMaxY, bMinY, bMaxY);
        double dz = overlap(aMinZ, aMaxZ, bMinZ, bMaxZ);
        
        if (dx == 0 || dy == 0 || dz == 0) {
            return null;
        }
        
        double minOverlap = Math.min(Math.min(dx, dy), dz);
        
        if (minOverlap == dx) {
            return new Vector3d(aMinX < bMinX ? -dx : dx, 0, 0);
        } else if (minOverlap == dy) {
            return new Vector3d(0, aMinY < bMinY ? -dy : dy, 0);
        } else {
            return new Vector3d(0, 0, aMinZ < bMinZ ? -dz : dz);
        }
    }

    private double overlap(double aMin, double aMax, double bMin, double bMax) {
        double overlap = Math.min(aMax, bMax) - Math.max(aMin, bMin);
        return overlap > 0 ? overlap : 0;
    }

    public Vector3d getPosition() {
        return position;
    }

    public Vector3d getFront() {
        return front;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }
}
