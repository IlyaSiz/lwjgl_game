package com.sizphoto.shiningproject.game;

import com.sizphoto.shiningproject.engine.Window;
import org.springframework.stereotype.Component;

@Component
public interface IGameLogic {

    void init() throws Exception;

    void input(Window window);

    void update(float interval);

    void render(Window window);

    void cleanup();
}
