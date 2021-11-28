package com.akshayAshokCode.textrecognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

// Add image crop feature, Add copy button, add font identifying button
public class MainActivity extends AppCompatActivity {
    Button text, speech;
    FrameLayout layout;
    FragmentManager fragmentManager;
    private static final String TAG = "MainActivity";
    private final int REQUEST_CODE=11;

    @Override
    protected void onStart() {
        super.onStart();
        checkUpdate();
    }

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
    private void checkUpdate(){
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,this,REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            Log.d(TAG, "onActivityResult: Updated to Latest Features");
            if(resultCode!=RESULT_OK){
                Log.e(TAG,"Update Failed, please Go to Google Play Store & update.");
            }
        }
    }

}