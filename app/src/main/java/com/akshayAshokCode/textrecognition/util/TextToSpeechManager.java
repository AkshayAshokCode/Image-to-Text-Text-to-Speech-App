package com.akshayAshokCode.textrecognition.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechManager {
    TextToSpeech textToSpeechInstance;

    public TextToSpeech textToSpeech(Context context){
        textToSpeechInstance= new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    int result= textToSpeechInstance.setLanguage(Locale.ENGLISH);
                    if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TAG","Language not supported");
                    }else{
                       // talk.setEnabled(true);
                    }
                }else{
                    Log.e("TAG","Failed");
                }
            }
        });
        return textToSpeechInstance;
    }
}
