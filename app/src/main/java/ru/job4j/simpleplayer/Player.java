package ru.job4j.simpleplayer;

import android.content.Context;
import android.media.MediaPlayer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class Player {
    private Context context;

    Player(Context context) {
        this.context = context.getApplicationContext();
    }

    MediaPlayer createMediaPlayer(int resourceID) {
        return MediaPlayer.create(this.context, resourceID);
    }

    Map<Integer, String> infoFromFields(Field[] fields) {
        Map<Integer, String> map = new HashMap<>();
        for (Field field : fields) {
            try {
                map.put(field.getInt(field), field.getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    String formatTimeFor(long startTime) {
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


