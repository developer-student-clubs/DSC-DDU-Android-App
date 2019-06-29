package com.dscddu.dscddu.Activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //make Activity in Full Screen Mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        //In Actual Splash Screen no need to set Splash Screen
//        setContentView(R.layout.activity_splash_screen);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {


                Intent i = new Intent(SplashScreen.this, OnBoardingActivity.class);
                startActivity(i);
//                        ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this).toBundle());
                finish();

//            }
//        },1500);
    }
}
