package ru.job4j.simpleplayer.data;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class Player extends MediaPlayer {
    private Context context;

    public Player(Context context) {
        this.context = context.getApplicationContext();
    }

    public MediaPlayer createMediaPlayer(int resourceID) {
        return MediaPlayer.create(this.context, resourceID);
    }

    public MediaPlayer createMediaPlayer(Uri uri) {
        return MediaPlayer.create(this.context, uri);
    }
}


