package com.example.dataapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    ProgressBar progressBar;
    FirebaseAuth fauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar=findViewById(R.id.progressBar3);
        fauth=FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the MainActivity. */
                if (fauth.getCurrentUser() != null) {
                    startActivity(new Intent(getApplicationContext(), Display.class));
                    progressBar.setVisibility(View.VISIBLE);
                    killActivity();
                }
                else {
                    Intent mainIntent = new Intent(Splash.this, Login.class);
                    startActivity(mainIntent);
                    progressBar.setVisibility(View.VISIBLE);
                    killActivity();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
        }
    private void killActivity() {
        finish();
    }
        }