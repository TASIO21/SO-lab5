package com.example.solab5;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

public class BrowserTimer {
    public static void main(String[] args) {
        String[] urls = {
                "https://www.google.com",
                "https://www.github.com",
                "https://www.stackoverflow.com"
        };

        Instant startTime = Instant.now(); // Засекаем время старта

        try {
            for (String url : urls) {
                System.out.println("Открываю: " + url);
                new ProcessBuilder("cmd", "/c", "start msedge " + url).start();
                Thread.sleep(1000); // Задержка между вкладками
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Все вкладки открыты в Microsoft Edge. Запускаем таймер...");

        // Таймер на сколько нужно минут
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                closeBrowser(startTime);
            }
        }, 1 * 60 * 1000); // 5 минут
    }

    private static void closeBrowser(Instant startTime) {
        Instant endTime = Instant.now(); // Засекаем время закрытия
        Duration uptime = Duration.between(startTime, endTime);
        long minutes = uptime.toMinutes();
        long seconds = uptime.minusMinutes(minutes).toSeconds();

        System.out.println("Браузер работал " + minutes + " минут и " + seconds + " секунд.");
        System.out.println("Закрываю Microsoft Edge...");

        try {
            new ProcessBuilder("taskkill", "/IM", "msedge.exe", "/F").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
