package com.example.planttest2.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.planttest2.R;
// TaskAdapter.java
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private String[] tasks;
    private int[] icons;
    private OnTaskSelectedListener listener;

    public interface OnTaskSelectedListener {
        void onTaskSelected(String task);
    }

    public TaskAdapter(String[] tasks, int[] icons, OnTaskSelectedListener listener) {
        this.tasks = tasks;
        this.icons = icons;
        this.listener = listener;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        holder.bind(tasks[position], icons[position]);
    }

    @Override
    public int getItemCount() {
        return tasks.length;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView text;

        TaskViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.taskIcon);
            text = itemView.findViewById(R.id.taskText);

            itemView.setOnClickListener(v -> {
                listener.onTaskSelected(tasks[getAdapterPosition()]);
            });
        }

        void bind(String task, int iconRes) {
            text.setText(task);
            icon.setImageResource(iconRes);
        }
    }
}
