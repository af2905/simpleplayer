package ru.job4j.simpleplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private Player player;
    private static MediaPlayer media;
    private ImageButton play, previous, next;
    private TextView text, time;
    private SeekBar seek;
    private CheckBox loop;
    private Map<Integer, String> map;
    private List<Integer> fieldsIds;
    private List<String> trackNames;
    int position = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = findViewById(R.id.play);
        play.setOnClickListener(this);
        previous = findViewById(R.id.previous);
        previous.setOnClickListener(this);
        previous.setEnabled(false);
        next = findViewById(R.id.next);
        next.setOnClickListener(this);
        seek = findViewById(R.id.seek);
        seek.setEnabled(false);
        text = findViewById(R.id.text);
        time = findViewById(R.id.time);
        loop = findViewById(R.id.loop);
        loop.setOnCheckedChangeListener(this);
        player = new Player(getApplicationContext());
        map = player.infoFromFields(R.raw.class.getFields());
        fieldsIds = new ArrayList<>(map.keySet());
        trackNames = new ArrayList<>(map.values());
        media = player.createMediaPlayer(fieldsIds.get(position));
        seek.setMax(media.getDuration());
        seek.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        media.release();
        media = null;
    }

    @Override
    public void onClick(View v) {
        handler.postDelayed(updateSongTime, 100);
        switch (v.getId()) {
            case R.id.play:
                text.setText(trackNames.get(position));
                seek.setClickable(true);
                seek.setEnabled(true);
                if (media.isPlaying()) {
                    play.setImageResource(R.drawable.ic_play);
                    media.pause();
                } else {
                    play.setImageResource(R.drawable.ic_pause);
                    media.start();
                }
                break;
            case R.id.previous:
                next.setEnabled(true);
                if (position == 0) {
                    previous.setEnabled(false);
                    return;
                } else {
                    media.release();
                    position--;
                    text.setText(trackNames.get(position));
                    media = player.createMediaPlayer(fieldsIds.get(position));
                    seek.setProgress(media.getCurrentPosition());
                    media.start();
                }
                break;
            case R.id.next:
                previous.setEnabled(true);
                if (position == fieldsIds.size() - 1) {
                    next.setEnabled(false);
                    Toast.makeText(getApplicationContext(),
                            "This is the last audio track", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    media.release();
                    position++;
                    text.setText(trackNames.get(position));
                    media = player.createMediaPlayer(fieldsIds.get(position));
                    seek.setProgress(media.getCurrentPosition());
                    media.start();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            media.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private Runnable updateSongTime = new Runnable() {
        public void run() {
            int startTime = media.getCurrentPosition();
            String formatT = player.formatTimeFor(startTime);
            time.setText(formatT);
            seek.setProgress(startTime);
            handler.postDelayed(this, 100);
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (media != null) {
            media.setLooping(isChecked);
        }
    }
}



