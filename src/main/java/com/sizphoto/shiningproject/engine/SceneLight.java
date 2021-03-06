package com.sizphoto.shiningproject.engine;

import com.sizphoto.shiningproject.engine.graph.lights.DirectionalLight;
import com.sizphoto.shiningproject.engine.graph.lights.PointLight;
import com.sizphoto.shiningproject.engine.graph.lights.SpotLight;
import org.joml.Vector3f;

public class SceneLight {

    private Vector3f ambientLight;

    private Vector3f skyBoxLight;

    private PointLight[] pointLightList;

    private SpotLight[] spotLightList;

    private DirectionalLight directionalLight;

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(final Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public PointLight[] getPointLightList() {
        return pointLightList;
    }

    public void setPointLightList(final PointLight[] pointLightList) {
        this.pointLightList = pointLightList;
    }

    public SpotLight[] getSpotLightList() {
        return spotLightList;
    }

    public void setSpotLightList(final SpotLight[] spotLightList) {
        this.spotLightList = spotLightList;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(final DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public Vector3f getSkyBoxLight() {
        return skyBoxLight;
    }

    public void setSkyBoxLight(final Vector3f skyBoxLight) {
        this.skyBoxLight = skyBoxLight;
    }
}