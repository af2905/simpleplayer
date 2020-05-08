package ru.job4j.simpleplayer;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static String formatTimeFor(long startTime) {
        long min = TimeUnit.MILLISECONDS.toMinutes(startTime);
        long sec = TimeUnit.MILLISECONDS.toSeconds(startTime)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                toMinutes(startTime));
        String timeFor = "%d:%d";
        if (min < 10) {
            timeFor = "0%d:%d";
        }
        if (sec < 10) {
            timeFor = "%d:0%d";
        }
        if (min < 10 && sec < 10) {
            timeFor = "0%d:0%d";
        }
        return String.format(Locale.getDefault(), timeFor, min, sec);
    }
}
