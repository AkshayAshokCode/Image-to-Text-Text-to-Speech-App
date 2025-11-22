package com.akshayAshokCode.textrecognition.presentation.speech;

import android.speech.tts.Voice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.akshayAshokCode.textrecognition.R;
import com.akshayAshokCode.textrecognition.data.LanguageRepository;
import com.akshayAshokCode.textrecognition.model.LanguageType;
import com.akshayAshokCode.textrecognition.model.VoiceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SpeechViewModel extends ViewModel {
    private MutableLiveData<List<LanguageType>> languages;
    private MutableLiveData<List<VoiceType>> voices;

    public LiveData<List<LanguageType>> getLanguages() {
        if (languages == null) {
            languages = new MutableLiveData<>();
            loadLanguages();
        }
        return languages;
    }

    public LiveData<List<VoiceType>> getVoices() {
        if (voices == null) {
            voices = new MutableLiveData<>();
        }
        return voices;
    }

    public void loadVoicesForLanguage(Set<Voice> availableVoices, Locale locale) {
        List<VoiceType> voiceList = new ArrayList<>();
        int voiceCounter = 1;

        for (Voice voice : availableVoices) {
            if (voice.getLocale().getLanguage().equals(locale.getLanguage())) {
                String displayName = "voice " + voiceCounter;
                voiceList.add(new VoiceType(displayName, voice));
                voiceCounter++;
            }
        }

        voices.postValue(voiceList);
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
