package com.example.planttest2.api;

import com.example.planttest2.beans.Plant;
import com.example.planttest2.model.LoginRequest;
import com.example.planttest2.model.LoginResponse;
import com.example.planttest2.model.Utilisateur;
import com.example.planttest2.model.request.JardinRequest;
import com.example.planttest2.model.request.PlantRequest;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("/user")
    Call<Void> createUser(@Body Utilisateur request);
    @GET("/profil")
    Call<Utilisateur> getProfil();
    @POST("/plants")
    Call<Long> createPlant(@Body PlantRequest plant);
    @POST("/jardin")
    Call<Void> addToJardin(@Body JardinRequest jardin);
    @GET("user/plants")
    Call<List<Plant>> getPlantsByUser();
    @PUT("userupdate/{id}")
    Call<Void> updateUser(@Path("id") Long  id,@Body Utilisateur user);
    @GET("/plants")
    Call<List<Plant>> getAllPlant();
    @GET("search/photos")
    Call<JsonObject> searchPhotos(
            @Query("query") String query,
            @Query("client_id") String clientId,
            @Query("per_page") int perPage
    );
    @DELETE("jardin/{id}")
    Call<Void> deletePlantFromJardin(
            @Path("id") Long plantId
    );


}
