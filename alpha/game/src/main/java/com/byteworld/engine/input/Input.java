
package com.byteworld.engine.input;

import com.byteworld.engine.Window;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class Input {
    private final Window window;
    private final Map<Integer, Boolean> keys = new HashMap<>();
    private final Map<Integer, Boolean> mouseButtons = new HashMap<>();
    
    private double mouseX, mouseY;
    private double prevMouseX, prevMouseY;
    private double deltaX, deltaY;
    
    private boolean cursorLocked = false;

    public Input(Window window) {
        this.window = window;
        
        GLFW.glfwSetKeyCallback(window.getWindowHandle(), (handle, key, scancode, action, mods) -> {
            keys.put(key, action != GLFW.GLFW_RELEASE);
        });
        
        GLFW.glfwSetMouseButtonCallback(window.getWindowHandle(), (handle, button, action, mods) -> {
            mouseButtons.put(button, action != GLFW.GLFW_RELEASE);
        });
        
        GLFW.glfwSetCursorPosCallback(window.getWindowHandle(), (handle, xpos, ypos) -> {
            prevMouseX = mouseX;
            prevMouseY = mouseY;
            mouseX = xpos;
            mouseY = ypos;
            
            if (cursorLocked) {
                deltaX += xpos - window.getWidth() / 2.0;
                deltaY += ypos - window.getHeight() / 2.0;
                GLFW.glfwSetCursorPos(handle, window.getWidth() / 2.0, window.getHeight() / 2.0);
                mouseX = window.getWidth() / 2.0;
                mouseY = window.getHeight() / 2.0;
            }
        });
    }

    public void update() {
        deltaX = 0;
        deltaY = 0;
    }

    public boolean isKeyPressed(int key) {
        return keys.getOrDefault(key, false);
    }

    public boolean isMouseButtonPressed(int button) {
        return mouseButtons.getOrDefault(button, false);
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public void setCursorLocked(boolean locked) {
        cursorLocked = locked;
        if (locked) {
            GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            GLFW.glfwSetCursorPos(window.getWindowHandle(), window.getWidth() / 2.0, window.getHeight() / 2.0);
            mouseX = window.getWidth() / 2.0;
            mouseY = window.getHeight() / 2.0;
        } else {
            GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }
    }

    public boolean isCursorLocked() {
        return cursorLocked;
    }
}
