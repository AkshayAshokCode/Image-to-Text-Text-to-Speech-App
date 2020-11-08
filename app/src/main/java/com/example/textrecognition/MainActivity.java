package com.example.textrecognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

// Add image crop feature, Add copy button, add font identifying button
public class MainActivity extends AppCompatActivity {
    Button text, speech;
    LinearLayout layout;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
          text=findViewById(R.id.text);
          speech=findViewById(R.id.speech);
          layout=findViewById(R.id.frame);

          text.setOnClickListener(new View.OnClickListener() {
              @SuppressLint("UseCompatLoadingForDrawables")
              @Override
              public void onClick(View v) {
                  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                  assert imm != null;
                  imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
                  speech.setBackground(getDrawable(R.drawable.edittext_shape));
                  speech.setTextColor(Color.BLACK);
                  text.setBackground(getDrawable(R.drawable.button));
                  text.setTextColor(Color.WHITE);
                  FragmentManager fragmentManager = getSupportFragmentManager();
                  if (fragmentManager.findFragmentByTag("TEXT")!= null) {
                      fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("TEXT")).commit();
                  } else{
                      fragmentManager.beginTransaction().add(R.id.frame, new recognitionFragment(), "TEXT").commit();
              }if(fragmentManager.findFragmentByTag("SPEECH")!=null) {
                      fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("SPEECH")).commit();

                  }
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
                  FragmentManager fragmentManager=getSupportFragmentManager();
                  if(fragmentManager.findFragmentByTag("SPEECH")!=null) {
                      fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("SPEECH")).commit();
                  }else{
                      fragmentManager.beginTransaction().add(R.id.frame, new SpeechFragment(), "SPEECH").commit();
                  }
                  if(fragmentManager.findFragmentByTag("TEXT")!=null){
                      fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("TEXT")).commit();
                  }
              }
          });
    }
}