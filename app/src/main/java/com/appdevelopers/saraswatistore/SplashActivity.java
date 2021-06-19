package com.appdevelopers.saraswatistore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashActivity extends AppCompatActivity {

    CircleImageView imgLogo;
    TextView appName,loading;
    Animation animation;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imgLogo = findViewById(R.id.imgLogo);
        appName = findViewById(R.id.txtapp_name);
        loading = findViewById(R.id.txtLoading);

        getSupportActionBar().hide();

        handler = new Handler();

        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);

        loading.startAnimation(animation);
        appName.startAnimation(animation);
        imgLogo.startAnimation(animation);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);

    }
}