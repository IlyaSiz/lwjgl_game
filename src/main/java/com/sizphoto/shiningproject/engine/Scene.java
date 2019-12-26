package com.sizphoto.shiningproject.engine;

public class Scene {

  private GameItem[] gameItems;

  private SkyBox skyBox;

  private SceneLight sceneLight;

  public GameItem[] getGameItems() {
    return gameItems;
  }

  public void setGameItems(final GameItem[] gameItems) {
    this.gameItems = gameItems;
  }

  public SkyBox getSkyBox() {
    return skyBox;
  }

  public void setSkyBox(final SkyBox skyBox) {
    this.skyBox = skyBox;
  }

  public SceneLight getSceneLight() {
    return sceneLight;
  }

  public void setSceneLight(final SceneLight sceneLight) {
    this.sceneLight = sceneLight;
  }

}
