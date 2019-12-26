package com.sizphoto.shiningproject.engine;

import com.sizphoto.shiningproject.engine.graph.Material;
import com.sizphoto.shiningproject.engine.graph.Mesh;
import com.sizphoto.shiningproject.engine.graph.Texture;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TextItem extends GameItem {

  private static final float Z_POS = 0.0f;

  private static final int VERTICES_PER_QUAD = 4;

  private String text;

  private final int numCols;

  private final int numRows;

  public TextItem(final String text, final String fontFileName, final int numCols,
                  final int numRows) throws Exception {
    super();
    this.text = text;
    this.numCols = numCols;
    this.numRows = numRows;
    final Texture texture = new Texture(fontFileName);
    this.setMesh(buildMesh(texture, numCols, numRows));
  }

  private Mesh buildMesh(final Texture texture, final int numCols, final int numRows) {
    final byte[] chars = text.getBytes(Charset.forName("ISO-8859-1"));
    final int numChars = chars.length;

    List<Float> positions = new ArrayList<>();
    List<Float> textCoords = new ArrayList<>();
    final float[] normals = new float[0];
    List<Integer> indices = new ArrayList<>();

    final float tileWidth = (float) texture.getWidth() / (float) numCols;
    final float tileHeight = (float) texture.getHeight() / (float) numRows;

    for (int i = 0; i < numChars; i++) {
      final byte currChar = chars[i];
      final int col = currChar % numCols;
      final int row = currChar / numCols;

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
      positions.add((float) i * tileWidth); // x
      positions.add(0.0f); //y
      positions.add(Z_POS); //z
      textCoords.add((float) col / (float) numCols);
      textCoords.add((float) row / (float) numRows);
      indices.add(i * VERTICES_PER_QUAD);

      // Left Bottom vertex
      positions.add((float) i * tileWidth); // x
      positions.add(tileHeight); //y
      positions.add(Z_POS); //z
      textCoords.add((float) col / (float) numCols);
      textCoords.add((float) (row + 1) / (float) numRows);
      indices.add(i * VERTICES_PER_QUAD + 1);

      // Right Bottom vertex
      positions.add((float) i * tileWidth + tileWidth); // x
      positions.add(tileHeight); //y
      positions.add(Z_POS); //z
      textCoords.add((float) (col + 1) / (float) numCols);
      textCoords.add((float) (row + 1) / (float) numRows);
      indices.add(i * VERTICES_PER_QUAD + 2);

      // Right Top vertex
      positions.add((float) i * tileWidth + tileWidth); // x
      positions.add(0.0f); //y
      positions.add(Z_POS); //z
      textCoords.add((float) (col + 1) / (float) numCols);
      textCoords.add((float) row / (float) numRows);
      indices.add(i * VERTICES_PER_QUAD + 3);

      // Add indices for left top and bottom right vertices
      indices.add(i * VERTICES_PER_QUAD);
      indices.add(i * VERTICES_PER_QUAD + 2);
    }

    final float[] posArr = Utils.listToArray(positions);
    final float[] textCoordsArr = Utils.listToArray(textCoords);
    final int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
    Mesh mesh = new Mesh(posArr, textCoordsArr, normals, indicesArr);
    mesh.setMaterial(new Material(texture));
    return mesh;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
    final Texture texture = this.getMesh().getMaterial().getTexture();
    this.getMesh().deleteBuffers();
    this.setMesh(buildMesh(texture, numCols, numRows));
  }
}
