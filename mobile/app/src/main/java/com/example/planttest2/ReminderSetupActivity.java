package com.example.planttest2;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import com.example.planttest2.adapter.TaskAdapter;

import android.widget.EditText;

import android.widget.ImageButton;
import android.view.animation.AlphaAnimation;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.planttest2.R;
import android.widget.FrameLayout;
import java.util.Date;  // Assurez-vous que cette ligne est présente

import com.example.planttest2.notificationconfig.NotificationHelper;
import com.example.planttest2.notificationconfig.ReminderModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.widget.NumberPicker;
import android.widget.Toast;
import com.example.planttest2.model.Reminder;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

// ReminderSetupActivity.java
public class ReminderSetupActivity extends AppCompatActivity {
    private String plantName;
    private MaterialButton btnTaskType, btnRepeat;
    private TextView timeDisplay;
    private MaterialButtonToggleGroup amPmToggle;
    private EditText noteInput;
    private TextView  plantNameAnimation ;
    private FrameLayout floralsContainer;

    private NotificationHelper notificationHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_setup);

        notificationHelper = new NotificationHelper(this);

        noteInput = findViewById(R.id.noteInput);
        // Demander les permissions nécessaires pour Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    1
            );
        }


        plantName = getIntent().getStringExtra("plantName");
        TextView plantNameView = findViewById(R.id.plantName);
        plantNameView.setText(plantName);

        setupTaskTypeButton();
        //setupRepeatButton();
        setupTimeSelector();
        setupCloseButton();
        setupActivateButton();
    }

    private void setupTaskTypeButton() {
        btnTaskType = findViewById(R.id.btnTaskType);
        btnTaskType.setOnClickListener(v -> {
            showTaskTypeDialog();
        });
    }

    private void showTaskTypeDialog() {
        String[] tasks = {
                getString(R.string.watering),
                getString(R.string.misting),
                getString(R.string.rotating),
                getString(R.string.fertilizing)
        };
        int[] icons = {
                R.drawable.ic_water,
                R.drawable.ic_mist,
                R.drawable.ic_rotate,
                R.drawable.ic_fertilize
        };

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.task_type_selector, null);
        RecyclerView recyclerView = view.findViewById(R.id.taskRecyclerView);

        TaskAdapter adapter = new TaskAdapter(tasks, icons, task -> {
            btnTaskType.setText(task); // Cela mettra à jour le texte du bouton avec la tâche sélectionnée
            dialog.dismiss();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        dialog.setContentView(view);
        dialog.show();
    }

    /*private void setupRepeatButton() {
        btnRepeat = findViewById(R.id.btnRepeat);
        btnRepeat.setOnClickListener(v -> {
            showRepeatDialog();
        });
    }*/

    /*private void showRepeatDialog() {
        NumberPicker daysPicker = new NumberPicker(this);
        daysPicker.setMinValue(1);
        daysPicker.setMaxValue(30);
        daysPicker.setValue(7);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Répéter tous les")
                .setView(daysPicker)
                .setPositiveButton("OK", (dialog, which) -> {
                    btnRepeat.setText("Tous les " + daysPicker.getValue() + " jours");
                })
                .show();
    }*/

    private void setupTimeSelector() {
        timeDisplay = findViewById(R.id.timeDisplay);
        amPmToggle = findViewById(R.id.amPmToggle);

        timeDisplay.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(7)
                    .setMinute(0)
                    .build();

            picker.addOnPositiveButtonClickListener(dialog -> {
                String time = String.format("%d:%02d",
                        picker.getHour(), picker.getMinute());
                timeDisplay.setText(time);
            });

            picker.show(getSupportFragmentManager(), "time_picker");
        });
    }

    private void setupCloseButton() {
        ImageButton btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> finish());
    }

    private void setupActivateButton() {
        MaterialButton btnActivate = findViewById(R.id.btnActivate);
        btnActivate.setOnClickListener(v -> {
            testNotification(btnTaskType.getText().toString());
            //finish();
        });
    }
    private void testNotification(String type) {
        Calendar calendar = Calendar.getInstance();

        // Parser l'heure depuis timeDisplay (format "HH:mm")
        String timeStr = timeDisplay.getText().toString();
        String[] timeParts = timeStr.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Configurer le calendar pour aujourd'hui à l'heure spécifiée
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Si l'heure spécifiée est déjà passée aujourd'hui, programmer pour demain
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        createReminder(
                type,
                getString(R.string.notification_time, type),
                calendar.getTimeInMillis()
        );

        // Afficher un message de confirmation
        String timeMessage = String.format("Rappel programmé pour %02d:%02d", hour, minute);
        Toast.makeText(ReminderSetupActivity.this, timeMessage, Toast.LENGTH_LONG).show();
    }

    private void createReminder(String title, String description, long timeInMillis) {
        // Créer le reminder pour la notification
        ReminderModel notificationReminder = new ReminderModel(
                (int) System.currentTimeMillis(),
                title,
                description,
                timeInMillis
        );
        notificationHelper.scheduleNotification(notificationReminder);

        // Créer le reminder pour le stockage
        Reminder reminder = new Reminder(
                plantName,
                btnTaskType.getText().toString(),
                timeDisplay.getText().toString(),
                noteInput.getText().toString()
        );

        // Sauvegarder dans SharedPreferences
        ReminderPreferences prefs = new ReminderPreferences(this);
        prefs.saveReminder(reminder);
    }
}