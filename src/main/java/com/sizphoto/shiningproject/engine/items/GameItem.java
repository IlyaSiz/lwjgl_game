package com.sizphoto.shiningproject.engine.items;

import com.sizphoto.shiningproject.engine.graph.Mesh;
import org.joml.Vector3f;

public class GameItem {

    private Mesh mesh;

    private final Vector3f position;

    private float scale;

    private final Vector3f rotation;

    GameItem() {
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public GameItem(Mesh mesh) {
        this();
        this.mesh = mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(final float x, final float y, final float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(final float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(final float x, final float y, final float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Mesh getMesh() {
        return mesh;
    }

    void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}