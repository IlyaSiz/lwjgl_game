package com.sizphoto.shiningproject.engine;

import com.sizphoto.shiningproject.engine.items.GameItem;

public interface IHud {

  GameItem[] getGameItems();

  default void cleanup() {
    GameItem[] gameItems = getGameItems();
    for (GameItem gameItem : gameItems) {
      gameItem.getMesh().cleanUp();
    }
  }
}
