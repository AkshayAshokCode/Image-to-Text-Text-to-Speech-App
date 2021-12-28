package com.akshayAshokCode.textrecognition.presentation.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.akshayAshokCode.textrecognition.presentation.textrecognition.RecognitionFragment;
import com.akshayAshokCode.textrecognition.presentation.speech.SpeechFragment;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new SpeechFragment();
        }
        return new RecognitionFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
