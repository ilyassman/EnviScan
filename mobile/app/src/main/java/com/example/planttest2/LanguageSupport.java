package com.example.planttest2;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

public interface LanguageSupport {
    void updateLanguage();
    String getString(int resourceId);
}