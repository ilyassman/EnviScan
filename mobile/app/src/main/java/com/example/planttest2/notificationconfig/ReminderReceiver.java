package com.example.planttest2.notificationconfig;
// ReminderReceiver.java

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.planttest2.R;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("reminderTitle");
        String description = intent.getStringExtra("reminderDesc");
        int reminderId = intent.getIntExtra("reminderId", 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ReminderChannel")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(reminderId, builder.build());
        }
    }
}