package com.sizphoto.shiningproject.game;

import com.sizphoto.shiningproject.engine.GameItem;
import com.sizphoto.shiningproject.engine.Utils;
import com.sizphoto.shiningproject.engine.Window;
import com.sizphoto.shiningproject.engine.graph.Camera;
import com.sizphoto.shiningproject.engine.graph.Mesh;
import com.sizphoto.shiningproject.engine.graph.ShaderProgram;
import com.sizphoto.shiningproject.engine.graph.Transformation;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL15.*;

@Component
public class Renderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Renderer.class);

    // Field of View in Radians
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;

    private ShaderProgram shaderProgram;

    public Renderer() {
        transformation = new Transformation();
    }

    public void init(Window window) throws Exception {
        // Create shader
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vert"));
        this.shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.frag"));
        this.shaderProgram.link();

        // Create uniforms for modelView and projection matrices and texture
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("texture_sampler");
        // Create uniform for default colour and the flag that controls it
        shaderProgram.createUniform("colour");
        shaderProgram.createUniform("useColour");
    }

    private void clear() {
        // clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameItem[] gameItems) {
        this.clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shaderProgram.setUniform("texture_sampler", 0);
        // Render each gameItem
        for (GameItem gameItem : gameItems) {
            Mesh mesh = gameItem.getMesh();
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            shaderProgram.setUniform("colour", mesh.getColour());
            shaderProgram.setUniform("useColour", mesh.isTextured() ? 0 : 1);
            mesh.render();
        }

        this.shaderProgram.unbind();
    }

    public void cleanup() {
        if (this.shaderProgram != null) {
            this.shaderProgram.cleanup();
        }

        // Terminate GLFW and free the error callback
        glfwTerminate();
        GLFWErrorCallback errorCallback = glfwSetErrorCallback(null);
        if (errorCallback != null) {
            errorCallback.free();
        } else {
            LOGGER.error("cleanup() - errorCallback is null");
        }
    }
}
