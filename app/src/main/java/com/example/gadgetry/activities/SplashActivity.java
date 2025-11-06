package com.example.gadgetry.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gadgetry.databinding.ActivitySplashBinding;
import com.example.gadgetry.utils.UtilKeys;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                UtilKeys.PREFS = getSharedPreferences(UtilKeys.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

                if (UtilKeys.PREFS.getString(UtilKeys.TOKEN, "").equals("admin")) {
                    startActivity(new Intent(SplashActivity.this, GalleryActivity.class));
                    finish();
                } else if (UtilKeys.PREFS.getString(UtilKeys.TOKEN, "").equals("user")) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            // printToastMessage("After 3 seconds we moved to next screen");

        }, 3000);
    }
}