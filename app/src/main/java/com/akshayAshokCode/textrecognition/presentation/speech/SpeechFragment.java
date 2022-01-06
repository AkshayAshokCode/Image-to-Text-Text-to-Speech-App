package com.akshayAshokCode.textrecognition.presentation.speech;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.akshayAshokCode.textrecognition.R;
import com.akshayAshokCode.textrecognition.databinding.FragmentSpeechBinding;
import com.akshayAshokCode.textrecognition.model.LanguageType;
import com.akshayAshokCode.textrecognition.util.PitchAndSpeedManager;
import com.akshayAshokCode.textrecognition.util.TextToSpeechManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class SpeechFragment extends Fragment {
    private TextToSpeech textToSpeech;
    private FragmentSpeechBinding binding;
    private static final String TAG = "SpeechFragment";
    private ArrayAdapter<LanguageType> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSpeechBinding.inflate(inflater);
        SpeechViewModel viewModel = new ViewModelProvider(this).get(SpeechViewModel.class);
        viewModel.getLanguages().observe(getViewLifecycleOwner(), languageTypes -> {
            for (int i = 0; i < languageTypes.size(); i++) {
                LanguageType[] languageList = languageTypes.toArray(new LanguageType[i]);
                adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, languageList);
                binding.spinner.setAdapter(adapter);
            }
        });

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
                if(textToSpeech.isSpeaking()){
                    textToSpeech.stop();
                }else{
                    Snackbar.make(binding.talk, "Not Talking", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        binding.pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i <= 30) {
                    seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(),R.color.seekbar_low), PorterDuff.Mode.SRC_IN);
                    seekBar.getThumb().setColorFilter(ContextCompat.getColor(getContext(),R.color.seekbar_low), PorterDuff.Mode.SRC_IN);
                } else if (i <= 70) {
                    seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(),R.color.seekbar_mid), PorterDuff.Mode.SRC_IN);
                    seekBar.getThumb().setColorFilter(ContextCompat.getColor(getContext(),R.color.seekbar_mid), PorterDuff.Mode.SRC_IN);
                } else {
                    seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(),R.color.seekbar_high), PorterDuff.Mode.SRC_IN);
                    seekBar.getThumb().setColorFilter(ContextCompat.getColor(getContext(),R.color.seekbar_high), PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i <= 30) {
                    seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(),R.color.seekbar_low), PorterDuff.Mode.SRC_IN);
                    seekBar.getThumb().setColorFilter(ContextCompat.getColor(getContext(),R.color.seekbar_low), PorterDuff.Mode.SRC_IN);
                } else if (i <= 70) {
                    seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(),R.color.seekbar_mid), PorterDuff.Mode.SRC_IN);
                    seekBar.getThumb().setColorFilter(ContextCompat.getColor(getContext(),R.color.seekbar_mid), PorterDuff.Mode.SRC_IN);
                } else {
                    seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(),R.color.seekbar_high), PorterDuff.Mode.SRC_IN);
                    seekBar.getThumb().setColorFilter(ContextCompat.getColor(getContext(),R.color.seekbar_high), PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return binding.getRoot();
    }

    public void speak() {
        float pitch = new PitchAndSpeedManager().getPitch(binding.pitch);
        float speed = new PitchAndSpeedManager().getPitch(binding.speed);
        CharSequence text = binding.text.getText().toString();


        textToSpeech.setPitch(pitch);
        textToSpeech.setSpeechRate(speed);

       /* HashMap<String, String> myHashAlarm = new HashMap<String, String>();
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_ALARM));
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
         CharSequence charText=text;
        */
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