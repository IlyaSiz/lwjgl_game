package com.sizphoto.shiningproject.game;

import com.sizphoto.shiningproject.engine.*;
import com.sizphoto.shiningproject.engine.graph.*;
import com.sizphoto.shiningproject.engine.graph.lights.DirectionalLight;
import com.sizphoto.shiningproject.engine.items.GameItem;
import com.sizphoto.shiningproject.engine.items.SkyBox;
import com.sizphoto.shiningproject.engine.items.Terrain;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

@Component
@Primary
public class DummyGame implements IGameLogic {

  private static final float MOUSE_SENSITIVITY = 0.2f;

  private static final float CAMERA_POS_STEP = 0.05f;

  private final Vector3f cameraInc;

  private final Renderer renderer;

  private final Camera camera;

  private Scene scene;

  private Hud hud;

  private float lightAngle;

  @Autowired
  public DummyGame(
      final Renderer renderer,
      final Camera camera
  ) {
    this.renderer = renderer;
    this.camera = camera;
    cameraInc = new Vector3f(0, 0, 0);
    lightAngle = -90;
  }

  @Override
  public void init(final Window window) throws Exception {

    renderer.init(window);

    scene = new Scene();

    // Setup game items
//    final float reflectance = 1f;
//    Mesh mesh = ObjLoader.loadMesh("/models/bunny.obj");
//    final Material material = new Material(new Vector4f(0.7f, 0.7f, 0.7f, 0.0f), reflectance);
//    Mesh mesh = ObjLoader.loadMesh("/models/cube.obj");
//    final Texture texture = new Texture("/textures/grassblock.png");
//    Material material = new Material(texture, reflectance);
//    mesh.setMaterial(material);

//    float blockScale = 0.5f;
    float skyBoxScale = 50.0f;
    float terrainScale = 10;
    int terrainSize = 3;
    float minY = -0.1f;
    float maxY = 0.1f;
    int textInc = 40;
//    float extension = 2.0f;

    Terrain terrain = new Terrain(
        terrainSize,
        terrainScale,
        minY,
        maxY,
        "/textures/heightmap.png",
        "/textures/terrain.png",
        textInc);
    scene.setGameItems(terrain.getGameItems());

//    float startX = extension * (-skyBoxScale + blockScale);
//    float startZ = extension * (skyBoxScale - blockScale);
//    float startY = -1.0f;
//    float inc = blockScale * 2;
//
//    float posX = startX;
//    float posZ = startZ;
//    float incY;
//    int numRows = (int) (extension * skyBoxScale * 2 / inc);
//    int numCols = (int) (extension * skyBoxScale * 2 / inc);
//    GameItem[] gameItems = new GameItem[numRows * numCols];
//    for (int i = 0; i < numRows; i++) {
//      for (int j = 0; j < numCols; j++) {
//        GameItem gameItem = new GameItem(mesh);
//        gameItem.setScale(blockScale);
//        incY = Math.random() > 0.9f ? blockScale * 2 : 0f;
//        gameItem.setPosition(posX, startY + incY, posZ);
//        gameItems[i * numCols + j] = gameItem;
//
//        posX += inc;
//      }
//      posX = startX;
//      posZ -= inc;
//    }
//    scene.setGameItems(gameItems);

    // Setup SkyBox
    SkyBox skyBox = new SkyBox("/models/skybox.obj", "/textures/skybox.png");
    skyBox.setScale(skyBoxScale);
    scene.setSkyBox(skyBox);

    // Setup Lights
    setupLights();

    // Create HUD
    hud = new Hud("Demo");

//    camera.getPosition().x = 0.65f;
//    camera.getPosition().y = 1.15f;
//    camera.getPosition().z = 4.34f;
    camera.getPosition().x = 0.0f;
    camera.getPosition().z = 0.0f;
    camera.getPosition().y = -0.2f;
    camera.getRotation().x = 10.f;
  }

  private void setupLights() {
    SceneLight sceneLight = new SceneLight();

    // Ambient Light
    sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
    sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

    // Directional Light
    float lightIntensity = 1.0f;
    Vector3f lightPosition = new Vector3f(1, 1, 0);
    sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity));

    scene.setSceneLight(sceneLight);
  }

  @Override
  public void input(final Window window, final MouseInput mouseInput) {
    cameraInc.set(0, 0, 0);
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

//    SpotLight[] spotLightList = scene.getSceneLight().getSpotLightList();
//    final float lightPos = spotLightList[0].getPointLight().getPosition().z;
//    if (window.isKeyPressed(GLFW_KEY_N)) {
//      spotLightList[0].getPointLight().getPosition().z = lightPos + 0.1f;
//    } else if (window.isKeyPressed(GLFW_KEY_M)) {
//      spotLightList[0].getPointLight().getPosition().z = lightPos - 0.1f;
//    }
  }

  @Override
  public void update(float interval, MouseInput mouseInput) {
    // Update camera based on mouse
    if (mouseInput.isRightButtonPressed()) {
      Vector2f rotVec = mouseInput.getDisplVec();
      camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

      // Update HUD compass
      hud.rotateCompass(camera.getRotation().y);
    }

    // Update camera position
    camera.movePosition(
        cameraInc.x * CAMERA_POS_STEP,
        cameraInc.y * CAMERA_POS_STEP,
        cameraInc.z * CAMERA_POS_STEP
    );

    SceneLight sceneLight = scene.getSceneLight();

    // Update directional light direction, intensity and colour
    DirectionalLight directionalLight = sceneLight.getDirectionalLight();
    lightAngle += 0.5f;
    if (lightAngle > 90) {
      directionalLight.setIntensity(0);
      if (lightAngle >= 360) {
        lightAngle = -90;
      }
      sceneLight.getSkyBoxLight().set(0.3f, 0.3f, 0.3f);
    } else if (lightAngle <= -80 || lightAngle >= 80) {
      float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
      sceneLight.getSkyBoxLight().set(factor, factor, factor);
      directionalLight.setIntensity(factor);
      directionalLight.getColour().y = Math.max(factor, 0.9f);
      directionalLight.getColour().z = Math.max(factor, 0.5f);
    } else {
      sceneLight.getSkyBoxLight().set(1.0f, 1.0f, 1.0f);
      directionalLight.setIntensity(1);
      directionalLight.getColour().x = 1;
      directionalLight.getColour().y = 1;
      directionalLight.getColour().z = 1;
    }
    double angRad = Math.toRadians(lightAngle);
    directionalLight.getDirection().x = (float) Math.sin(angRad);
    directionalLight.getDirection().y = (float) Math.cos(angRad);
  }

  @Override
  public void render(final Window window) {
    hud.updateSize(window);
    renderer.render(window, camera, scene, hud);
  }

  @Override
  public void cleanup() {
    this.renderer.cleanup();
    final Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
    for (Mesh mesh : mapMeshes.keySet()) {
      mesh.cleanUp();
    }
    hud.cleanup();
  }
}