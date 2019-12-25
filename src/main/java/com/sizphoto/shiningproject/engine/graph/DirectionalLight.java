package com.sizphoto.shiningproject.engine.graph;

import org.joml.Vector3f;

public class DirectionalLight {

    private Vector3f colour;

    private Vector3f direction;

    private float intensity;

    public DirectionalLight(final Vector3f colour, final Vector3f direction, final float intensity) {
        this.colour = colour;
        this.direction = direction;
        this.intensity = intensity;
    }

    public DirectionalLight(final DirectionalLight light) {
        this(new Vector3f(light.getColour()), new Vector3f(light.getDirection()), light.getIntensity());
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(final Vector3f colour) {
        this.colour = colour;
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