package com.sizphoto.shiningproject.game;

import com.sizphoto.shiningproject.engine.*;
import com.sizphoto.shiningproject.engine.graph.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import static org.lwjgl.glfw.GLFW.*;

@Component
@Primary
public class DummyGame implements IGameLogic {

  private static final float MOUSE_SENSITIVITY = 0.2f;

  private static final float CAMERA_POS_STEP = 0.05f;

  private final Renderer renderer;

  private final Camera camera;

  private final Vector3f cameraInc;

  private GameItem[] gameItems;

  private SceneLight sceneLight;

  private Hud hud;

  private float lightAngle;

  private float spotAngle = 0;

  private float spotInc = 1;

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

    final float reflectance = 1f;

//    Mesh mesh = ObjLoader.loadMesh("/models/bunny.obj");
//    final Material material = new Material(new Vector4f(0.7f, 0.7f, 0.7f, 0.0f), reflectance);

    Mesh mesh = ObjLoader.loadMesh("/models/cube.obj");
    Texture texture = new Texture("/textures/grassblock.png");
    Material material = new Material(texture, reflectance);

    mesh.setMaterial(material);
    GameItem gameItem = new GameItem(mesh);
    gameItem.setScale(0.5f);
//    gameItem.setPosition(0.0f, -0.5f, -2.0f);
    gameItem.setPosition(0.0f, 0.0f, -2.0f);
    //gameItem.setScale(0.1f);
    //gameItem.setPosition(0, 0, -2);
    //gameItem.setPosition(0, 0, -0.2f);
    gameItems = new GameItem[]{gameItem};

    sceneLight = new SceneLight();

    // Ambient Light
    final Vector3f ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
    sceneLight.setAmbientLight(ambientLight);

    // Point Light
    Vector3f lightPosition = new Vector3f(0, 0, 1);
    float lightIntensity = 1.0f;
    PointLight pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
    PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
    pointLight.setAttenuation(att);
    PointLight[] pointLightList = new PointLight[]{pointLight};
    sceneLight.setPointLightList(pointLightList);

    // Spot Light
    lightPosition = new Vector3f(0, 0.0f, 10f);
    pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
    att = new PointLight.Attenuation(0.0f, 0.0f, 0.02f);
    pointLight.setAttenuation(att);
    Vector3f coneDir = new Vector3f(0, 0, -1);
    float cutoff = (float) Math.cos(Math.toRadians(140));
    SpotLight spotLight = new SpotLight(pointLight, coneDir, cutoff);
    SpotLight[] spotLightList = new SpotLight[]{spotLight, new SpotLight(spotLight)};
    sceneLight.setSpotLightList(spotLightList);

    // Directional light
    lightPosition = new Vector3f(-1, 0, 0);
    DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
    sceneLight.setDirectionalLight(directionalLight);

    // Create HUD
    hud = new Hud("Demo");
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
    SpotLight[] spotLightList = sceneLight.getSpotLightList();
    final float lightPos = spotLightList[0].getPointLight().getPosition().z;
    if (window.isKeyPressed(GLFW_KEY_N)) {
      spotLightList[0].getPointLight().getPosition().z = lightPos + 0.1f;
    } else if (window.isKeyPressed(GLFW_KEY_M)) {
      spotLightList[0].getPointLight().getPosition().z = lightPos - 0.1f;
    }
  }

  @Override
  public void update(final float interval, final MouseInput mouseInput) {
    // Update camera position
    camera.movePosition(
        cameraInc.x * CAMERA_POS_STEP,
        cameraInc.y * CAMERA_POS_STEP,
        cameraInc.z * CAMERA_POS_STEP
    );

    // Update camera based on mouse
    if (mouseInput.isRightButtonPressed()) {
      final Vector2f rotVec = mouseInput.getDisplVec();
      camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

      // Update HUD compass
      hud.rotateCompass(camera.getRotation().y);
    }

    // Update spot light direction
    spotAngle += spotInc * 0.05f;
    if (spotAngle > 2) {
      spotInc = -1;
    } else if (spotAngle < -2) {
      spotInc = 1;
    }
    final double spotAngleRad = Math.toRadians(spotAngle);
    SpotLight[] spotLightList = sceneLight.getSpotLightList();
    Vector3f coneDir = spotLightList[0].getConeDirection();
    coneDir.y = (float) Math.sin(spotAngleRad);

    // Update directional light direction, intensity and color
    DirectionalLight directionalLight = sceneLight.getDirectionalLight();
    lightAngle += 1.1f;
    if (lightAngle > 90) {
      directionalLight.setIntensity(0);
      if (lightAngle >= 360) {
        lightAngle = -90;
      }
    } else if (lightAngle <= -80 || lightAngle >= 80) {
      final float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
      directionalLight.setIntensity(factor);
      directionalLight.getColour().y = Math.max(factor, 0.9f);
      directionalLight.getColour().z = Math.max(factor, 0.5f);
    } else {
      directionalLight.setIntensity(1);
      directionalLight.getColour().x = 1;
      directionalLight.getColour().y = 1;
      directionalLight.getColour().z = 1;
    }
    final double angRad = Math.toRadians(lightAngle);
    directionalLight.getDirection().x = (float) Math.sin(angRad);
    directionalLight.getDirection().y = (float) Math.cos(angRad);
  }

  @Override
  public void render(final Window window) {
    hud.updateSize(window);
    renderer.render(window, camera, gameItems, sceneLight, hud);
  }

  @Override
  public void cleanup() {
    this.renderer.cleanup();
    for (GameItem gameItem : gameItems) {
      gameItem.getMesh().cleanUp();
    }
    hud.cleanup();
  }
}