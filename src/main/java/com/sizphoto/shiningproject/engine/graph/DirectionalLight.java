package com.sizphoto.shiningproject.engine.graph;

import org.joml.Vector3f;

public class DirectionalLight {

    private Vector3f color;

    private Vector3f direction;

    private float intensity;

    public DirectionalLight(final Vector3f color, final Vector3f direction, final float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public DirectionalLight(final DirectionalLight light) {
        this(new Vector3f(light.getColor()), new Vector3f(light.getDirection()), light.getIntensity());
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(final Vector3f color) {
        this.color = color;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(final Vector3f direction) {
        this.direction = direction;
    }

    float getIntensity() {
        return intensity;
    }

    public void setIntensity(final float intensity) {
        this.intensity = intensity;
    }
}