package com.example.planttest2.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.planttest2.R;
import com.example.planttest2.ReminderSetupActivity;
import com.example.planttest2.api.ApiService;
import com.example.planttest2.beans.Plant;
import com.example.planttest2.config.PlantSingleton;
import com.example.planttest2.config.RetrofitClient;
import com.example.planttest2.plantsInfos;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {

    private List<Plant> plants;
    private Context context;

    public PlantAdapter(Context context) {
        this.context = context;
        this.plants = new ArrayList<>();
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_item, parent, false);
        return new PlantViewHolder(view, plants, context,this);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plants.get(position);
        holder.plantName.setText(plant.getName());
        holder.plantType.setText(plant.getType());
        holder.plantscientifiquename.setText(plant.getScientifiquename());

        String imageUrl = plant.getImageUrl();

        // Vérifier si l'image est une chaîne Base64 et ajouter le préfixe manuellement si nécessaire
        if (imageUrl != null && !imageUrl.startsWith("data:image")) {
            // Ajouter un préfixe "data:image/png;base64,"
            imageUrl = "data:image/png;base64," + imageUrl;
        }

        // Vérifier si l'image est une chaîne Base64 ou une URL
        if (imageUrl != null && imageUrl.startsWith("data:image")) {
            // Si c'est en base64, décoder l'image
            byte[] decodedString = Base64.decode(imageUrl.substring(imageUrl.indexOf(",") + 1), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.plantImage.setImageBitmap(decodedByte);
        } else {
            // Si c'est une URL, utiliser Glide pour la charger
            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(holder.plantImage);
        }
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
        notifyDataSetChanged();
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        private ImageView plantImage;
        private TextView plantName, plantscientifiquename;
        private TextView plantType;
        private LinearLayout reminderLayout;
        private ImageButton btnMore;
        private List<Plant> plants;
        private Context context;
        private PlantAdapter adapter;
        PlantViewHolder(@NonNull View itemView, List<Plant> plants, Context context, PlantAdapter adapter) {
            super(itemView);
            this.plants = plants;
            this.context = context;
            this.adapter = adapter;
            btnMore = itemView.findViewById(R.id.btnMore);
            btnMore.setOnClickListener(v -> showBottomSheet(plants.get(getAdapterPosition()).getIdPlante()));

            reminderLayout = itemView.findViewById(R.id.reminderLayout);
            plantImage = itemView.findViewById(R.id.plantImage);
            plantName = itemView.findViewById(R.id.plantName);
            plantscientifiquename = itemView.findViewById(R.id.plantScientificName);
            plantType = itemView.findViewById(R.id.plantType);

            itemView.setOnClickListener(v -> {
                if (!v.findViewById(R.id.reminderLayout).equals(v)) {
                    Plant plant = plants.get(getAdapterPosition());
                    Intent intent = new Intent(context, plantsInfos.class);
                    intent.putExtra("plantName", plant.getName());
                    PlantSingleton.getInstance().setPlantImage(plant.getImageUrl());
                    context.startActivity(intent);
                }
            });
            reminderLayout.setOnClickListener(v -> {
                Plant plant = plants.get(getAdapterPosition());
                Intent intent = new Intent(context, ReminderSetupActivity.class);
                intent.putExtra("plantName", plant.getName());
                context.startActivity(intent);
            });
        }

        private void showBottomSheet(Long nom) {
            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_plant_options, null);

            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(bottomSheetView);

            View reminderOption = bottomSheetView.findViewById(R.id.reminderOption);
            View deleteOption = bottomSheetView.findViewById(R.id.deleteOption);

            deleteOption.setOnClickListener(v -> {
                // Créer et afficher le dialogue de confirmation
                new AlertDialog.Builder(context)
                        .setTitle("Confirmation de suppression")
                        .setMessage("Êtes-vous sûr de vouloir supprimer cette plante ?")
                        .setPositiveButton("Supprimer", (dialog, which) -> {
                            // Code de suppression existant
                            String accessToken = context.getSharedPreferences("user_prefs", context.MODE_PRIVATE)
                                    .getString("access_token", null);

                            ApiService apiService = RetrofitClient.getInstance(accessToken).create(ApiService.class);
                            apiService.deletePlantFromJardin(nom).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        int position = getAdapterPosition();
                                        if (position != RecyclerView.NO_POSITION) {
                                            plants.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            adapter.notifyItemRangeChanged(position, plants.size());
                                        }
                                        Toast.makeText(context, "Plante supprimée avec succès", Toast.LENGTH_SHORT).show();
                                    }
                                    bottomSheetDialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.dismiss();
                                }
                            });
                        })
                        .setNegativeButton("Annuler", (dialog, which) -> {
                            // Ne rien faire, fermer simplement le dialogue
                            dialog.dismiss();
                        })
                        .show();

                // Fermer le bottom sheet
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.show();
        }
    }
}
