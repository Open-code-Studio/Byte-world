
package com.byteworld.game;

import com.byteworld.engine.Window;
import com.byteworld.engine.renderer.Renderer;
import com.byteworld.engine.input.Input;
import com.byteworld.world.World;
import com.byteworld.entity.Player;
import com.byteworld.item.Inventory;
import com.byteworld.item.ItemStack;
import com.byteworld.item.ItemType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);
    
    private Window window;
    private Renderer renderer;
    private Input input;
    private World world;
    private Player player;
    private Inventory inventory;
    private GameLogic gameLogic;
    
    private boolean running = false;
    private long lastTime;

    public static void main(String[] args) {
        new Game().start();
    }

    public void start() {
        logger.info("Starting ByteWorld Game...");
        
        init();
        
        running = true;
        lastTime = System.nanoTime();
        
        while (running) {
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastTime) / 1_000_000_000.0f;
            lastTime = currentTime;
            
            update(deltaTime);
            render();
            
            if (window.shouldClose()) {
                running = false;
            }
        }
        
        cleanup();
        logger.info("Game shutdown complete.");
    }

    private void init() {
        logger.info("Initializing game components...");
        
        window = new Window("ByteWorld", 1280, 720);
        input = new Input(window);
        renderer = new Renderer(window);
        
        world = new World();
        player = new Player(input);
        inventory = new Inventory();
        
        inventory.addItem(new ItemStack(ItemType.GRASS_BLOCK, 64));
        inventory.addItem(new ItemStack(ItemType.DIRT, 64));
        inventory.addItem(new ItemStack(ItemType.STONE, 64));
        inventory.addItem(new ItemStack(ItemType.WOOD, 64));
        
        gameLogic = new GameLogic(input, player, world, inventory);
        
        logger.info("Game initialization complete.");
    }

    private void update(float deltaTime) {
        window.update();
        input.update();
        player.update(deltaTime, world);
        world.update(deltaTime);
        gameLogic.update(deltaTime);
    }

    private void render() {
        renderer.render(world, player);
        window.swapBuffers();
    }

    private void cleanup() {
        logger.info("Cleaning up resources...");
        
        renderer.cleanup();
        window.cleanup();
        world.cleanup();
        
        logger.info("Cleanup complete.");
    }
}
