package com.sizphoto.shiningproject.game;

import com.sizphoto.shiningproject.engine.GameItem;
import com.sizphoto.shiningproject.engine.IHud;
import com.sizphoto.shiningproject.engine.TextItem;
import com.sizphoto.shiningproject.engine.Window;
import org.joml.Vector4f;

public class Hud implements IHud {

  private static final int FONT_COLS = 16;

  private static final int FONT_ROWS = 16;

  private static final String FONT_TEXTURE = "/textures/font_texture.png";

  private final GameItem[] gameItems;

  private final TextItem statusTextItem;

  Hud(final String statusText) throws Exception {
    this.statusTextItem = new TextItem(statusText, FONT_TEXTURE, FONT_COLS, FONT_ROWS);
    this.statusTextItem.getMesh().getMaterial()
        .setAmbientColour(new Vector4f(1, 1, 1, 1));
    gameItems = new GameItem[]{statusTextItem};
  }

  @Override
  public GameItem[] getGameItems() {
    return gameItems;
  }

  public void setStatusText(final String statusText) {
    this.statusTextItem.setText(statusText);
  }

  void updateSize(final Window window) {
    this.statusTextItem.setPosition(10f, window.getHeight() - 50f, 0);
  }
}
