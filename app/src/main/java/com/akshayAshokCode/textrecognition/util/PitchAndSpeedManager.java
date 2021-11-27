package com.akshayAshokCode.textrecognition.util;

import android.widget.SeekBar;

public class PitchAndSpeedManager {
    public float getPitch(SeekBar pitchProgress){
        float pitch=(float)pitchProgress.getProgress()/50;
        if(pitch<0.1) pitch=0.1f;
        return pitch;
    }
    public float getSpeed(SeekBar speedProgress){
        float speed=(float)speedProgress.getProgress()/50;
        if(speed<0.1) speed=0.1f;
        return  speed;
    }
}
