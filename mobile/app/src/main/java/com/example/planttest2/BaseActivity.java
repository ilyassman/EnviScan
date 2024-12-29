package com.example.planttest2;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.planttest2.utils.LanguageManager;
import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    protected LanguageManager languageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        languageManager = LanguageManager.getInstance();
        languageManager.setLocale(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String languageCode = LanguageManager.getInstance().getLanguagePreference(newBase);
        Context context = updateBaseContextLocale(newBase, languageCode);
        super.attachBaseContext(context);
    }

    private Context updateBaseContextLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }
}