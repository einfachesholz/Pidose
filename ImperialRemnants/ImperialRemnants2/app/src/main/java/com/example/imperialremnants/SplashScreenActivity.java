package com.example.imperialremnants;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 1500; // Splash screen duration in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Delay the start of the MainActivity using a Handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start MainActivity after the splash screen delay
                Intent mainIntent = new Intent(SplashScreenActivity.this, com.example.imperialremnants.MainActivity.class);
                startActivity(mainIntent);
                finish(); // Close the splash screen activity
            }
        }, SPLASH_SCREEN_DELAY);
    }
}
