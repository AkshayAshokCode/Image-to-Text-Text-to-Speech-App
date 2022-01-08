package com.akshayAshokCode.textrecognition.presentation.speech;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
    private static final int AUDIO_REQUEST = 202;
    private String[] audioPermission;

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
        audioPermission = new String[]{Manifest.permission.RECORD_AUDIO};

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
            } else {
                if (!checkAudioPermission()) {
                    requestAudioPermission();
                } else {
                    InputMethodManager inputMethodManager = (InputMethodManager)requireActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v1.getApplicationWindowToken(),0);
                    speak();
                }
            }

        });

        binding.stop.setOnClickListener(view -> {
            if (textToSpeech != null) {
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                } else {
                    Snackbar.make(binding.talk, "Not Talking", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        binding.pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int color = viewModel.getColor(i);
                seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC_IN);
                if (textToSpeech.isSpeaking()) {
                    speak();
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
                int color = viewModel.getColor(i);
                seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC_IN);
                if (textToSpeech.isSpeaking()) {
                    speak();
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

    private void speak() {
        float pitch = getPitch();
        float speed = getSpeed();
        CharSequence text = binding.text.getText().toString();

        textToSpeech.setPitch(pitch);
        textToSpeech.setSpeechRate(speed);

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        binding.audioVisualizer.setVisibility(View.VISIBLE);
                        binding.audioVisualizer.requestFocus();
                    }
                });
            }
            @Override
            public void onStop(String utteranceId, boolean interrupted) {
                super.onStop(utteranceId, interrupted);
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        binding.audioVisualizer.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onDone(String s) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        binding.audioVisualizer.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onError(String s) {

            }

            @Override
            public void onAudioAvailable(String utteranceId, byte[] audio) {
                super.onAudioAvailable(utteranceId, audio);
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        binding.audioVisualizer.updateVisualizer(audio);
                    }
                });
            }
        });


        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
    }

    private Boolean checkAudioPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestAudioPermission() {
        requestPermissions(audioPermission, AUDIO_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AUDIO_REQUEST) {
            if (grantResults.length > 0) {
                boolean audioAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (audioAccepted) {
                    speak();
                } else {
                    Snackbar.make(binding.talk, "Please enable record audio permissions", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }


    private float getPitch() {
        return new PitchAndSpeedManager().getPitch(binding.pitch);
    }

    private float getSpeed() {
        return new PitchAndSpeedManager().getSpeed(binding.speed);
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