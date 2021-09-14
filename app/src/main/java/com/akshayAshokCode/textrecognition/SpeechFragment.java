package com.akshayAshokCode.textrecognition;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.akshayAshokCode.textrecognition.R;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeechFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeechFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SpeechFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpeechFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeechFragment newInstance(String param1, String param2) {
        SpeechFragment fragment = new SpeechFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    SeekBar mSpeed, mPitch;
    EditText mText;
    Button talk;
    TextToSpeech textToSpeech;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_speech, container, false);
        talk=v.findViewById(R.id.talk);
        textToSpeech= new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    int result= textToSpeech.setLanguage(Locale.ENGLISH);
                    if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TAG","Language not supported");
                    }else{
                        talk.setEnabled(true);
                    }
                }else{
                    Log.e("TAG","Failed");
                }
            }
        });
        mSpeed=v.findViewById(R.id.speed);
        mPitch=v.findViewById(R.id.pitch);
        mText=v.findViewById(R.id.text);

        talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(mText.getText().toString().equals("")){
                   Toast.makeText(getContext(), "No Text Entered", Toast.LENGTH_SHORT).show();
               }else
                   speak();
            }
        });
    return  v;
    }
    public void speak(){
        String text= mText.getText().toString();
        float pitch=(float)mPitch.getProgress()/50;
        if(pitch<0.1) pitch=0.1f;
        float speed=(float)mSpeed.getProgress()/50;
        if(speed<0.1) speed=0.1f;
        textToSpeech.setPitch(pitch);
        textToSpeech.setSpeechRate(speed);
        textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    public void onDestroy() {
        if(textToSpeech!=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}