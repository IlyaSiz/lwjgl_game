package com.sizphoto.shiningproject.engine;

import com.sizphoto.shiningproject.engine.graph.FontTexture;
import com.sizphoto.shiningproject.engine.graph.Material;
import com.sizphoto.shiningproject.engine.graph.Mesh;

import java.util.ArrayList;
import java.util.List;

public class TextItem extends GameItem {

  private static final float Z_POS = 0.0f;

  private static final int VERTICES_PER_QUAD = 4;

  private final FontTexture fontTexture;

  private String text;

  public TextItem(final String text, final FontTexture fontTexture) {
    super();
    this.text = text;
    this.fontTexture = fontTexture;
    this.setMesh(buildMesh());
  }

  private Mesh buildMesh() {
    List<Float> positions = new ArrayList<>();
    List<Float> textCoords = new ArrayList<>();
    final float[] normals = new float[0];
    List<Integer> indices = new ArrayList<>();
    char[] characters = text.toCharArray();
    int numChars = characters.length;

    float startX = 0;

    for (int i = 0; i < numChars; i++) {

      FontTexture.CharInfo charInfo = fontTexture.getCharInfo(characters[i]);

      // Build a character tile composed by two triangles

      // We will represent the vertices using screen coordinates (remember that the origin of the screen coordinates
      // is located at the top left corner). The y coordinate of the vertices on top of the triangles is lower than
      // the y coordinate of the vertices on the bottom of the triangles.

      // We don’t scale the shape, so each tile is at a x distance equal to a character width. The height of the
      // triangles will be the height of each character. This is because we want to represent the text as similar
      // as possible as the original texture. (Anyway we can scale the result since TextItem class inherits
      // from GameItem).

      // We set a fixed value for the z coordinate, since it will be irrelevant in order to draw this object.

      // Left Top vertex
      positions.add(startX); // x
      positions.add(0.0f); //y
      positions.add(Z_POS); //z
      textCoords.add((float) charInfo.getStartX() / (float) fontTexture.getWidth());
      textCoords.add(0.0f);
      indices.add(i * VERTICES_PER_QUAD);

      // Left Bottom vertex
      positions.add(startX); // x
      positions.add((float) fontTexture.getHeight()); //y
      positions.add(Z_POS); //z
      textCoords.add((float) charInfo.getStartX() / (float) fontTexture.getWidth());
      textCoords.add(1.0f);
      indices.add(i * VERTICES_PER_QUAD + 1);

      // Right Bottom vertex
      positions.add(startX + charInfo.getWidth()); // x
      positions.add((float) fontTexture.getHeight()); //y
      positions.add(Z_POS); //z
      textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) fontTexture.getWidth());
      textCoords.add(1.0f);
      indices.add(i * VERTICES_PER_QUAD + 2);

      // Right Top vertex
      positions.add(startX + charInfo.getWidth()); // x
      positions.add(0.0f); //y
      positions.add(Z_POS); //z
      textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) fontTexture.getWidth());
      textCoords.add(0.0f);
      indices.add(i * VERTICES_PER_QUAD + 3);

      // Add indices por left top and bottom right vertices
      indices.add(i * VERTICES_PER_QUAD);
      indices.add(i * VERTICES_PER_QUAD + 2);

      startX += charInfo.getWidth();
    }

    final float[] posArr = Utils.listToArray(positions);
    final float[] textCoordsArr = Utils.listToArray(textCoords);
    final int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
    Mesh mesh = new Mesh(posArr, textCoordsArr, normals, indicesArr);
    mesh.setMaterial(new Material(fontTexture.getTexture()));
    return mesh;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
    this.getMesh().deleteBuffers();
    this.setMesh(buildMesh());
  }
}
