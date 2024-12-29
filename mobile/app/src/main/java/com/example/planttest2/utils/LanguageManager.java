package com.example.planttest2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageManager {
    private static LanguageManager instance;
    private static final String PREFS_NAME = "AppSettings";
    private static final String LANGUAGE_KEY = "language";

    private LanguageManager() {}

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public void setLocale(Context context) {
        String languageCode = getLanguagePreference(context);
        updateResources(context, languageCode);
    }

    public String getLanguagePreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(LANGUAGE_KEY, "fr"); // Default to French
    }

    public void saveLanguagePreference(Context context, String languageCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(LANGUAGE_KEY, languageCode).apply();
    }

    private void updateResources(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        context.createConfigurationContext(config);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}