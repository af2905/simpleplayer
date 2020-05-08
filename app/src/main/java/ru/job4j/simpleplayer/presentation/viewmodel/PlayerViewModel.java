package ru.job4j.simpleplayer.presentation.viewmodel;

import android.app.Application;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.job4j.simpleplayer.data.Player;
import ru.job4j.simpleplayer.data.SongsRepository;

public class PlayerViewModel extends AndroidViewModel {
    private final static String TAG = "log";
    private Player player = new Player(getApplication());
    private MutableLiveData<MediaPlayer> media = new MutableLiveData<>();
    private MutableLiveData<Uri> uriLiveData = new MutableLiveData<>();
    private MutableLiveData<Map<Integer, String>> songsLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> position = new MutableLiveData<>();
    private MutableLiveData<List<Integer>> fieldsIds = new MutableLiveData<>();
    private MutableLiveData<List<String>> trackNames = new MutableLiveData<>();
    private MutableLiveData<Integer> currentTrackId = new MutableLiveData<>();
    private MutableLiveData<String> currentTrackName = new MutableLiveData<>();
    private MutableLiveData<Integer> currentTime = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();

    PlayerViewModel(Application application, Uri uri) {
        super(application);
        this.uriLiveData.setValue(uri);
        this.position.setValue(0);
    }

    public MediaPlayer isReceivedARequestToPlay() {
        List<String> list = new ArrayList<>();
        trackNames.setValue(list);
        media.setValue(player.createMediaPlayer(uriLiveData.getValue()));
        return player.createMediaPlayer(uriLiveData.getValue());
    }

    public MediaPlayer isRegularPlayerForSongs() {
        fieldsIds.setValue(new ArrayList<>(Objects.requireNonNull(songsLiveData.getValue()).keySet()));
        trackNames.setValue(new ArrayList<>(songsLiveData.getValue().values()));
        currentTrackId.setValue(Objects.requireNonNull(fieldsIds.getValue()).get(position.getValue()));
        currentTrackName.setValue(Objects.requireNonNull(trackNames.getValue()).get(position.getValue()));
        media.setValue(player.createMediaPlayer(currentTrackId.getValue()));
        return player.createMediaPlayer(currentTrackId.getValue());
    }

    public LiveData<Map<Integer, String>> getSongsLiveData() {
        songsLiveData.setValue(
                SongsRepository.getInstance().getDataFromFields(SongsRepository.RAW_FIELDS));
        return songsLiveData;
    }

    public LiveData<Integer> getPosition() {
        return position;
    }

    public void setPosition(MutableLiveData<Integer> position) {
        this.position = position;
    }

    public LiveData<String> getCurrentTrackName() {
        return currentTrackName;
    }

    public MutableLiveData<List<String>> getTrackNames() {
        return trackNames;
    }

    public void updateSongTime(int currentTime) {
        Log.d(TAG, "this is a currentTime from outside: " + currentTime);
        MutableLiveData<Integer> time = new MutableLiveData<>();
        time.setValue(currentTime);
        setCurrentTime(time);
        Log.d(TAG, "ViewModel time: " + time.getValue());
        Log.d(TAG, "ViewModelCurrentTime: " + getCurrentTime().getValue());
    }

    public LiveData<Integer> getCurrentTime() {
        return currentTime;
    }

    private void setCurrentTime(MutableLiveData<Integer> currentTime) {
        this.currentTime = currentTime;
    }

    public void setCurrentTrackName(MutableLiveData<String> currentTrackName) {
        this.currentTrackName = currentTrackName;
    }

    public LiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(MutableLiveData<Boolean> isPlaying) {
        this.isPlaying = isPlaying;
    }
}