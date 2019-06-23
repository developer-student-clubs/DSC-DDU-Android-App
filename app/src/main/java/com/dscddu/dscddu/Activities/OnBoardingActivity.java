package com.dscddu.dscddu.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.dscddu.dscddu.Adapters.IntroViewPagerAdapter;
import com.dscddu.dscddu.Model_Class.LayoutScreenForOnBoarding;
import com.dscddu.dscddu.R;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private IntroViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private Button nextBtn,getStarted,skipBtn;
    private int position = 0;
    private Animation animatioButton;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make Activity in Full Screen Mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Check for First Time opened or Not
        if(!checkStatus()){
            Intent i1 = new Intent(getApplicationContext(),SignUpActivity.class);
            startActivity(i1);
            finish();
        }

        setContentView(R.layout.activity_on_boarding);
        //hide Action bar
        //getSupportActionBar().hide();


        // initialization
        nextBtn = findViewById(R.id.next_btn);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLyout);
        getStarted = findViewById(R.id.btn_get_started);
        skipBtn = findViewById(R.id.btn_skip);
        animatioButton = AnimationUtils.loadAnimation(getApplicationContext(),R.animator.btn_animation);

        final List<LayoutScreenForOnBoarding> mList = new ArrayList<>();
        // fill list screen
        mList.add(new LayoutScreenForOnBoarding("Easy Event Registration","Be the first one " +
                "register for all the events by DSC-DDU",R.drawable.ic_calendar));
        mList.add(new LayoutScreenForOnBoarding("Fast Notification","Never miss a single event by" +
                " receiving regular notifications.",
                R.drawable.ic_notification));
        mList.add(new LayoutScreenForOnBoarding("e-Certificates for Events","Easily Download " +
                "e-Certificates for the events participated",R.drawable.ic_medal));

        // setup viewpager
        adapter = new IntroViewPagerAdapter(this,mList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = viewPager.getCurrentItem();
                if(position < mList.size()){
                    position++;
                    viewPager.setCurrentItem(position);
                }
                if(position == mList.size()-1){
                    //TODO: show get started Button
                    lastScreenUpdate();
                }
            }

        });
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenSignUpForm();
            }
        });
        //TabLayout Add Change Listener
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == mList.size() - 1){
                    lastScreenUpdate();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenSignUpForm();
            }
        });
    }

    private void OpenSignUpForm() {
        Intent i = new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(i);
        savePrefrence();
        finish();
    }

    private boolean checkStatus() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPrefs",
                MODE_PRIVATE);
        return pref.getBoolean("firstTimeLaunch",true);
    }

    private void savePrefrence() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPrefs",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("firstTimeLaunch",false);
        editor.commit();
    }

    private void lastScreenUpdate() {
        nextBtn.setVisibility(View.INVISIBLE);
        tabLayout.setVisibility(View.INVISIBLE);
        skipBtn.setVisibility(View.INVISIBLE);
        getStarted.setVisibility(View.VISIBLE);
        //TODO: add animation to GetStarted Button
        getStarted.setAnimation(animatioButton);
    }
}
