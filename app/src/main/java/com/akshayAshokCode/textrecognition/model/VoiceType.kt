package com.akshayAshokCode.textrecognition.model

import android.speech.tts.Voice

data class VoiceType(
    val displayName: String,
    val voice: Voice
) {
    override fun toString(): String = displayName
}
