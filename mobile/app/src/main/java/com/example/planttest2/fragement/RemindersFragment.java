package com.example.planttest2.fragement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planttest2.R;
import com.example.planttest2.adapter.ReminderAdapter;
import com.example.planttest2.model.Reminder;
import com.example.planttest2.ReminderPreferences;
import com.google.android.material.button.MaterialButton;
import java.util.Collections;
import java.util.List;

public class RemindersFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private MaterialButton addReminderBtn;
    private ReminderPreferences reminderPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        reminderPreferences = new ReminderPreferences(requireContext());
        recyclerView = view.findViewById(R.id.reminderRecyclerView);
        addReminderBtn = view.findViewById(R.id.btnAddReminder);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadReminders();

        addReminderBtn.setOnClickListener(v -> {
            // Implémenter l'ajout de rappel
        });

        return view;
    }

    private void loadReminders() {
        List<Reminder> reminders = reminderPreferences.getReminders();
        // Trier les rappels par heure
        Collections.sort(reminders, (r1, r2) -> r1.getTime().compareTo(r2.getTime()));
        adapter = new ReminderAdapter(reminders);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadReminders();
    }
}