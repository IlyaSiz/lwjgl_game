package com.sizphoto.shiningproject.engine.items;

import com.sizphoto.shiningproject.engine.graph.HeightMapMesh;

public class Terrain {

    private final GameItem[] gameItems;

    public Terrain(
            final int blocksPerRow,
            final float scale,
            final float minY,
            final float maxY,
            final String heightMap,
            final String textureFile,
            final int textInc
    ) throws Exception {
        gameItems = new GameItem[blocksPerRow * blocksPerRow];
        final HeightMapMesh heightMapMesh = new HeightMapMesh(minY, maxY, heightMap, textureFile, textInc);
        for (int row = 0; row < blocksPerRow; row++) {
            for (int col = 0; col < blocksPerRow; col++) {
                final float xDisplacement = (col - ((float) blocksPerRow - 1) / (float) 2)
                        * scale * HeightMapMesh.getXLength();
                final float zDisplacement = (row - ((float) blocksPerRow - 1) / (float) 2)
                        * scale * HeightMapMesh.getZLength();

                GameItem terrainBlock = new GameItem(heightMapMesh.getMesh());
                terrainBlock.setScale(scale);
                terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
                gameItems[row * blocksPerRow + col] = terrainBlock;
            }
        }
    }

    public GameItem[] getGameItems() {
        return gameItems;
    }
}
