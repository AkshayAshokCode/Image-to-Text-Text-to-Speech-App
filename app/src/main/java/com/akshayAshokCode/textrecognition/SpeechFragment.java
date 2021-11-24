package com.akshayAshokCode.textrecognition;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.akshayAshokCode.textrecognition.util.PitchAndSpeedManager;
import com.akshayAshokCode.textrecognition.util.TextToSpeechManager;

import java.util.Locale;

public class SpeechFragment extends Fragment {
    SeekBar mSpeed, mPitch;
    EditText mText;
    Button talk, stop;
    TextToSpeech textToSpeech;
    private static final String TAG = "SpeechFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_speech, container, false);
        talk = v.findViewById(R.id.talk);
        textToSpeech = new TextToSpeechManager().textToSpeech(getContext());
        mSpeed = v.findViewById(R.id.speed);
        mPitch = v.findViewById(R.id.pitch);
        mText = v.findViewById(R.id.text);
        stop = v.findViewById(R.id.stop);

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
        String text = mText.getText().toString();

        textToSpeech.setPitch(pitch);
        textToSpeech.setSpeechRate(speed);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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
}