package com.akshayAshokCode.textrecognition.presentation;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.akshayAshokCode.textrecognition.databinding.ActivityMainBinding;
import com.akshayAshokCode.textrecognition.presentation.adapter.FragmentAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

// Add image crop feature, Add copy button, add font identifying button
public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    private static final String TAG = "MainActivity";
    private final int REQUEST_CODE = 11;
    private ActivityMainBinding binding;
    private AppUpdateManager appUpdateManager;

    @Override
    protected void onStart() {
        super.onStart();
        checkUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackBarForCompleteUpdate();
                } else {
                    Log.d(TAG, "State of update:" + appUpdateInfo.installStatus());
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();
        FragmentAdapter fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle());
        binding.pager.setAdapter(fragmentAdapter);

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Recognize Text"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Text to Speech"));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });

    }

    private void checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            this,
                            REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
        appUpdateManager.registerListener(listener);
    }
    // Create a listener to track request state updates.
    InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackBarForCompleteUpdate();
        }
        // Log state or install the update.
        Log.d(TAG, "State of update:"+state.installStatus());
    };
    // Displays the snackBar notification and call to action.
    private void popupSnackBarForCompleteUpdate() {
        Snackbar snackbar= Snackbar.make(
                findViewById(android.R.id.content),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: Updated to Latest Features");
            if (resultCode != RESULT_OK) {
                Log.e(TAG, "Update Failed, please Go to Google Play Store & update.");
            }
        }
    }

    @Override
    protected void onStop() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(listener);
        }
        super.onStop();

    }
}