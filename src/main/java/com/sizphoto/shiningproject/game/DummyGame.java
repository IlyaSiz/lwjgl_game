package com.sizphoto.shiningproject.game;

import com.sizphoto.shiningproject.engine.Renderer;
import com.sizphoto.shiningproject.engine.Window;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

@Component
@Primary
public class DummyGame implements IGameLogic {

    private int direction = 0;

    private float color = 0.0f;

    private Renderer renderer;

    public DummyGame(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void init() throws Exception {
        renderer.init();
    }

    @Override
    public void input(Window window) {
        if ( window.isKeyPressed(GLFW_KEY_UP) ) {
            direction = 1;
        } else if ( window.isKeyPressed(GLFW_KEY_DOWN) ) {
            direction = -1;
        } else {
            direction = 0;
        }
    }

    @Override
    public void update(float interval) {
        color += direction * 0.01f;
        if (color > 1) {
            color = 1.0f;
        } else if ( color < 0 ) {
            color = 0.0f;
        }
    }

    @Override
    public void render(Window window) {
        window.setClearColor(color, color, color, 0.0f);
        this.renderer.render(window);
    }

    @Override
    public void cleanup() {
        this.renderer.cleanup();
    }
}
