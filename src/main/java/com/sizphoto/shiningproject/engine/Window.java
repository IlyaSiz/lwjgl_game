package com.sizphoto.shiningproject.engine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@Component
public class Window {

    private static final Logger LOGGER = LoggerFactory.getLogger(Window.class);

    @Value("${window.title}")
    private String title;

    @Value("${window.width}")
    private int width;

    @Value("${window.height}")
    private int height;

    @Value("${window.vSync}")
    private boolean vSync;

    private long windowHandle;

    private boolean resized;

    public Window() {
        this.resized = false;
    }

    public void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            LOGGER.error("init() - Unable to initialize GLFW");
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            LOGGER.error("init() - Failed to create the GLFW window");
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResized(true);
        });

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        });

        // Get the resolution of the primary monitor
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center the window
        if (vidMode != null) {
            glfwSetWindowPos(
                    windowHandle,
                    (vidMode.width() - this.getWidth()) / 2,
                    (vidMode.height() - this.getHeight()) / 2
            );
        } else {
            LOGGER.error("init() - vidMode (the resolution of the primary monitor) is null");
            throw new NullPointerException("vidMode is null");
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        if (isvSync()) {
            // Enable v-sync
            glfwSwapInterval(1);
        }

        // Make the window visible
        glfwShowWindow(windowHandle);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        setClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        // uncomment this to show polygons without filling
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public void setClearColor(float r, float g, float b, float alpha) {
        glClearColor(r, g, b, alpha);
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public void update() {
        // swap the color buffers
        glfwSwapBuffers(windowHandle);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }

    public void release() {
        // Release window and window callbacks
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
    }
}
