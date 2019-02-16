package com.george.tcsdemo.interfaces;

import com.george.tcsdemo.data.HitsList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TcsInterface {

    String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    @GET("forecast")
    Call<HitsList> getAllWeatherData(@Query("lat") String latitude, @Query("lon") String longtitude, @Query("units") String metric, @Query("APPID") String apiKey);
}
