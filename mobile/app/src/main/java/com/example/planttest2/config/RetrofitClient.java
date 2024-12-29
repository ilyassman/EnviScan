package com.example.planttest2.config;

import android.util.Log;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static String currentToken = null;

    public static Retrofit getInstance(String token) {
        if (retrofit == null || !token.equals(currentToken)) {
            currentToken = token;

            // Configurer les délais de connexion et de lecture
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(token))
                    .connectTimeout(60, TimeUnit.SECONDS)  // Temps de connexion
                    .readTimeout(60, TimeUnit.SECONDS)     // Temps d'attente pour la lecture
                    .writeTimeout(60, TimeUnit.SECONDS)    // Temps d'attente pour l'écriture
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8082/")  // URL de base
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
