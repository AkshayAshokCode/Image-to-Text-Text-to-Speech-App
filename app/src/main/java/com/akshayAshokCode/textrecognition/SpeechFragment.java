package com.akshayAshokCode.textrecognition;

import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.akshayAshokCode.textrecognition.data.LanguageData;
import com.akshayAshokCode.textrecognition.model.LanguageType;
import com.akshayAshokCode.textrecognition.util.PitchAndSpeedManager;
import com.akshayAshokCode.textrecognition.util.TextToSpeechManager;

import java.util.HashMap;
import java.util.Locale;

public class SpeechFragment extends Fragment {
    SeekBar mSpeed, mPitch;
    EditText mText;
    Button talk, stop;
    TextToSpeech textToSpeech;
    AppCompatSpinner spinner;
    private static final String TAG = "SpeechFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_speech, container, false);
        talk = v.findViewById(R.id.talk);
        mSpeed = v.findViewById(R.id.speed);
        mPitch = v.findViewById(R.id.pitch);
        mText = v.findViewById(R.id.text);
        stop = v.findViewById(R.id.stop);
        spinner=v.findViewById(R.id.spinner);
        LanguageType[] languageTypes= new LanguageData().getLanguageData();
        ArrayAdapter<LanguageType> adapter=new ArrayAdapter<LanguageType>(getContext(),R.layout.support_simple_spinner_dropdown_item,languageTypes);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LanguageType languageTypeSelected=adapter.getItem(i);
                final Locale selectedLanguage = languageTypeSelected.getLocale();
                if(textToSpeech!=null){
                    textToSpeech.stop();
                }
                textToSpeech = new TextToSpeechManager().textToSpeech(getContext(), selectedLanguage);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        talk.setOnClickListener(v1 -> {
            if (mText.getText().toString().equals("")) {
                Toast.makeText(getContext(), "No Text Entered", Toast.LENGTH_SHORT).show();
            } else
                speak();
        });

        stop.setOnClickListener(view -> {
            if (textToSpeech != null) {
                textToSpeech.stop();
            }
        });

        return v;
    }

    public void speak() {
        float pitch = new PitchAndSpeedManager().getPitch(mPitch);
        float speed = new PitchAndSpeedManager().getPitch(mSpeed);
        CharSequence text = mText.getText().toString();

        HashMap<String, String> myHashAlarm = new HashMap<String, String>();
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_ALARM));
        textToSpeech.setPitch(pitch);
        textToSpeech.setSpeechRate(speed);
        //textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
       // CharSequence charText=text;
        textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null,TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }


    @Override
    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden && textToSpeech != null){
            textToSpeech.stop();
        }
    }
}