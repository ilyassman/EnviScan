package com.example.planttest2.api;

import com.example.planttest2.beans.Plant;
import com.example.planttest2.model.ChatBotResponse;
import com.example.planttest2.model.DetailPlant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiOpenAi {
    @GET("/chat")
    Call<DetailPlant> getDetailPlant(@Query("message") String message);
    @GET("/chatboot")
    Call<ChatBotResponse> getResponseFromBoot(@Query("message") String message);
    @GET("/searchPlant")
    Call<List<Plant>> getSearchPlant(@Query("message") String message);
}
