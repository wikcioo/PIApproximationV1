import java.awt.*;
import java.awt.image.BufferStrategy;

public class Visualiser implements Runnable {

    private final String title;
    private final int width;
    private final int height;

    private Display display;

    private Thread thread;
    private boolean running = false;

    // Drawing to the screen
    private BufferStrategy bs;
    private Graphics2D g;

    // Input
    private MouseInput mouseInput;

    public Visualiser(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        mouseInput = new MouseInput();
    }

    private void init() {
        display = new Display(title, width, height);
        display.getFrame().addMouseMotionListener(mouseInput);
        display.getCanvas().addMouseMotionListener(mouseInput);
    }

    @Override
    public void run() {
        init();

        double fps = 60d;
        double timePerRender = 1000000000 / fps;
        double delta = 0;
        long timer = System.currentTimeMillis();
        long lastTime = System.nanoTime();
        long now = 0;
        int frames = 0;

        while (running) {
            now = System.nanoTime();
            delta += (now - lastTime) / timePerRender;
            lastTime = now;

            if (delta >= 1) {
                render();
                frames++;
                delta = 0;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                display.getFrame().setTitle("Ray casting | " + frames + " FPS");
                frames = 0;
                timer += 1000;
            }
        }

        stop();
    }

    private void render() {
        bs = display.getCanvas().getBufferStrategy();
        if (bs == null) {
            display.getCanvas().createBufferStrategy(3);
            return;
        }

        g = (Graphics2D) bs.getDrawGraphics();



        g.dispose();
        bs.show();
    }

    public synchronized void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}