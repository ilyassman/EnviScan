package com.example.planttest2;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.planttest2.model.Reminder;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReminderPreferences {
    private static final String PREF_NAME = "ReminderPrefs";
    private static final String KEY_REMINDERS = "reminders";
    private SharedPreferences preferences;
    private Gson gson;

    public ReminderPreferences(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveReminder(Reminder reminder) {
        List<Reminder> reminders = getReminders();
        reminders.add(reminder);
        saveReminders(reminders);
    }

    public void saveReminders(List<Reminder> reminders) {
        String jsonReminders = gson.toJson(reminders);
        preferences.edit().putString(KEY_REMINDERS, jsonReminders).apply();
    }

    public List<Reminder> getReminders() {
        String jsonReminders = preferences.getString(KEY_REMINDERS, "");
        if (jsonReminders.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Reminder>>(){}.getType();
        return gson.fromJson(jsonReminders, type);
    }

    public void clearReminders() {
        preferences.edit().remove(KEY_REMINDERS).apply();
    }
}