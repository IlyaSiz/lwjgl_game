package com.sizphoto.shiningproject.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.sizphoto.shiningproject.utils.Constant.TARGET_FPS;
import static com.sizphoto.shiningproject.utils.Constant.TARGET_UPS;

@Component
public class GameEngine implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameEngine.class);

    private final Window window;

    private final Timer timer;

    private final IGameLogic gameLogic;

    private final MouseInput mouseInput;

    private final Thread gameLoopThread;

    @Autowired
    public GameEngine(final Window window, final Timer timer, final IGameLogic gameLogic,
                      final MouseInput mouseInput) {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        this.window = window;
        this.timer = timer;
        this.gameLogic = gameLogic;
        this.mouseInput = mouseInput;
    }

    public void start() {
        final String osName = System.getProperty("os.name");
        LOGGER.info("start() - Starting on {} operating system", osName);
        if (osName.contains("Mac")) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (final Exception exception) {
            LOGGER.error("run() - Game engine run failed: {}", exception.getMessage());
            exception.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void init() throws Exception {
        window.init();
        timer.init();
        mouseInput.init(window);
        gameLogic.init(window);
    }

    private void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        final float interval = 1f / TARGET_UPS;

        while (!this.window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isVsync()) {
                sync();
            }
        }
    }

    private void sync() {
        final float loopSlot = 1f / TARGET_FPS;
        final double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (final InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    private void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    private void update(final float interval) {
        gameLogic.update(interval, mouseInput);
    }

    private void cleanup() {
        window.release();
        gameLogic.cleanup();
    }

    private void render() {
        gameLogic.render(this.window);
        window.update();
    }
}