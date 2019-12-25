package com.sizphoto.shiningproject.engine.graph;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

class Texture {

  private final int id;

  private Texture(final String fileName) throws Exception {
    this(loadTexture(fileName));
  }

  private Texture(final int id) {
    this.id = id;
  }

  public void bind() {
    glBindTexture(GL_TEXTURE_2D, id);
  }

  int getId() {
    return id;
  }

  private static int loadTexture(final String fileName) throws Exception {
    int width;
    int height;
    ByteBuffer buf;
    // Load Texture file
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final IntBuffer w = stack.mallocInt(1);
      final IntBuffer h = stack.mallocInt(1);
      final IntBuffer channels = stack.mallocInt(1);

      final URL url = Texture.class.getResource(fileName);
      final File file = Paths.get(url.toURI()).toFile();
      final String filePath = file.getAbsolutePath();
      buf = stbi_load(filePath, w, h, channels, 4);
      if (buf == null) {
        throw new Exception("Image file [" + filePath + "] not loaded: " + stbi_failure_reason());
      }

      /* Get width and height of image */
      width = w.get();
      height = h.get();
    }

    // Create a new OpenGL texture
    final int textureId = glGenTextures();
    // Bind the texture
    glBindTexture(GL_TEXTURE_2D, textureId);

    // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

    // Filtering refers to how the image will be drawn when scaling and how pixels will be interpolated.
    // If filtering parameters are not set the texture will not be displayed.
    // These parameters basically say that when a pixel is drawn with no direct one to one association to
    // a texture coordinate it will pick the nearest texture coordinate point.
    // Instead we will generate a mipmap. A mipmap is a decreasing resolution set of images generated from
    // a high detailed texture. These lower resolution images will be used automatically when our object is scaled.
    // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    // Upload the texture data
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA,
        GL_UNSIGNED_BYTE, buf);

    // Generate Mip Map
    glGenerateMipmap(GL_TEXTURE_2D);

    stbi_image_free(buf);

    return textureId;
  }

  void cleanup() {
    glDeleteTextures(id);
  }
}