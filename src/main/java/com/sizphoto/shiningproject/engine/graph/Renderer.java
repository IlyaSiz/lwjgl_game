package com.sizphoto.shiningproject.engine.graph;

import com.sizphoto.shiningproject.engine.GameItem;
import com.sizphoto.shiningproject.engine.SceneLight;
import com.sizphoto.shiningproject.engine.Utils;
import com.sizphoto.shiningproject.engine.Window;
import com.sizphoto.shiningproject.engine.IHud;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.sizphoto.shiningproject.utils.Constant.*;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL15.*;

@Component
public class Renderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Renderer.class);

  private static final String PROJECTION_MATRIX_UNIFORM_NAME = "projectionMatrix";
  private static final String MODEL_VIEW__MATRIX_UNIFORM_NAME = "modelViewMatrix";
  private static final String TEXTURE_SAMPLER_UNIFORM_NAME = "texture_sampler";
  private static final String MATERIAL_UNIFORM_NAME = "material";
  private static final String SPECULAR_POWER_UNIFORM_NAME = "specularPower";
  private static final String AMBIENT_LIGHT_UNIFORM_NAME = "ambientLight";
  private static final String POINT_LIGHTS_UNIFORM_NAME = "pointLights";
  private static final String SPOT_LIGHTS_UNIFORM_NAME = "spotLights";
  private static final String DIRECTIONAL_LIGHT_UNIFORM_NAME = "directionalLight";
  private static final String PROJ_MODEL_MATRIX_UNIFORM_NAME = "projModelMatrix";
  private static final String COLOUR_UNIFORM_NAME = "colour";
  private static final String HAS_TEXTURE_UNIFORM_NAME = "hasTexture";

  private final Transformation transformation;

  // Field of View in Radians
  private static final float FOV = (float) Math.toRadians(60.0f);

  private static final float Z_NEAR = 0.01f;

  private static final float Z_FAR = 1000.f;

  private static final int MAX_POINT_LIGHTS = 5;

  private static final int MAX_SPOT_LIGHTS = 5;

  private ShaderProgram sceneShaderProgram;

  private ShaderProgram hudShaderProgram;

  private final float specularPower;

  @Autowired
  public Renderer(
      final Transformation transformation
  ) {
    this.transformation = transformation;
    specularPower = 10f;
  }

  public void init(Window window) throws Exception {
    setupSceneShader();
    setupHudShader();
  }

  private void setupSceneShader() throws Exception {
    // Create shader
    this.sceneShaderProgram = new ShaderProgram();
    this.sceneShaderProgram.createVertexShader(Utils.loadResource(VERTEX_SHADER_FILE_NAME));
    this.sceneShaderProgram.createFragmentShader(Utils.loadResource(FRAGMENT_SHADER_FILE_NAME));
    this.sceneShaderProgram.link();

    // Create uniforms for modelView and projection matrices and texture
    this.sceneShaderProgram.createUniform(PROJECTION_MATRIX_UNIFORM_NAME);
    this.sceneShaderProgram.createUniform(MODEL_VIEW__MATRIX_UNIFORM_NAME);
    this.sceneShaderProgram.createUniform(TEXTURE_SAMPLER_UNIFORM_NAME);
    // Create uniform for material
    this.sceneShaderProgram.createMaterialUniform(MATERIAL_UNIFORM_NAME);
    // Create lighting related uniforms
    this.sceneShaderProgram.createUniform(SPECULAR_POWER_UNIFORM_NAME);
    this.sceneShaderProgram.createUniform(AMBIENT_LIGHT_UNIFORM_NAME);
    this.sceneShaderProgram.createPointLightListUniform(POINT_LIGHTS_UNIFORM_NAME, MAX_POINT_LIGHTS);
    this.sceneShaderProgram.createSpotLightListUniform(SPOT_LIGHTS_UNIFORM_NAME, MAX_SPOT_LIGHTS);
    this.sceneShaderProgram.createDirectionalLightUniform(DIRECTIONAL_LIGHT_UNIFORM_NAME);
  }

  private void setupHudShader() throws Exception {
    this.hudShaderProgram = new ShaderProgram();
    this.hudShaderProgram.createVertexShader(Utils.loadResource(HUD_VERTEX_SHADER_FILE_NAME));
    this.hudShaderProgram.createFragmentShader(Utils.loadResource(HUD_FRAGMENT_SHADER_FILE_NAME));
    this.hudShaderProgram.link();

    // Create uniforms for Orthographic-model projection matrix and base colour
    this.hudShaderProgram.createUniform(PROJ_MODEL_MATRIX_UNIFORM_NAME);
    this.hudShaderProgram.createUniform(COLOUR_UNIFORM_NAME);
    this.hudShaderProgram.createUniform(HAS_TEXTURE_UNIFORM_NAME);
  }

  private void clear() {
    // clear the framebuffer
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public void render(final Window window, final Camera camera, final GameItem[] gameItems,
                     final SceneLight sceneLight, final IHud hud) {

    clear();

    final int windowWidth = window.getWidth();
    final int windowHeight = window.getHeight();

    if (window.isResized()) {
      glViewport(0, 0, windowWidth, windowHeight);
      window.setResized(false);
    }

    renderScene(window, camera, gameItems, sceneLight);

    renderHud(window, hud);
  }

  private void renderScene(final Window window, final Camera camera,
                           final GameItem[] gameItems, final SceneLight sceneLight) {

    final int windowWidth = window.getWidth();
    final int windowHeight = window.getHeight();

    sceneShaderProgram.bind();

    // Update projection Matrix
    final Matrix4f projectionMatrix = transformation.getProjectionMatrix(
        FOV, windowWidth, windowHeight, Z_NEAR, Z_FAR
    );
    sceneShaderProgram.setUniform(PROJECTION_MATRIX_UNIFORM_NAME, projectionMatrix);

    // Update view Matrix
    final Matrix4f viewMatrix = transformation.getViewMatrix(camera);

    // Update Light Uniforms
    renderLights(viewMatrix, sceneLight);

    sceneShaderProgram.setUniform(TEXTURE_SAMPLER_UNIFORM_NAME, 0);
    // Render each gameItem
    for (GameItem gameItem : gameItems) {
      final Mesh mesh = gameItem.getMesh();
      // Set model view matrix for this item
      final Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
      sceneShaderProgram.setUniform(MODEL_VIEW__MATRIX_UNIFORM_NAME, modelViewMatrix);
      // Render the mesh for this game item
      sceneShaderProgram.setUniform(MATERIAL_UNIFORM_NAME, mesh.getMaterial());
      mesh.render();
    }

    this.sceneShaderProgram.unbind();
  }

  private void renderLights(final Matrix4f viewMatrix, final SceneLight sceneLight) {

    sceneShaderProgram.setUniform(AMBIENT_LIGHT_UNIFORM_NAME, sceneLight.getAmbientLight());
    sceneShaderProgram.setUniform(SPECULAR_POWER_UNIFORM_NAME, specularPower);

    // Process Point Lights
    final PointLight[] pointLightList = sceneLight.getPointLightList();
    int numLights = pointLightList != null ? pointLightList.length : 0;
    for (int i = 0; i < numLights; i++) {
      // Get a copy of the point light object and transform its position to view coordinates
      final PointLight currPointLight = new PointLight(pointLightList[i]);
      Vector3f lightPos = currPointLight.getPosition();
      setViewLightPosition(lightPos, viewMatrix);
      sceneShaderProgram.setUniform(POINT_LIGHTS_UNIFORM_NAME, currPointLight, i);
    }

    // Process Spot Lights
    final SpotLight[] spotLightList = sceneLight.getSpotLightList();
    numLights = spotLightList != null ? spotLightList.length : 0;
    for (int i = 0; i < numLights; i++) {
      // Get a copy of the spot light object and transform its position and cone direction
      // to view coordinates
      SpotLight currSpotLight = new SpotLight(spotLightList[i]);
      final Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
      dir.mul(viewMatrix);
      currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
      Vector3f lightPos = currSpotLight.getPointLight().getPosition();
      setViewLightPosition(lightPos, viewMatrix);
      sceneShaderProgram.setUniform(SPOT_LIGHTS_UNIFORM_NAME, currSpotLight, i);
    }

    // Get a copy of the directional light object and transform its position to view coordinates
    DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
    final Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
    dir.mul(viewMatrix);
    currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
    sceneShaderProgram.setUniform(DIRECTIONAL_LIGHT_UNIFORM_NAME, currDirLight);
  }

  private void renderHud(final Window window, final IHud hud) {
    hudShaderProgram.bind();

    Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
    for (GameItem gameItem : hud.getGameItems()) {
      Mesh mesh = gameItem.getMesh();
      // Set orthographic and model matrix for this HUD item
      Matrix4f projModelMatrix = transformation.getOrthoProjModelMatrix(gameItem, ortho);
      hudShaderProgram.setUniform(PROJ_MODEL_MATRIX_UNIFORM_NAME, projModelMatrix);
      hudShaderProgram.setUniform(COLOUR_UNIFORM_NAME, gameItem.getMesh().getMaterial().getAmbientColour());
      hudShaderProgram.setUniform(HAS_TEXTURE_UNIFORM_NAME, gameItem.getMesh().getMaterial().isTextured() ? 1 : 0);

      // Render the mesh for this HUD item
      mesh.render();
    }

    hudShaderProgram.unbind();
  }

  public void cleanup() {
    if (sceneShaderProgram != null) {
      sceneShaderProgram.cleanup();
    }

    if (hudShaderProgram != null) {
      hudShaderProgram.cleanup();
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

  private void setViewLightPosition(final Vector3f lightPos, final Matrix4f viewMatrix) {
    final Vector4f aux = new Vector4f(lightPos, 1);
    aux.mul(viewMatrix);
    lightPos.x = aux.x;
    lightPos.y = aux.y;
    lightPos.z = aux.z;
  }
}