package com.sizphoto.shiningproject.game;

import com.sizphoto.shiningproject.engine.GameItem;
import com.sizphoto.shiningproject.engine.MouseInput;
import com.sizphoto.shiningproject.engine.Window;
import com.sizphoto.shiningproject.engine.graph.Camera;
import com.sizphoto.shiningproject.engine.graph.Mesh;
import com.sizphoto.shiningproject.engine.graph.OBJLoader;
import com.sizphoto.shiningproject.engine.graph.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import static org.lwjgl.glfw.GLFW.*;

@Component
@Primary
public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private GameItem[] gameItems;

    private static final float CAMERA_POS_STEP = 0.05f;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
//        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
//        Texture texture = new Texture("/textures/grassblock.png");
//        mesh.setTexture(texture);
//        GameItem gameItem = new GameItem(mesh);
//        gameItem.setScale(0.5f);
//        gameItem.setPosition(0, 0, -2);
        Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        GameItem gameItem = new GameItem(mesh);
        gameItem.setScale(0.5f);
        gameItem.setPosition(0, -0.5f, -2);
        gameItems = new GameItem[]{gameItem};
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        this.cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems);
    }

    @Override
    public void cleanup() {
        this.renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}
