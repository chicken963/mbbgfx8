package ru.orthodox.mbbg.utils;

import javafx.application.Platform;

public class ThreadUtils {
    public static void runTaskInSeparateThread(Runnable r){
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                Platform.runLater(r);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
