package com.sizphoto.shiningproject.engine;

import com.sizphoto.shiningproject.engine.graph.Material;
import com.sizphoto.shiningproject.engine.graph.Mesh;
import com.sizphoto.shiningproject.engine.graph.ObjLoader;
import com.sizphoto.shiningproject.engine.graph.Texture;

public class SkyBox extends GameItem {

  public SkyBox(final String objModel, final String textureFile) throws Exception {
    super();
    Mesh skyBoxMesh = ObjLoader.loadMesh(objModel);
    final Texture skyBoxTexture = new Texture(textureFile);
    skyBoxMesh.setMaterial(new Material(skyBoxTexture, 0.0f));
    setMesh(skyBoxMesh);
    setPosition(0, 0, 0);
  }
}
