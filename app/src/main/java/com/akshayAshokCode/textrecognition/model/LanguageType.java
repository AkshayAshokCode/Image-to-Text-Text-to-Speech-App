package com.akshayAshokCode.textrecognition.model;

import androidx.annotation.NonNull;

import java.util.Locale;

public class LanguageType {
    private String name;
    private Locale locale;

    public LanguageType(String name, Locale locale) {
        this.name = name;
        this.locale = locale;
    }

    public LanguageType() {
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
