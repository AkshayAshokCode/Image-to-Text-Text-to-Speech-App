package com.akshayAshokCode.textrecognition.presentation.speech;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
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
import com.akshayAshokCode.textrecognition.model.VoiceType;
import com.akshayAshokCode.textrecognition.util.PitchAndSpeedManager;
import com.akshayAshokCode.textrecognition.util.TextToSpeechManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.Set;

public class SpeechFragment extends Fragment {
    private TextToSpeech textToSpeech;
    private FragmentSpeechBinding binding;
    private SpeechViewModel viewModel;
    private static final String TAG = "SpeechFragment";
    private ArrayAdapter<LanguageType> adapter;
    private static final int AUDIO_REQUEST = 202;
    private String[] audioPermission;
    private ArrayAdapter<VoiceType> voiceAdapter;
    private Voice selectedVoice;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSpeechBinding.inflate(inflater);
        viewModel = new ViewModelProvider(this).get(SpeechViewModel.class);

        viewModel.getVoices().observe(getViewLifecycleOwner(), voiceTypes -> {
            if (!voiceTypes.isEmpty()) {
                voiceAdapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, voiceTypes);
                voiceAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                binding.voiceSpinner.setAdapter(voiceAdapter);
                binding.voiceSpinner.setVisibility(View.VISIBLE);
            } else {
                binding.voiceSpinner.setVisibility(View.GONE);
            }
        });

        SpeechViewModel viewModel = new ViewModelProvider(this).get(SpeechViewModel.class);
        viewModel.getLanguages().observe(getViewLifecycleOwner(), languageTypes -> {
            for (int i = 0; i < languageTypes.size(); i++) {
                LanguageType[] languageList = languageTypes.toArray(new LanguageType[i]);
                adapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, languageList);
                adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
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
                textToSpeech = new TextToSpeechManager().textToSpeech(getContext(), selectedLanguage, new TextToSpeechManager.LanguageCallback() {
                    @Override
                    public void onLanguageNotSupported(String languageName) {
                        getActivity().runOnUiThread(() -> {
                            Snackbar.make(binding.talk, "Language Not Supported:" + languageName, Snackbar.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onLanguageSetSuccessfully(String languageName) {
                        loadVVoicesForLanguage(selectedLanguage);

                    }
                });
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
                    InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v1.getApplicationWindowToken(), 0);
                    speak();
                }
            }

        });

        binding.stop.setOnClickListener(view -> {
            if (textToSpeech != null) {
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                if(binding.audioVisualizer != null){
                    binding.audioVisualizer.updateVisualizer(null);
                }
            }
        });
        binding.pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int color = viewModel.getColor(i);
                seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC_IN);
                if (textToSpeech != null) {
                    if (textToSpeech.isSpeaking()) {
                        speak();
                    }
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
                if (textToSpeech != null) {
                    if (textToSpeech.isSpeaking()) {
                        speak();
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.voiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                VoiceType selectedVoiceType = voiceAdapter.getItem(position);
                selectedVoice = selectedVoiceType.getVoice();
                if (textToSpeech != null) {
                    textToSpeech.setVoice(selectedVoice);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
                getActivity().runOnUiThread(() -> {
                    binding.audioVisualizer.requestFocus();
                });
            }

            @Override
            public void onStop(String utteranceId, boolean interrupted) {
                super.onStop(utteranceId, interrupted);
            }

            @Override
            public void onDone(String s) {
                getActivity().runOnUiThread(() -> binding.audioVisualizer.updateVisualizer(null));

            }

            @Override
            public void onError(String s) {

            }

            @Override
            public void onAudioAvailable(String utteranceId, byte[] audio) {
                super.onAudioAvailable(utteranceId, audio);
                getActivity().runOnUiThread(() -> binding.audioVisualizer.updateVisualizer(audio));
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

    private void loadVVoicesForLanguage(Locale locale) {
        if (textToSpeech != null) {
            Set<Voice> voices = textToSpeech.getVoices();
            viewModel.loadVoicesForLanguage(voices, locale);
        }
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