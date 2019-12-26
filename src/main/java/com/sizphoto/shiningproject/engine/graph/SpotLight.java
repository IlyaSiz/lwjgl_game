package com.sizphoto.shiningproject.engine.graph;

import org.joml.Vector3f;

public class SpotLight {

  private PointLight pointLight;

  private Vector3f coneDirection;

  private float cutOff;

  private SpotLight(
      final PointLight pointLight,
      final Vector3f coneDirection,
      final float cutOffAngle
  ) {
    this.pointLight = pointLight;
    this.coneDirection = coneDirection;
    setCutOffAngle(cutOffAngle);
  }

  SpotLight(final SpotLight spotLight) {
    this(new PointLight(spotLight.getPointLight()),
        new Vector3f(spotLight.getConeDirection()),
        0);
    setCutOff(spotLight.getCutOff());
  }

  PointLight getPointLight() {
    return pointLight;
  }

  public void setPointLight(final PointLight pointLight) {
    this.pointLight = pointLight;
  }

  Vector3f getConeDirection() {
    return coneDirection;
  }

  void setConeDirection(final Vector3f coneDirection) {
    this.coneDirection = coneDirection;
  }

  float getCutOff() {
    return cutOff;
  }

  private void setCutOff(final float cutOff) {
    this.cutOff = cutOff;
  }

  private void setCutOffAngle(final float cutOffAngle) {
    this.setCutOff((float) Math.cos(Math.toRadians(cutOffAngle)));
  }
}