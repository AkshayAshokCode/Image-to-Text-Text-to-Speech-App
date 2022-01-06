package com.akshayAshokCode.textrecognition.presentation.speech;

import android.graphics.PorterDuff;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.akshayAshokCode.textrecognition.R;
import com.akshayAshokCode.textrecognition.data.LanguageRepository;
import com.akshayAshokCode.textrecognition.model.LanguageType;

import java.util.List;

public class SpeechViewModel extends ViewModel {
    private MutableLiveData<List<LanguageType>> languages;

    public LiveData<List<LanguageType>> getLanguages() {
        if (languages == null) {
            languages = new MutableLiveData<>();
            loadLanguages();
        }
        return languages;
    }

    public int getColor(int i) {
        int color;
        if (i <= 30) {
            color = R.color.seekbar_low;
        } else if (i <= 70) {
            color = R.color.seekbar_mid;
        } else {
            color = R.color.seekbar_high;
        }
        return color;
    }

    private void loadLanguages() {
        List<LanguageType> languageTypeList = new LanguageRepository().getLanguageData();
        if (languageTypeList != null) {
            languages.postValue(languageTypeList);
        }
    }
}
