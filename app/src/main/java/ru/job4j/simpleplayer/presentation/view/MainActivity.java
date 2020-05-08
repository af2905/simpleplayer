package ru.job4j.simpleplayer.presentation.view;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.job4j.simpleplayer.R;
import ru.job4j.simpleplayer.Utils;
import ru.job4j.simpleplayer.presentation.viewmodel.PlayerViewModel;
import ru.job4j.simpleplayer.presentation.viewmodel.PlayerViewModelFactory;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private static MediaPlayer media;
    Disposable disposable;
    private ImageButton play, previous, next;
    private TextView text, time;
    private SeekBar seek;
    private PlayerViewModel model;
    private CheckBox loop;
    private final static String TAG = "log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setListeners();
        checkInputAndCreateMediaPlayer();
        seek.setMax(media.getDuration());
    }

    void findViews() {
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seek = findViewById(R.id.seek);
        text = findViewById(R.id.text);
        time = findViewById(R.id.time);
        loop = findViewById(R.id.loop);
        seek.setEnabled(false);
    }

    void setListeners() {
        play.setOnClickListener(this);
        loop.setOnCheckedChangeListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        seek.setOnSeekBarChangeListener(this);
    }

    void checkInputAndCreateMediaPlayer() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Uri uri = intent.getData();
        model = ViewModelProviders.of(
                this, new PlayerViewModelFactory(getApplication(), uri))
                .get(PlayerViewModel.class);
        if (Intent.ACTION_VIEW.equals(action) && type != null) {
            if ("audio/*".equals(type)) {
                media = model.isReceivedARequestToPlay();
            }
        } else {
            model.getSongsLiveData();
            media = model.isRegularPlayerForSongs();
        }
        if (Objects.requireNonNull(model.getTrackNames().getValue()).size() == 0) {
            MutableLiveData<String> name = new MutableLiveData<>();
            name.setValue(null);
            model.setCurrentTrackName(name);
        }
        LiveData<String> currentName = model.getCurrentTrackName();
        LiveData<Integer> currentTime = model.getCurrentTime();
        LiveData<Boolean> isPlaying = model.getIsPlaying();
        observeChanges(currentName, currentTime, isPlaying);
    }

    void observeChanges(LiveData<String> currentName,
                        LiveData<Integer> currentTime,
                        LiveData<Boolean> isPlaying) {
        currentName.observe(this, s -> text.setText(s));
        currentTime.observe(this, integer -> {
            if (integer != 0) {
                time.setText(Utils.formatTimeFor(integer));
                seek.setEnabled(true);
                seek.setProgress(integer);
                media.seekTo(integer);
            }
            Log.d(TAG, "currentTrackTime: " + integer);
        });
        isPlaying.observe(this, aBoolean -> {
            if (aBoolean) {
                play.setImageResource(R.drawable.ic_pause);
                media.start();
                updateTime();
            }
        });
    }

    @Override
    public void onClick(View v) {
        MutableLiveData<Integer> changedPosition = new MutableLiveData<>();
        MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();
        int position = 0;
        if (Objects.requireNonNull(model.getTrackNames().getValue()).size() != 0) {
            position = model.getPosition().getValue();
        } else {
            previous.setEnabled(false);
            next.setEnabled(false);
        }
        switch (v.getId()) {
            case R.id.play:
                seek.setClickable(true);
                seek.setEnabled(true);
                if (model.getTrackNames().getValue().size() != 0) {
                    text.setText(model.getCurrentTrackName().getValue());
                }
                if (media.isPlaying()) {
                    play.setImageResource(R.drawable.ic_play);
                    media.pause();
                    isPlaying.setValue(false);
                    model.setIsPlaying(isPlaying);
                    disposable.dispose();
                } else {
                    play.setImageResource(R.drawable.ic_pause);
                    media.start();
                    isPlaying.setValue(true);
                    model.setIsPlaying(isPlaying);
                    updateTime();
                }
                break;
            case R.id.next:
                previous.setEnabled(true);
                if (position == model.getTrackNames().getValue().size() - 1) {
                    next.setEnabled(false);
                    Toast.makeText(getApplicationContext(),
                            R.string.last_track, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    checkedNextOrPreviousByDefault(changedPosition, isPlaying, ++position);
                }
                break;
            case R.id.previous:
                if (position == 0) {
                    previous.setEnabled(false);
                    return;
                } else {
                    next.setEnabled(true);
                    checkedNextOrPreviousByDefault(changedPosition, isPlaying, --position);
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
            updateTime();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void updateTime() {
        disposable = Observable
                .interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> {
                    int currentTime = media.getCurrentPosition();
                    String formatT = Utils.formatTimeFor(currentTime + v.intValue());
                    time.setText(formatT);
                    seek.setProgress(currentTime + v.intValue());
                    if (currentTime != 0) {
                        model.updateSongTime(currentTime);
                    }
                    Log.d(TAG, "formatT: " + formatT + "currentTrackTime: " + currentTime);
                });
    }

    void checkedNextOrPreviousByDefault(MutableLiveData<Integer> changedPosition,
                                        MutableLiveData<Boolean> isPlaying,
                                        int position) {
        media.release();
        if (disposable != null) {
            disposable.dispose();
        }
        changedPosition.setValue(position);
        model.setPosition(changedPosition);
        media = model.isRegularPlayerForSongs();
        seek.setProgress(media.getCurrentPosition());
        media.start();
        isPlaying.setValue(true);
        model.setIsPlaying(isPlaying);
        updateTime();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (media != null) {
            media.setLooping(isChecked);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.disposable != null) {
            this.disposable.dispose();
        }
        if (media != null) {
            media.release();
            media = null;
        }
    }
}



