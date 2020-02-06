package com.sizphoto.shiningproject.engine.graph.lights;

import org.joml.Vector3f;

public class PointLight {

    private Vector3f colour;

    private Vector3f position;

    private float intensity;

    private Attenuation attenuation;

    private PointLight(final Vector3f colour, final Vector3f position, final float intensity) {
        attenuation = new Attenuation(1, 0, 0);
        this.colour = colour;
        this.position = position;
        this.intensity = intensity;
    }

    private PointLight(final Vector3f colour, final Vector3f position, final float intensity,
                       final Attenuation attenuation) {
        this(colour, position, intensity);
        this.attenuation = attenuation;
    }

    public PointLight(final PointLight pointLight) {
        this(new Vector3f(pointLight.getColour()), new Vector3f(pointLight.getPosition()),
                pointLight.getIntensity(), pointLight.getAttenuation());
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(final Vector3f colour) {
        this.colour = colour;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(final Vector3f position) {
        this.position = position;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(final float intensity) {
        this.intensity = intensity;
    }

    public Attenuation getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(final Attenuation attenuation) {
        this.attenuation = attenuation;
    }

    public static class Attenuation {

        private float constant;

        private float linear;

        private float exponent;

        Attenuation(final float constant, final float linear, final float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        public float getConstant() {
            return constant;
        }

        public void setConstant(final float constant) {
            this.constant = constant;
        }

        public float getLinear() {
            return linear;
        }

        public void setLinear(final float linear) {
            this.linear = linear;
        }

        public float getExponent() {
            return exponent;
        }

        public void setExponent(final float exponent) {
            this.exponent = exponent;
        }
    }
}