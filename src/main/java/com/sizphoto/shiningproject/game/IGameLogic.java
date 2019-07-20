package com.sizphoto.shiningproject.game;

import com.sizphoto.shiningproject.engine.MouseInput;
import com.sizphoto.shiningproject.engine.Window;
import org.springframework.stereotype.Component;

@Component
public interface IGameLogic {

    void init(Window window) throws Exception;

    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput);

    void render(Window window);

    void cleanup();
}