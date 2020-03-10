package ru.job4j.simpleplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button play = findViewById(R.id.play);
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            Log.i("Raw Asset: ", field.getName());
            try {
                int resourceID = field.getInt(field);
                media = MediaPlayer.create(MainActivity.this, resourceID);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    media.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    media.pause();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        media.release();
        media = null;
    }
}
