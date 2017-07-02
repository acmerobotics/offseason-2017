package com.acmerobotics.code;

/**
 * @author Ryan
 */

public class Looper extends Thread {

    public interface Loop {
        void onLoop();
    }

    private double timePerLoop;
    private boolean running;
    private Loop loop;

    public Looper(int hz, Loop loop) {
        timePerLoop = 1.0 / hz;
        this.loop = loop;
    }

    public double getTime() {
        return System.nanoTime() / Math.pow(10, 9);
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            double startTime = getTime();
            loop.onLoop();
            while ((getTime() - startTime) < timePerLoop) {
                try {
                    Thread.sleep(0, 250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void terminate() {
        running = false;
    }

}
