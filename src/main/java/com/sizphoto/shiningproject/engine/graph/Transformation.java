package com.sizphoto.shiningproject.engine.graph;

import com.sizphoto.shiningproject.engine.items.GameItem;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.springframework.stereotype.Component;

@Component
public class Transformation {

    private final Matrix4f projectionMatrix;

    private final Matrix4f modelMatrix;

    private final Matrix4f modelViewMatrix;

    private final Matrix4f viewMatrix;

    private final Matrix4f orthoMatrix;

    private final Matrix4f orthoModelMatrix;

    public Transformation() {
        projectionMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        orthoMatrix = new Matrix4f();
        orthoModelMatrix = new Matrix4f();
    }

    Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    void updateProjectionMatrix(
            final float fov,
            final float width,
            final float height,
            final float zNear,
            final float zFar
    ) {
        final float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
    }

    Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    void updateViewMatrix(final Camera camera) {
        final Vector3f cameraPos = camera.getPosition();
        final Vector3f rotation = camera.getRotation();

        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    }

    final Matrix4f getOrthoProjectionMatrix(
            final float left,
            final float right,
            final float bottom,
            final float top
    ) {
        orthoMatrix.identity();
        orthoMatrix.setOrtho2D(left, right, bottom, top);
        return orthoMatrix;
    }

    Matrix4f buildModelViewMatrix(final GameItem gameItem, final Matrix4f viewMatrix) {
        updateModelMatrix(gameItem);
        modelViewMatrix.set(viewMatrix);
        return modelViewMatrix.mul(modelMatrix);
    }

    Matrix4f buildOrthoProjModelMatrix(final GameItem gameItem, final Matrix4f orthoMatrix) {
        updateModelMatrix(gameItem);
        orthoModelMatrix.set(orthoMatrix);
        orthoModelMatrix.mul(modelMatrix);
        return orthoModelMatrix;
    }

    private void updateModelMatrix(final GameItem gameItem) {
        Vector3f rotation = gameItem.getRotation();
        modelMatrix.identity().translate(gameItem.getPosition())
                .rotateX((float) Math.toRadians(-rotation.x))
                .rotateY((float) Math.toRadians(-rotation.y))
                .rotateZ((float) Math.toRadians(-rotation.z))
                .scale(gameItem.getScale());
    }
}