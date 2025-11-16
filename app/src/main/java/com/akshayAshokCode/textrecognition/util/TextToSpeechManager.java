package com.akshayAshokCode.textrecognition.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.Locale;
import java.util.Set;

public class TextToSpeechManager {
    private TextToSpeech textToSpeechInstance;
    private final String TAG="TextToSpeechManager";

    public interface LanguageCallback {
        void onLanguageNotSupported(String languageName);
        void onLanguageSetSuccessfully(String languageName);
    }

    public TextToSpeech textToSpeech(Context context,Locale lang, LanguageCallback callback){
        textToSpeechInstance= new TextToSpeech(context, status -> {
            if(status==TextToSpeech.SUCCESS){
                int result= textToSpeechInstance.setLanguage(lang);
                if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                    Log.e(TAG,"Language not supported");
                    if(callback!=null){
                        callback.onLanguageNotSupported(lang.getDisplayName());
                    }
                }else{
                    Log.e(TAG,"Language set successfully");
                    if(callback!=null){
                        callback.onLanguageSetSuccessfully(lang.getDisplayName());
                    }
/*
                    // To get voiceSet
                    Set<Voice> voiceSet=textToSpeechInstance.getVoices();
                    Voice[] voices = voiceSet.toArray(new Voice[voiceSet.size()]);
                    for (Voice voice:voices){
                        Log.d(TAG,"voice:"+voice);
                    }

                    // To get languageSet
                    Set<Locale> languageSet=textToSpeechInstance.getAvailableLanguages();
                    Locale[] locales = languageSet.toArray(new Locale[languageSet.size()]);
                    for(Locale locale:locales){
                        Log.d(TAG,"Language;"+locale);
                    }
*/
                }
            }else{
                Log.e(TAG,"Failed to initialize TextToSpeech");
            }
        });
        return textToSpeechInstance;
    }
}
