package com.example.planttest2.fragement;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.planttest2.api.ApiOpenAi;
import com.example.planttest2.api.ApiService;
import com.example.planttest2.config.RetrofitClient;
import com.example.planttest2.config.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.example.planttest2.R;
import com.example.planttest2.adapter.PlantDBAdapter;
import com.example.planttest2.beans.Plant;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantsFragment extends BaseFragment {
    private PlantDBAdapter adapter;
    private RecyclerView recyclerView;
    private TextInputEditText searchInput;
    private static final String BASE_URL = "https://api.unsplash.com/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plants, container, false);
        showLoadingOverlay(view);
        // Initialisation du RecyclerView
        recyclerView = view.findViewById(R.id.plantsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialisation de l'adaptateur
        adapter = new PlantDBAdapter(getContext());
        recyclerView.setAdapter(adapter);

        // Configuration de la recherche
        searchInput = view.findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              int size=adapter.filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0)
                    loadSamplePlants(view);
                int size = adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                        return true; // Ignorer les événements ACTION_UP
                    }

                    List<Plant> plants = new ArrayList<>();
                    String searchQuery = searchInput.getText().toString();
                    showLoadingOverlay(view);
                    if(searchQuery.length()==0){
                        loadSamplePlants(view);
                        return true;
                    }
                    ApiOpenAi apiOpenAi = RetrofitClient.getInstance(SessionManager.getInstance().getAccessToken()).create(ApiOpenAi.class);
                    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(httpClient.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    ApiService apiService = retrofit.create(ApiService.class);

                    apiOpenAi.getSearchPlant(searchQuery).enqueue(new Callback<List<Plant>>() {
                        @Override
                        public void onResponse(Call<List<Plant>> call, Response<List<Plant>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<Plant> fetchedPlants = response.body();
                                int totalPlants = fetchedPlants.size();
                                AtomicInteger completedRequests = new AtomicInteger(0); // Compteur pour suivre les requêtes terminées

                                for (Plant plant : fetchedPlants) {
                                    apiService.searchPhotos(plant.getScientifiquename(), "-zFvRZjv06sXEm7Z5ocQaqJS1-JRJLJDYSd0CuvGMvc", 1)
                                            .enqueue(new Callback<JsonObject>() {
                                                @Override
                                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                    if (response.isSuccessful() && response.body() != null) {
                                                        try {
                                                            JsonObject jsonResponse = response.body();
                                                            JsonArray results = jsonResponse.getAsJsonArray("results");

                                                            if (results != null && results.size() > 0) {
                                                                JsonObject firstResult = results.get(0).getAsJsonObject();
                                                                String imageUrl = firstResult.getAsJsonObject("urls").get("raw").getAsString();
                                                                plant.setImageUrl(imageUrl);
                                                                Log.d("ImageURL", "Fetched URL: " + imageUrl);
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    // Vérifier si toutes les requêtes sont terminées
                                                    if (completedRequests.incrementAndGet() == totalPlants) {
                                                        plants.addAll(fetchedPlants); // Ajouter les plantes à la liste principale
                                                        adapter.setPlants(plants); // Mettre à jour l'adaptateur
                                                        hideLoadingOverlay(view);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                                    Log.d("ImageURL", "Error fetching image: " + t.getMessage());

                                                    // Vérifier si toutes les requêtes sont terminées malgré l'échec
                                                    if (completedRequests.incrementAndGet() == totalPlants) {
                                                        plants.addAll(fetchedPlants); // Ajouter les plantes à la liste principale
                                                        adapter.setPlants(plants); // Mettre à jour l'adaptateur
                                                    }
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Plant>> call, Throwable t) {
                            Log.d("PlantFetch", "Error fetching plants: " + t.getMessage());
                        }
                    });
                    return true; // Signifie que l'action a été traitée
                }
                return false;
            }
        });


        // Chargement des données de test
        loadSamplePlants(view);


        return view;
    }
    private void showLoadingOverlay(View view) {
        FrameLayout loadingOverlay = view.findViewById(R.id.loadingOverlay);
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoadingOverlay(View view) {
        FrameLayout loadingOverlay = view.findViewById(R.id.loadingOverlay);
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
        }
    }



    private void loadSamplePlants(View view) {

        List<Plant> plants = new ArrayList<>();
        ApiService apiService = RetrofitClient.getInstance(SessionManager.getInstance().getAccessToken()).create(ApiService.class);
        apiService.getAllPlant().enqueue(new Callback<List<Plant>>() {
            @Override
            public void onResponse(Call<List<Plant>> call, Response<List<Plant>> response) {
                if (response.isSuccessful()) {
                    for (Plant plant : response.body()) {
                        plants.add(plant);
                    }
                    adapter.setPlants(plants);
                    // Ajouter un délai de 1 seconde avant de masquer l'animation de chargement
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                         hideLoadingOverlay(view);
                        }
                    }, 1000);                }
            }

            @Override
            public void onFailure(Call<List<Plant>> call, Throwable t) {

            }
        });
    }
}