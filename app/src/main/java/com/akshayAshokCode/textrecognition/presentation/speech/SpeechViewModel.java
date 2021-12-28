package com.akshayAshokCode.textrecognition.presentation.speech;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.akshayAshokCode.textrecognition.data.LanguageRepository;
import com.akshayAshokCode.textrecognition.model.LanguageType;

import java.util.List;

public class SpeechViewModel extends ViewModel {
    private MutableLiveData<List<LanguageType>> languages;
    public LiveData<List<LanguageType>> getLanguages(){
        if(languages==null){
            languages= new MutableLiveData<>();
            loadLanguages();
        }
        return languages;
    }
    private void loadLanguages(){
        List<LanguageType> languageTypeList=new LanguageRepository().getLanguageData();
        if(languageTypeList!=null){
            languages.postValue(languageTypeList);
        }
    }
}
