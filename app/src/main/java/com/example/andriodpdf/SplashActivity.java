package com.example.andriodpdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY_MS =2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, MainActivity.class); // Redirect to your main activity
            startActivity(intent);
            finish(); // Close the splash activity
        }, SPLASH_DELAY_MS);
    }
}