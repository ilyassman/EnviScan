package com.example.planttest2.notificationconfig;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

public class NotificationHelper {
    private static final String CHANNEL_ID = "ReminderChannel";
    private static final String CHANNEL_NAME = "Rappels";
    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal pour les rappels");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void scheduleNotification(ReminderModel reminder) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("reminderTitle", reminder.getTitle());
        intent.putExtra("reminderDesc", reminder.getDescription());
        intent.putExtra("reminderId", reminder.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                // Demander la permission à l'utilisateur
                Intent alarmPermissionIntent = new Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                );
                context.startActivity(alarmPermissionIntent);
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminder.getTimeInMillis(),
                    pendingIntent
            );
        }
    }
}
