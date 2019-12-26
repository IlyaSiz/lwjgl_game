package com.sizphoto.shiningproject.engine;

import com.sizphoto.shiningproject.engine.graph.Mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

  private Map<Mesh, List<GameItem>> meshMap;

  private SkyBox skyBox;

  private SceneLight sceneLight;

  public Scene() {
    meshMap = new HashMap<>();
  }

  public Map<Mesh, List<GameItem>> getGameMeshes() {
    return meshMap;
  }

  public void setGameItems(final GameItem[] gameItems) {
    int numGameItems = gameItems != null ? gameItems.length : 0;
    for (int i = 0; i < numGameItems; i++) {
      GameItem gameItem = gameItems[i];
      Mesh mesh = gameItem.getMesh();
      List<GameItem> list = meshMap.computeIfAbsent(mesh, k -> new ArrayList<>());
      list.add(gameItem);
    }
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
