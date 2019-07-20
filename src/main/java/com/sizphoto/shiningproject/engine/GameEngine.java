package com.sizphoto.shiningproject.engine;

import com.sizphoto.shiningproject.game.IGameLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.sizphoto.shiningproject.utils.Constant.TARGET_FPS;
import static com.sizphoto.shiningproject.utils.Constant.TARGET_UPS;

@Component
public class GameEngine implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameEngine.class);

    private Window window;

    private final Thread gameLoopThread;

    private Timer timer;

    private IGameLogic gameLogic;

    private final MouseInput mouseInput;

    public GameEngine(final Window window, final Timer timer, final IGameLogic gameLogic) {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        this.window = window;
        mouseInput = new MouseInput();
        this.timer = timer;
        this.gameLogic = gameLogic;
    }

    public void start() {
        String osName = System.getProperty("os.name");
        LOGGER.info("start() - Starting on {} operating system", osName);
        if (osName.contains("Mac")) {
            this.gameLoopThread.run();
        } else {
            this.gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            this.init();
            this.gameLoop();
        } catch (Exception exception) {
            LOGGER.error("run() - Game engine run failed: {}", exception.getMessage());
            exception.printStackTrace();
        } finally {
            this.cleanup();
        }
    }

    private void init() throws Exception {
        this.window.init();
        this.timer.init();
        this.mouseInput.init(window);
        this.gameLogic.init(window);
    }

    private void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        while (!this.window.windowShouldClose()) {
            elapsedTime = this.timer.getElapsedTime();
            accumulator += elapsedTime;

            this.input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            this.render();

            if (!this.window.isvSync()) {
                sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = this.timer.getLastLoopTime() + loopSlot;
        while (this.timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    private void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    private void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }

    private void cleanup() {
        this.window.release();
        this.gameLogic.cleanup();
    }

    private void render() {
        this.gameLogic.render(this.window);
        this.window.update();
    }
}
