package ru.orthodox.mbbg.utils;

import javafx.application.Platform;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    public static void runTaskInSeparateThread(Runnable r, String threadName){
        Optional<Thread> previousTrackingThread = Thread.getAllStackTraces().keySet()
                .stream()
                .filter(thread -> thread.getName().contains(threadName))
                .findFirst();
        Thread thread = previousTrackingThread.orElseGet(() -> new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                Platform.runLater(r);
            }
        }));
        thread.setName(threadName);
        thread.setDaemon(true);
        thread.start();
    }

    public static void runTaskInSeparateThread(Supplier<Boolean> booleanProvider, Runnable r){
        Thread thread = new Thread(() -> {
            do {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                Platform.runLater(r);
            } while (booleanProvider.get());
        });
        thread.setDaemon(true);
        thread.start();
    }
}
