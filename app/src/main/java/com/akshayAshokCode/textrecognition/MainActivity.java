package com.akshayAshokCode.textrecognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

// Add image crop feature, Add copy button, add font identifying button
public class MainActivity extends AppCompatActivity {
    Button text, speech;
    FrameLayout layout;
    FragmentManager fragmentManager;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.text);
        speech = findViewById(R.id.speech);
        layout = findViewById(R.id.frame);

        text.setBackground(ContextCompat.getDrawable(this, R.drawable.button));
        text.setTextColor(Color.WHITE);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.frame, new RecognitionFragment(), "TEXT").commit();
        text.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
                speech.setBackground(getDrawable(R.drawable.edittext_shape));
                speech.setTextColor(Color.BLACK);
                text.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.button));
                text.setTextColor(Color.WHITE);
               // fragmentManager = getSupportFragmentManager();

                    fragmentManager.beginTransaction().replace(R.id.frame, new RecognitionFragment(), "TEXT").commit();

            }
        });
        speech.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                text.setBackground(getDrawable(R.drawable.edittext_shape));
                text.setTextColor(Color.BLACK);
                speech.setBackground(getDrawable(R.drawable.button));
                speech.setTextColor(Color.WHITE);
                    fragmentManager.beginTransaction().replace(R.id.frame, new SpeechFragment(), "SPEECH").commit();

            }
        });
    }
}