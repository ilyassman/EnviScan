package com.example.planttest2.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.planttest2.PlantSearchInfo;
import com.example.planttest2.R;
import com.example.planttest2.beans.Plant;
import com.example.planttest2.config.PlantSingleton;
import com.example.planttest2.plantsInfos;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
public class PlantDBAdapter extends RecyclerView.Adapter<PlantDBAdapter.PlantViewHolder> {
    private List<Plant> plantList;
    private List<Plant> plantListFull;
    private Context context;

    public PlantDBAdapter(Context context) {
        this.context = context;
        this.plantList = new ArrayList<>();
        this.plantListFull = new ArrayList<>();
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_item_db, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plantList.get(position);

        holder.plantName.setText(plant.getName());
        holder.plantScientificName.setText(plant.getScientifiquename());
        holder.categoryChip.setText(plant.getType());

        String imageUrl = plant.getImageUrl();
        // Vérifier si l'image est une chaîne Base64 et ajouter le préfixe manuellement si nécessaire
        if (imageUrl != null && !imageUrl.startsWith("data:image") && !imageUrl.startsWith("https://images.")) {
            // Ajouter un préfixe "data:image/png;base64,"
            imageUrl = "data:image/png;base64," + imageUrl;
        }
        if (imageUrl != null && imageUrl.startsWith("data:image")) {
            byte[] decodedString = Base64.decode(imageUrl.substring(imageUrl.indexOf(",") + 1), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.plantImage.setImageBitmap(decodedByte);
        } else {
            Log.d("ImageURL","depui adapter");
            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(holder.plantImage);
        }

        // Ajouter la logique de visibilité pour arLabel et arIcon
        if ("Aloe Vera".equals(plant.getName())) {
            holder.arLabel.setVisibility(View.VISIBLE);
            holder.arIcon.setVisibility(View.VISIBLE);
        } else {
            holder.arLabel.setVisibility(View.GONE);
            holder.arIcon.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlantSearchInfo.class);
            intent.putExtra("plantName", plant.getName());
            if(plant.getImageUrl().startsWith("https://images."))
                intent.putExtra("imageUrl", plant.getImageUrl());
            else
            PlantSingleton.getInstance().setPlantImage(plant.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public void setPlants(List<Plant> plants) {
        this.plantList = plants;
        this.plantListFull = new ArrayList<>(plants);
        notifyDataSetChanged();
    }

    public int filter(String text) {
        plantList.clear();
        if (text.isEmpty()) {
            plantList.addAll(plantListFull);
        } else {
            text = text.toLowerCase();
            for (Plant plant : plantListFull) {
                if (plant.getName().toLowerCase().contains(text) ||
                        plant.getScientifiquename().toLowerCase().contains(text) ||
                        plant.getType().toLowerCase().contains(text)) {
                    plantList.add(plant);
                }
            }
        }
        notifyDataSetChanged();
        return plantList.size();
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView plantImage;
        TextView plantName, plantScientificName, arLabel;
        Chip categoryChip;
        ImageView arIcon;

        PlantViewHolder(View itemView) {
            super(itemView);
            plantImage = itemView.findViewById(R.id.plantImage);
            plantName = itemView.findViewById(R.id.plantName);
            plantScientificName = itemView.findViewById(R.id.plantScientificName);
            categoryChip = itemView.findViewById(R.id.categoryChip);
            arLabel = itemView.findViewById(R.id.arLabel);
            arIcon = itemView.findViewById(R.id.arIcon);
        }
    }

}