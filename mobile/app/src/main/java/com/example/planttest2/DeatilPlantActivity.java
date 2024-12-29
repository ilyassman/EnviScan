package com.example.planttest2;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planttest2.api.ApiOpenAi;
import com.example.planttest2.config.RetrofitClient;
import com.example.planttest2.model.DetailPlant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeatilPlantActivity extends AppCompatActivity {

    private ImageView plantImageView;
    private TextView plantNameTextView;
    private TextView plantDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String accessToken = getSharedPreferences("user_prefs", MODE_PRIVATE)
            .getString("access_token", null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deatil_plant);
        ApiOpenAi apiOpenAi = RetrofitClient.getInstance(accessToken).create(ApiOpenAi.class);


        // Récupérer les vues du layout
        plantImageView = findViewById(R.id.plantImageView);
        plantNameTextView = findViewById(R.id.plantNameTextView);
        plantDescriptionTextView = findViewById(R.id.plantDescriptionTextView);
        TextView temperatureTextView = findViewById(R.id.temperatureTextView);
        TextView lightTextView = findViewById(R.id.lightTextView);
        TextView wateringTextView = findViewById(R.id.wateringTextView);
        TextView repottingTextView = findViewById(R.id.repottingTextView);
        TextView fertilizationTextView = findViewById(R.id.fertilizationTextView);

        // Récupérer le nom de la plante et l'URI de l'image depuis l'Intent
        String plantName = getIntent().getStringExtra("plantName");
        String imageUriString = getIntent().getStringExtra("imageUri");
        Uri imageUri = Uri.parse(imageUriString);
        apiOpenAi.getDetailPlant(plantName).enqueue(new Callback<DetailPlant>() {
            @Override
            public void onResponse(Call<DetailPlant> call, Response<DetailPlant> response) {
                if(response.isSuccessful() && response.body()!=null){
                    plantDescriptionTextView.setText(response.body().getDescriptionfr());
                    temperatureTextView.setText(response.body().getTemperature());
                    lightTextView.setText(response.body().getLumiere());
                    wateringTextView.setText(response.body().getfrequencearrosage());
                    repottingTextView.setText(response.body().getRempotage());
                    fertilizationTextView.setText(response.body().getFertilisation());
                }
            }

            @Override
            public void onFailure(Call<DetailPlant> call, Throwable t) {

            }
        });
        // Afficher les détails de la plante
        showPlantDetails(plantName, imageUri);

        // Gérer le clic sur le bouton de diagnostic

    }

    private void showPlantDetails(String plantName, Uri imageUri) {
        // Charger l'image à partir de l'URI
        plantImageView.setImageURI(imageUri);

        // Afficher le nom de la plante
        plantNameTextView.setText(plantName);

        // Récupérer et afficher la description de la plante


    }


}