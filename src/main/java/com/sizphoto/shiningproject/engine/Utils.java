package com.sizphoto.shiningproject.engine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

  public static String loadResource(final String fileName) throws Exception {
    String result;
    try (
        final InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(fileName);
        final Scanner scanner = new Scanner(in, "UTF-8")
    ) {
      result = scanner.useDelimiter("\\A").next();
    }
    return result;
  }

  public static List<String> readAllLines(final String fileName) throws Exception {
    List<String> list = new ArrayList<>();
    try (
        final InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(fileName);
        final InputStreamReader isr = new InputStreamReader(in);
        final BufferedReader br = new BufferedReader(isr)
    ) {
      String line;
      while ((line = br.readLine()) != null) {
        list.add(line);
      }
    }
    return list;
  }

  static float[] listToArray(final List<Float> list) {
    final int size = list != null ? list.size() : 0;
    float[] floatArr = new float[size];
    for (int i = 0; i < size; i++) {
      floatArr[i] = list.get(i);
    }
    return floatArr;
  }
}