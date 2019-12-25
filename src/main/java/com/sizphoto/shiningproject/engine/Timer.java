package com.sizphoto.shiningproject.engine;

import org.springframework.stereotype.Component;

@Component
public class Timer {

  private double lastLoopTime;

  void init() {
    lastLoopTime = getTime();
  }

  double getTime() {
    return System.nanoTime() / 1000_000_000.0;
  }

  float getElapsedTime() {
    double time = getTime();
    float elapsedTime = (float) (time - lastLoopTime);
    lastLoopTime = time;
    return elapsedTime;
  }

  double getLastLoopTime() {
    return lastLoopTime;
  }
}