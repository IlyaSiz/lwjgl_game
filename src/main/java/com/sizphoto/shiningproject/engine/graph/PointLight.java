package com.sizphoto.shiningproject.engine.graph;

import org.joml.Vector3f;

public class PointLight {

    private Vector3f color;

    private Vector3f position;

    private float intensity;

    private Attenuation attenuation;

    public PointLight(final Vector3f color, final Vector3f position, final float intensity) {
        attenuation = new Attenuation(1, 0, 0);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    private PointLight(final Vector3f color, final Vector3f position, final float intensity,
                       final Attenuation attenuation) {
        this(color, position, intensity);
        this.attenuation = attenuation;
    }

    public PointLight(final PointLight pointLight) {
        this(new Vector3f(pointLight.getColor()), new Vector3f(pointLight.getPosition()),
                pointLight.getIntensity(), pointLight.getAttenuation());
    }

    Vector3f getColor() {
        return color;
    }

    public void setColor(final Vector3f color) {
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(final Vector3f position) {
        this.position = position;
    }

    float getIntensity() {
        return intensity;
    }

    public void setIntensity(final float intensity) {
        this.intensity = intensity;
    }

    Attenuation getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(final Attenuation attenuation) {
        this.attenuation = attenuation;
    }

    public static class Attenuation {

        private float constant;

        private float linear;

        private float exponent;

        public Attenuation(final float constant, final float linear, final float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        float getConstant() {
            return constant;
        }

        public void setConstant(final float constant) {
            this.constant = constant;
        }

        float getLinear() {
            return linear;
        }

        public void setLinear(final float linear) {
            this.linear = linear;
        }

        float getExponent() {
            return exponent;
        }

        public void setExponent(final float exponent) {
            this.exponent = exponent;
        }
    }
}