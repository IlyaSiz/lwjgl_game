package com.sizphoto.shiningproject.game;

import com.sizphoto.shiningproject.engine.GameItem;
import com.sizphoto.shiningproject.engine.Utils;
import com.sizphoto.shiningproject.engine.Window;
import com.sizphoto.shiningproject.engine.graph.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.sizphoto.shiningproject.utils.Constant.FRAGMENT_SHADER_FILE_NAME;
import static com.sizphoto.shiningproject.utils.Constant.VERTEX_SHADER_FILE_NAME;
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

  private final Transformation transformation;

  // Field of View in Radians
  private static final float FOV = (float) Math.toRadians(60.0f);

  private static final float Z_NEAR = 0.01f;

  private static final float Z_FAR = 1000.f;

  private static final int MAX_POINT_LIGHTS = 5;

  private static final int MAX_SPOT_LIGHTS = 5;

  private ShaderProgram shaderProgram;

  private final float specularPower;

  @Autowired
  public Renderer(
      final Transformation transformation
  ) {
    this.transformation = transformation;
    specularPower = 10f;
  }

  void init(final Window window) throws Exception {
    // Create shader
    this.shaderProgram = new ShaderProgram();
    this.shaderProgram.createVertexShader(Utils.loadResource(VERTEX_SHADER_FILE_NAME));
    this.shaderProgram.createFragmentShader(Utils.loadResource(FRAGMENT_SHADER_FILE_NAME));
    this.shaderProgram.link();

    // Create uniforms for modelView and projection matrices and texture
    shaderProgram.createUniform(PROJECTION_MATRIX_UNIFORM_NAME);
    shaderProgram.createUniform(MODEL_VIEW__MATRIX_UNIFORM_NAME);
    shaderProgram.createUniform(TEXTURE_SAMPLER_UNIFORM_NAME);
    // Create uniform for material
    shaderProgram.createMaterialUniform(MATERIAL_UNIFORM_NAME);
    // Create lighting related uniforms
    shaderProgram.createUniform(SPECULAR_POWER_UNIFORM_NAME);
    shaderProgram.createUniform(AMBIENT_LIGHT_UNIFORM_NAME);
    shaderProgram.createPointLightListUniform(POINT_LIGHTS_UNIFORM_NAME, MAX_POINT_LIGHTS);
    shaderProgram.createSpotLightListUniform(SPOT_LIGHTS_UNIFORM_NAME, MAX_SPOT_LIGHTS);
    shaderProgram.createDirectionalLightUniform(DIRECTIONAL_LIGHT_UNIFORM_NAME);
  }

  private void clear() {
    // clear the framebuffer
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  void render(final Window window, final Camera camera, final GameItem[] gameItems,
              final Vector3f ambientLight, final PointLight[] pointLightList,
              final SpotLight[] spotLightList, final DirectionalLight directionalLight) {

    clear();

    final int windowWidth = window.getWidth();
    final int windowHeight = window.getHeight();

    if (window.isResized()) {
      glViewport(0, 0, windowWidth, windowHeight);
      window.setResized(false);
    }

    shaderProgram.bind();

    // Update projection Matrix
    final Matrix4f projectionMatrix = transformation.getProjectionMatrix(
        FOV, windowWidth, windowHeight, Z_NEAR, Z_FAR
    );
    shaderProgram.setUniform(PROJECTION_MATRIX_UNIFORM_NAME, projectionMatrix);

    // Update view Matrix
    final Matrix4f viewMatrix = transformation.getViewMatrix(camera);

    // Update Light Uniforms
    renderLights(viewMatrix, ambientLight, pointLightList, spotLightList, directionalLight);

    shaderProgram.setUniform(TEXTURE_SAMPLER_UNIFORM_NAME, 0);
    // Render each gameItem
    for (GameItem gameItem : gameItems) {
      final Mesh mesh = gameItem.getMesh();
      // Set model view matrix for this item
      final Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
      shaderProgram.setUniform(MODEL_VIEW__MATRIX_UNIFORM_NAME, modelViewMatrix);
      // Render the mesh for this game item
      shaderProgram.setUniform(MATERIAL_UNIFORM_NAME, mesh.getMaterial());
      mesh.render();
    }

    this.shaderProgram.unbind();
  }

  private void renderLights(final Matrix4f viewMatrix, final Vector3f ambientLight,
                            final PointLight[] pointLightList, final SpotLight[] spotLightList,
                            final DirectionalLight directionalLight) {

    shaderProgram.setUniform(AMBIENT_LIGHT_UNIFORM_NAME, ambientLight);
    shaderProgram.setUniform(SPECULAR_POWER_UNIFORM_NAME, specularPower);

    // Process Point Lights
    int numLights = pointLightList != null ? pointLightList.length : 0;
    for (int i = 0; i < numLights; i++) {
      // Get a copy of the point light object and transform its position to view coordinates
      final PointLight currPointLight = new PointLight(pointLightList[i]);
      Vector3f lightPos = currPointLight.getPosition();
      setViewLightPosition(lightPos, viewMatrix);
      shaderProgram.setUniform(POINT_LIGHTS_UNIFORM_NAME, currPointLight, i);
    }

    // Process Spot Lights
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
      shaderProgram.setUniform(SPOT_LIGHTS_UNIFORM_NAME, currSpotLight, i);
    }

    // Get a copy of the directional light object and transform its position to view coordinates
    DirectionalLight currDirLight = new DirectionalLight(directionalLight);
    final Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
    dir.mul(viewMatrix);
    currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
    shaderProgram.setUniform(DIRECTIONAL_LIGHT_UNIFORM_NAME, currDirLight);

  }

  void cleanup() {
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


  private void setViewLightPosition(final Vector3f lightPos, final Matrix4f viewMatrix) {
    final Vector4f aux = new Vector4f(lightPos, 1);
    aux.mul(viewMatrix);
    lightPos.x = aux.x;
    lightPos.y = aux.y;
    lightPos.z = aux.z;
  }
}