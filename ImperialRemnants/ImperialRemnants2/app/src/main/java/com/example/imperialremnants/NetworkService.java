package com.example.imperialremnants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.HashMap;
import java.util.Map;

public class NetworkService {

    // Define your Retrofit interface
    public interface ApiService {
        @POST("/set-time")
        Call<Void> setTime(@Body Map<String, String> data);
    }

    // Define your Retrofit instance as a static variable
    private static Retrofit retrofit;

    // Define your base URL
    private static final String BASE_URL = "http://192.168.162.126:5000";

    // Method to create and return Retrofit instance
    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Method to create and return ApiService instance
    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }

    // Method to send the time input to your Raspberry Pi
    public static void sendTimeToRaspberryPi(String selectedTime) {
        // Prepare data to send
        Map<String, String> data = new HashMap<>();
        data.put("selected_time", selectedTime);

        // Send HTTP POST request
        Call<Void> call = getApiService().setTime(data);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Handle success response
                if (response.isSuccessful()) {
                    // Success handling
                } else {
                    // Error handling
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
            }
        });
    }
}
