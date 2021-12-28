package com.akshayAshokCode.textrecognition.presentation;

import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.akshayAshokCode.textrecognition.R;
import com.akshayAshokCode.textrecognition.data.LanguageData;
import com.akshayAshokCode.textrecognition.databinding.FragmentSpeechBinding;
import com.akshayAshokCode.textrecognition.model.LanguageType;
import com.akshayAshokCode.textrecognition.util.PitchAndSpeedManager;
import com.akshayAshokCode.textrecognition.util.TextToSpeechManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Locale;

public class SpeechFragment extends Fragment {
    private TextToSpeech textToSpeech;
    private FragmentSpeechBinding binding;
    private static final String TAG = "SpeechFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSpeechBinding.inflate(inflater);
        LanguageType[] languageTypes = new LanguageData().getLanguageData();
        ArrayAdapter<LanguageType> adapter = new ArrayAdapter<LanguageType>(getContext(), R.layout.support_simple_spinner_dropdown_item, languageTypes);
        binding.spinner.setAdapter(adapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LanguageType languageTypeSelected = adapter.getItem(i);
                final Locale selectedLanguage = languageTypeSelected.getLocale();
                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
                textToSpeech = new TextToSpeechManager().textToSpeech(getContext(), selectedLanguage);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.talk.setOnClickListener(v1 -> {
            if (binding.text.getText().toString().equals("")) {
                Snackbar.make(binding.talk, "No Text Entered", Snackbar.LENGTH_SHORT).show();
            } else
                speak();
        });

        binding.stop.setOnClickListener(view -> {
            if (textToSpeech != null) {
                textToSpeech.stop();
            }
        });

        return binding.getRoot();
    }

    public void speak() {
        float pitch = new PitchAndSpeedManager().getPitch(binding.pitch);
        float speed = new PitchAndSpeedManager().getPitch(binding.speed);
        CharSequence text = binding.text.getText().toString();

        HashMap<String, String> myHashAlarm = new HashMap<String, String>();
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_ALARM));
        textToSpeech.setPitch(pitch);
        textToSpeech.setSpeechRate(speed);
        //textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        // CharSequence charText=text;
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
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
        if (hidden && textToSpeech != null) {
            textToSpeech.stop();
        }
    }
}