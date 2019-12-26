package com.sizphoto.shiningproject.engine.graph;

import com.sizphoto.shiningproject.engine.graph.lights.DirectionalLight;
import com.sizphoto.shiningproject.engine.graph.lights.PointLight;
import com.sizphoto.shiningproject.engine.graph.lights.SpotLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

class ShaderProgram {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShaderProgram.class);

  private final int programId;

  private int vertexShaderId;

  private int fragmentShaderId;

  private final Map<String, Integer> uniforms;

  ShaderProgram() throws Exception {
    programId = glCreateProgram();
    if (programId == 0) {
      LOGGER.error("ShaderProgram() - Could not create Shader");
      throw new Exception("Could not create Shader");
    }
    uniforms = new HashMap<>();
  }

  void createUniform(final String uniformName) throws Exception {
    final int uniformLocation = glGetUniformLocation(programId, uniformName);
    if (uniformLocation < 0) {
      final String errorMessage = String.format("Could not find uniform [%s]", uniformName);
      LOGGER.error("createUniform() - " + errorMessage);
      throw new Exception(errorMessage);
    }
    uniforms.put(uniformName, uniformLocation);
  }

  void createPointLightListUniform(final String uniformName, final int size) throws Exception {
    for (int i = 0; i < size; i++) {
      createPointLightUniform(uniformName + "[" + i + "]");
    }
  }

  private void createPointLightUniform(final String uniformName) throws Exception {
    createUniform(uniformName + ".colour");
    createUniform(uniformName + ".position");
    createUniform(uniformName + ".intensity");
    createUniform(uniformName + ".att.constant");
    createUniform(uniformName + ".att.linear");
    createUniform(uniformName + ".att.exponent");
  }

  void createSpotLightListUniform(final String uniformName, final int size) throws Exception {
    for (int i = 0; i < size; i++) {
      createSpotLightUniform(uniformName + "[" + i + "]");
    }
  }

  private void createSpotLightUniform(final String uniformName) throws Exception {
    createPointLightUniform(uniformName + ".pl");
    createUniform(uniformName + ".conedir");
    createUniform(uniformName + ".cutoff");
  }

  void createDirectionalLightUniform(final String uniformName) throws Exception {
    createUniform(uniformName + ".colour");
    createUniform(uniformName + ".direction");
    createUniform(uniformName + ".intensity");
  }

  void createMaterialUniform(final String uniformName) throws Exception {
    createUniform(uniformName + ".ambient");
    createUniform(uniformName + ".diffuse");
    createUniform(uniformName + ".specular");
    createUniform(uniformName + ".hasTexture");
    createUniform(uniformName + ".reflectance");
  }

  void setUniform(final String uniformName, final Matrix4f value) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      // Dump the matrix into a float buffer
      final FloatBuffer fb = stack.mallocFloat(16);
      value.get(fb);
      glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
    }
  }

  void setUniform(final String uniformName, final int value) {
    glUniform1i(uniforms.get(uniformName), value);
  }

  void setUniform(final String uniformName, final float value) {
    glUniform1f(uniforms.get(uniformName), value);
  }

  void setUniform(final String uniformName, final Vector3f value) {
    glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
  }

  void setUniform(final String uniformName, final Vector4f value) {
    glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
  }

  void setUniform(final String uniformName, final PointLight[] pointLights) {
    final int numLights = pointLights != null ? pointLights.length : 0;
    for (int i = 0; i < numLights; i++) {
      setUniform(uniformName, pointLights[i], i);
    }
  }

  void setUniform(final String uniformName, final PointLight pointLight, final int pos) {
    setUniform(uniformName + "[" + pos + "]", pointLight);
  }

  private void setUniform(final String uniformName, final PointLight pointLight) {
    setUniform(uniformName + ".colour", pointLight.getColour());
    setUniform(uniformName + ".position", pointLight.getPosition());
    setUniform(uniformName + ".intensity", pointLight.getIntensity());
    final PointLight.Attenuation att = pointLight.getAttenuation();
    setUniform(uniformName + ".att.constant", att.getConstant());
    setUniform(uniformName + ".att.linear", att.getLinear());
    setUniform(uniformName + ".att.exponent", att.getExponent());
  }

  void setUniform(final String uniformName, final SpotLight[] spotLights) {
    final int numLights = spotLights != null ? spotLights.length : 0;
    for (int i = 0; i < numLights; i++) {
      setUniform(uniformName, spotLights[i], i);
    }
  }

  void setUniform(final String uniformName, final SpotLight spotLight, final int pos) {
    setUniform(uniformName + "[" + pos + "]", spotLight);
  }

  private void setUniform(final String uniformName, final SpotLight spotLight) {
    setUniform(uniformName + ".pl", spotLight.getPointLight());
    setUniform(uniformName + ".conedir", spotLight.getConeDirection());
    setUniform(uniformName + ".cutoff", spotLight.getCutOff());
  }

  void setUniform(final String uniformName, final DirectionalLight dirLight) {
    setUniform(uniformName + ".colour", dirLight.getColour());
    setUniform(uniformName + ".direction", dirLight.getDirection());
    setUniform(uniformName + ".intensity", dirLight.getIntensity());
  }

  void setUniform(final String uniformName, final Material material) {
    setUniform(uniformName + ".ambient", material.getAmbientColour());
    setUniform(uniformName + ".diffuse", material.getDiffuseColour());
    setUniform(uniformName + ".specular", material.getSpecularColour());
    setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
    setUniform(uniformName + ".reflectance", material.getReflectance());
  }

  void createVertexShader(final String shaderCode) throws Exception {
    vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
  }

  void createFragmentShader(final String shaderCode) throws Exception {
    fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
  }

  private int createShader(final String shaderCode, final int shaderType) throws Exception {
    final int shaderId = glCreateShader(shaderType);
    if (shaderId == 0) {
      LOGGER.error("createShader() - Error creating shader. Type: {}", shaderType);
      throw new Exception("Error creating shader. Type: " + shaderType);
    }

    glShaderSource(shaderId, shaderCode);
    glCompileShader(shaderId);

    if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
      final String shaderInfoLog = glGetShaderInfoLog(shaderId, 1024);
      LOGGER.error("createShader() - Error compiling Shader code: {}", shaderInfoLog);
      throw new Exception("Error compiling Shader code: " + shaderInfoLog);
    }

    glAttachShader(programId, shaderId);

    return shaderId;
  }

  void link() throws Exception {
    glLinkProgram(programId);
    if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
      final String programInfoLog = glGetProgramInfoLog(programId, 1024);
      LOGGER.error("link() - Error linking Shader code: {}", programInfoLog);
      throw new Exception("Error linking Shader code: " + programInfoLog);
    }

    if (vertexShaderId != 0) {
      glDetachShader(programId, vertexShaderId);
    }
    if (fragmentShaderId != 0) {
      glDetachShader(programId, fragmentShaderId);
    }

    // This method is used mainly for debugging purposes, and it should be removed when the game
    // reaches production stage. This means, that validation may fail in some cases even if the
    // shader is correct, due to the fact that the current state is not complete enough to run the
    // shader (some data may have not been uploaded yet).
    glValidateProgram(programId);
    if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
      LOGGER.warn(
          "link() - Warning validating Shader code: {}",
          glGetProgramInfoLog(programId, 1024)
      );
    }

  }

  void bind() {
    glUseProgram(programId);
  }

  void unbind() {
    glUseProgram(0);
  }

  void cleanup() {
    unbind();
    if (programId != 0) {
      glDeleteProgram(programId);
    }
  }
}