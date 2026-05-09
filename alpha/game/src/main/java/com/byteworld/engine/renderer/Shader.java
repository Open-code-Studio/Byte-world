
package com.byteworld.engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

public class Shader {
    private static final Logger logger = LoggerFactory.getLogger(Shader.class);
    
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    
    private final FloatBuffer matrixBuffer = org.lwjgl.BufferUtils.createFloatBuffer(16);

    public void createVertexShader(String shaderCode) {
        vertexShaderId = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) {
        fragmentShaderId = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    private int createShader(String shaderPath, int shaderType) {
        int shaderId = GL20.glCreateShader(shaderType);
        if (shaderId == 0) {
            logger.error("Shader creation failed for type: {}", shaderType);
            throw new RuntimeException("Shader creation failed");
        }
        
        String shaderCode = readFile(shaderPath);
        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);
        
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            logger.error("Shader compilation error: {}", GL20.glGetShaderInfoLog(shaderId));
            throw new RuntimeException("Shader compilation failed");
        }
        
        return shaderId;
    }

    private String readFile(String path) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (Exception e) {
            logger.error("Failed to read shader file: {}", path, e);
            throw new RuntimeException("Failed to read shader file");
        }
        return builder.toString();
    }

    public void link() {
        programId = GL20.glCreateProgram();
        if (programId == 0) {
            logger.error("Program creation failed");
            throw new RuntimeException("Program creation failed");
        }
        
        GL20.glAttachShader(programId, vertexShaderId);
        GL20.glAttachShader(programId, fragmentShaderId);
        GL20.glLinkProgram(programId);
        
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            logger.error("Program linking error: {}", GL20.glGetProgramInfoLog(programId));
            throw new RuntimeException("Program linking failed");
        }
        
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);
    }

    public void bind() {
        GL20.glUseProgram(programId);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void setUniform(String name, Matrix4f value) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            value.get(matrixBuffer);
            GL20.glUniformMatrix4fv(location, false, matrixBuffer);
        }
    }

    public void setUniform(String name, Vector3f value) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            GL20.glUniform3f(location, value.x, value.y, value.z);
        }
    }

    public void setUniform(String name, float value) {
        int location = GL20.glGetUniformLocation(programId, name);
        if (location != -1) {
            GL20.glUniform1f(location, value);
        }
    }

    public void cleanup() {
        unbind();
        GL20.glDeleteProgram(programId);
    }

    public int getProgramId() {
        return programId;
    }
}
