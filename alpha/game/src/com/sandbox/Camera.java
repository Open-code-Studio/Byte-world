package com.sandbox;

public class Camera {
    public double x, y, z;
    public double yaw;
    public double pitch;

    private static final double MOVE_SPEED = 3.0;
    private static final double MOUSE_SENSITIVITY = 0.0005;
    private static final double EYE_HEIGHT = 1.3;

    public Camera() {
        this.x = 1.5;
        this.y = 2.5;
        this.z = 5.0;
        this.yaw = Math.PI;
        this.pitch = -0.15;
    }

    public void moveForward(double dt) {
        x += Math.sin(yaw) * MOVE_SPEED * dt;
        z += Math.cos(yaw) * MOVE_SPEED * dt;
    }

    public void moveBackward(double dt) {
        x -= Math.sin(yaw) * MOVE_SPEED * dt;
        z -= Math.cos(yaw) * MOVE_SPEED * dt;
    }

    public void moveLeft(double dt) {
        x -= Math.cos(yaw) * MOVE_SPEED * dt;
        z += Math.sin(yaw) * MOVE_SPEED * dt;
    }

    public void moveRight(double dt) {
        x += Math.cos(yaw) * MOVE_SPEED * dt;
        z -= Math.sin(yaw) * MOVE_SPEED * dt;
    }

    public void moveUp(double dt) {
        y += MOVE_SPEED * dt;
    }

    public void moveDown(double dt) {
        y -= MOVE_SPEED * dt;
    }

    public void rotateYaw(double delta) {
        yaw += delta * MOUSE_SENSITIVITY;
    }

    public void rotatePitch(double delta) {
        pitch += delta * MOUSE_SENSITIVITY;
        pitch = Math.max(-Math.PI / 2.2, Math.min(Math.PI / 2.2, pitch));
    }
}