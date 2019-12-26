package com.sizphoto.shiningproject.game;

import com.sizphoto.shiningproject.engine.GameItem;
import com.sizphoto.shiningproject.engine.IHud;
import com.sizphoto.shiningproject.engine.TextItem;
import com.sizphoto.shiningproject.engine.Window;
import com.sizphoto.shiningproject.engine.graph.FontTexture;
import com.sizphoto.shiningproject.engine.graph.Material;
import com.sizphoto.shiningproject.engine.graph.Mesh;
import com.sizphoto.shiningproject.engine.graph.ObjLoader;
import org.joml.Vector4f;

import java.awt.*;

public class Hud implements IHud {

  private static final Font FONT = new Font("Helvetica", Font.PLAIN, 20);

  private static final String CHARSET = "CP1251";

  private static final String COMPASS_MESH = "/models/compass.obj";

  private final GameItem[] gameItems;

  private final TextItem statusTextItem;

  private final GameItem compassItem;

  Hud(final String statusText) throws Exception {
    FontTexture fontTexture = new FontTexture(FONT, CHARSET);
    this.statusTextItem = new TextItem(statusText, fontTexture);
    this.statusTextItem.getMesh().getMaterial()
        .setAmbientColour(new Vector4f(1, 1, 1, 1));

    // Create compass
    Mesh mesh = ObjLoader.loadMesh(COMPASS_MESH);
    Material material = new Material();
    material.setAmbientColour(new Vector4f(0.5f, 0.5f, 0.6f, 1));
    mesh.setMaterial(material);
    compassItem = new GameItem(mesh);
    compassItem.setScale(40.0f);
    // Rotate to transform it to screen coordinates
    compassItem.setRotation(0f, 0f, 180f);

    // Create list that holds the items that compose the HUD
    gameItems = new GameItem[]{statusTextItem, compassItem};
  }

  @Override
  public GameItem[] getGameItems() {
    return gameItems;
  }

  public void setStatusText(final String statusText) {
    this.statusTextItem.setText(statusText);
  }

  void rotateCompass(final float angle) {
    this.compassItem.setRotation(0, 0, 180 + angle);
  }

  void updateSize(final Window window) {
    this.statusTextItem.setPosition(10f, window.getHeight() - 50f, 0);
    this.compassItem.setPosition(window.getWidth() - 40f, 50f, 0);
  }
}
