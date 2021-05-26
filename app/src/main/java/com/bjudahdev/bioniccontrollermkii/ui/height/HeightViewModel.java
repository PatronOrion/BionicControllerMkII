package com.bjudahdev.bioniccontrollermkii.ui.height;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HeightViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HeightViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}