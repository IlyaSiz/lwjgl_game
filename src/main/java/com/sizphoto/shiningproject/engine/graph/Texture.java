package com.sizphoto.shiningproject.engine.graph;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;
import org.springframework.util.ResourceUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

  private final int id;

  private final int width;

  private final int height;

  public Texture(final String fileName) throws Exception {

    ByteBuffer buf;

    // Load Texture file
    try (MemoryStack stack = MemoryStack.stackPush()) {
      final IntBuffer w = stack.mallocInt(1);
      final IntBuffer h = stack.mallocInt(1);
      final IntBuffer channels = stack.mallocInt(1);

      final String fileLocation = String.format("file:%s", fileName);
      final File file = ResourceUtils.getFile(fileLocation);
      final String filePath = file.getAbsolutePath();

      buf = stbi_load(filePath, w, h, channels, 4);
      if (buf == null) {
        throw new Exception("Image file [" + filePath + "] not loaded: " + stbi_failure_reason());
      }

      /* Set width and height of image */
      this.width = w.get();
      this.height = h.get();
    }

    this.id = createTexture(buf);

    stbi_image_free(buf);
  }

  Texture(final ByteBuffer imageBuffer) throws Exception {

    ByteBuffer buf;

    // Load Texture file
    try (MemoryStack stack = MemoryStack.stackPush()) {
      final IntBuffer w = stack.mallocInt(1);
      final IntBuffer h = stack.mallocInt(1);
      final IntBuffer channels = stack.mallocInt(1);

      buf = stbi_load_from_memory(imageBuffer, w, h, channels, 4);
      if (buf == null) {
        throw new Exception("Image file not loaded: " + stbi_failure_reason());
      }

      this.width = w.get();
      this.height = h.get();
    }

    this.id = createTexture(buf);

    stbi_image_free(buf);
  }

  public void bind() {
    glBindTexture(GL_TEXTURE_2D, id);
  }

  void cleanup() {
    glDeleteTextures(id);
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  int getId() {
    return id;
  }

  private int createTexture(final ByteBuffer buf) {

    // Create a new OpenGL texture
    final int textureId = glGenTextures();
    // Bind the texture
    glBindTexture(GL_TEXTURE_2D, textureId);

    // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

    // smoothing
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    // Upload the texture data
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA,
        GL_UNSIGNED_BYTE, buf);

    // Generate Mip Map
    glGenerateMipmap(GL_TEXTURE_2D);

    return textureId;
  }
}