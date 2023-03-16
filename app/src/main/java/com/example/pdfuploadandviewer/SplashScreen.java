package com.example.pdfuploadandviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int SPLASH_SCREEN_TIME_OUT = 1000;
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(SplashScreen.this, R.anim.fade_in, R.anim.fade_out);
            SplashScreen.this.startActivity(i, options.toBundle());
            finish();
        }, SPLASH_SCREEN_TIME_OUT);

    }
}