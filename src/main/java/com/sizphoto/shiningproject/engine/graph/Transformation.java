package com.sizphoto.shiningproject.engine.graph;

import com.sizphoto.shiningproject.engine.GameItem;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.springframework.stereotype.Component;

@Component
public class Transformation {

  private final Matrix4f modelViewMatrix;

  private final Matrix4f projectionMatrix;

  private final Matrix4f viewMatrix;

  private final Matrix4f orthoMatrix;

  public Transformation() {
    modelViewMatrix = new Matrix4f();
    projectionMatrix = new Matrix4f();
    viewMatrix = new Matrix4f();
    orthoMatrix = new Matrix4f();
  }

  final Matrix4f getProjectionMatrix(
      final float fov,
      final float width,
      final float height,
      final float zNear,
      final float zFar
  ) {
    final float aspectRatio = width / height;
    projectionMatrix.identity();
    projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
    return projectionMatrix;
  }

  Matrix4f getViewMatrix(final Camera camera) {
    final Vector3f cameraPos = camera.getPosition();
    final Vector3f rotation = camera.getRotation();

    viewMatrix.identity();
    // First do the rotation so camera rotates over its position
    viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
        .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
    // Then do the translation
    viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    return viewMatrix;
  }

  Matrix4f getModelViewMatrix(final GameItem gameItem, final Matrix4f viewMatrix) {
    final Vector3f rotation = gameItem.getRotation();
    modelViewMatrix.identity().translate(gameItem.getPosition())
        .rotateX((float) Math.toRadians(-rotation.x))
        .rotateY((float) Math.toRadians(-rotation.y))
        .rotateZ((float) Math.toRadians(-rotation.z))
        .scale(gameItem.getScale());
    final Matrix4f viewCurr = new Matrix4f(viewMatrix);
    return viewCurr.mul(modelViewMatrix);
  }

  final Matrix4f getOrthoProjectionMatrix(final float left, final float right,
                                          final float bottom, final float top) {
    orthoMatrix.identity();
    orthoMatrix.setOrtho2D(left, right, bottom, top);
    return orthoMatrix;
  }

  Matrix4f getOrthoProjModelMatrix(final GameItem gameItem, final Matrix4f orthoMatrix) {
    final Vector3f rotation = gameItem.getRotation();
    Matrix4f modelMatrix = new Matrix4f();
    modelMatrix.identity().translate(gameItem.getPosition())
        .rotateX((float) Math.toRadians(-rotation.x))
        .rotateY((float) Math.toRadians(-rotation.y))
        .rotateZ((float) Math.toRadians(-rotation.z))
        .scale(gameItem.getScale());
    Matrix4f orthoMatrixCurr = new Matrix4f(orthoMatrix);
    orthoMatrixCurr.mul(modelMatrix);
    return orthoMatrixCurr;
  }
}