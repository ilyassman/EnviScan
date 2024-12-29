package com.example.planttest2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planttest2.R;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.example.planttest2.model.Reminder;
import android.widget.LinearLayout;  // Ajout de l'importation de LinearLayout


public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    private List<Reminder> reminders;

    public ReminderAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);

        // Définir l'icône selon le type
        switch(reminder.getType().toLowerCase()) {
            case "arroser":
                holder.wateringIcon.setImageResource(R.drawable.ic_water);
                break;
            case "brumiser":
                holder.wateringIcon.setImageResource(R.drawable.ic_mist);
                break;
            case "tourner":
                holder.wateringIcon.setImageResource(R.drawable.ic_rotate);
                break;
            case "fertiliser":
                holder.wateringIcon.setImageResource(R.drawable.ic_fertilize);
                break;
        }

        holder.typeText.setText(reminder.getType() + " - " + reminder.getPlantName());
        holder.timeText.setText(reminder.getTime());
        holder.notesText.setText(reminder.getNotes());

        // Gérer l'expansion
        holder.expandIcon.setOnClickListener(v -> {
            boolean isExpanded = holder.notesSection.getVisibility() == View.VISIBLE;
            holder.notesSection.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            holder.expandIcon.setRotation(isExpanded ? 0 : 180);
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView typeText, timeText, notesText;
        ImageView wateringIcon, expandIcon;
        LinearLayout notesSection;

        ViewHolder(View itemView) {
            super(itemView);
            typeText = itemView.findViewById(R.id.typeText);
            timeText = itemView.findViewById(R.id.timeText);
            notesText = itemView.findViewById(R.id.notesText);
            wateringIcon = itemView.findViewById(R.id.wateringIcon);
            expandIcon = itemView.findViewById(R.id.expandIcon);
            notesSection = itemView.findViewById(R.id.notesSection);
        }
    }
}