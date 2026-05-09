
package com.byteworld.engine.renderer;

import com.byteworld.engine.Window;
import com.byteworld.entity.Player;
import com.byteworld.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Renderer {
    private static final Logger logger = LoggerFactory.getLogger(Renderer.class);
    
    private final Window window;
    private final Shader shader;
    private final ChunkRenderer chunkRenderer;
    
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;

    public Renderer(Window window) {
        this.window = window;
        this.shader = new Shader();
        this.chunkRenderer = new ChunkRenderer();
        
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        
        init();
    }

    private void init() {
        logger.info("Initializing renderer...");
        
        GL11.glClearColor(0.529f, 0.808f, 0.922f, 1.0f);
        
        shader.createVertexShader("/shaders/vertex.glsl");
        shader.createFragmentShader("/shaders/fragment.glsl");
        shader.link();
        
        chunkRenderer.init(shader);
        
        logger.info("Renderer initialized successfully");
    }

    public void render(World world, Player player) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        
        shader.bind();
        
        updateProjectionMatrix();
        updateViewMatrix(player);
        
        shader.setUniform("projectionMatrix", projectionMatrix);
        shader.setUniform("viewMatrix", viewMatrix);
        
        Vector3f playerPos = player.getPosition();
        shader.setUniform("viewPos", playerPos);
        
        chunkRenderer.render(world, player);
        
        shader.unbind();
    }

    private void updateProjectionMatrix() {
        float aspectRatio = (float) window.getWidth() / window.getHeight();
        projectionMatrix.setPerspective((float) Math.toRadians(70.0f), aspectRatio, 0.1f, 1000.0f);
    }

    private void updateViewMatrix(Player player) {
        viewMatrix.setLookAt(
            new Vector3f((float) player.getPosition().x, (float) player.getPosition().y + 1.8f, (float) player.getPosition().z),
            new Vector3f(
                (float) (player.getPosition().x + player.getFront().x),
                (float) (player.getPosition().y + 1.8f + player.getFront().y),
                (float) (player.getPosition().z + player.getFront().z)
            ),
            new Vector3f(0.0f, 1.0f, 0.0f)
        );
    }

    public void cleanup() {
        logger.info("Cleaning up renderer...");
        shader.cleanup();
        chunkRenderer.cleanup();
    }
}
