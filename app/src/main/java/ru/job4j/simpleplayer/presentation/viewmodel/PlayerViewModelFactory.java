package ru.job4j.simpleplayer.presentation.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PlayerViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    private final Uri uri;
    private Application application;

    public PlayerViewModelFactory(Application application, Uri uri) {
        super(application);
        this.uri = uri;
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == PlayerViewModel.class) {
            return (T) new PlayerViewModel(application, uri);
        }
        return null;
    }
}
