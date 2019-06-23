package com.dscddu.dscddu.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dscddu.dscddu.Fragments.EventDetailsFragment;
import com.dscddu.dscddu.Fragments.EventHistoryFragment;
import com.dscddu.dscddu.Fragments.HomeFragment;
import com.dscddu.dscddu.Fragments.ProfileFragment;
import com.dscddu.dscddu.Fragments.QrCodeFragment;
import com.dscddu.dscddu.Fragments.RegisterConfirmationDialog;
import com.dscddu.dscddu.Listeners.FragmentActionListener;
import com.dscddu.dscddu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentActionListener{
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FragmentManager fragmentManager;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //init
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        fragmentManager = getSupportFragmentManager();
        //FOR FLOATING BUTTON

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        setHomeFragment();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        updateNavHeader();
    }

    @Override
    protected void onStart() {
        super.onStart();

//        user.getIdToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
//            @Override
//            public void onSuccess(GetTokenResult result) {
//                boolean isAdmin = (boolean) result.getClaims().get("admin");
//                if (isAdmin) {
//                    // Show admin UI.
////                    showAdminUI();
//                } else {
//                    // Show regular user UI.
////                    showRegularUI();
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            Toast.makeText(this,"HELP CLICKED",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setHomeFragment();

        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            fragmentManager.beginTransaction().replace(R.id.layout_container,
                    new ProfileFragment()).commit();

        }
//        else if (id == R.id.nav_settings) {
//            getSupportActionBar().setTitle("Settings");
//            fragmentManager.beginTransaction().replace(R.id.layout_container,
//                    new HomeFragment()).commit();
//
//        }
        else if (id == R.id.nav_logout) {
            mAuth.signOut();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent back = new Intent(getApplicationContext(),SignUpActivity.class);
            startActivity(back);
            finish();
        }
        else if(id == R.id.nav_qr_code){
            getSupportActionBar().setTitle("Your QR Code");
            fragmentManager.beginTransaction().replace(R.id.layout_container,
                    new QrCodeFragment()).commit();

        }
        else if(id == R.id.nav_events_registered){
            getSupportActionBar().setTitle(R.string.menu_registered_events);
            fragmentManager.beginTransaction().replace(R.id.layout_container,
                    new EventHistoryFragment()).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setHomeFragment() {
        getSupportActionBar().setTitle("Home");
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setFragmentActionListener(this::actionPerformed);
        fragmentManager.beginTransaction().replace(R.id.layout_container,
                homeFragment).commit();
    }

    public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_user_name);
        TextView navEmail = headerView.findViewById(R.id.nav_user_email);
        ImageView imageView = headerView.findViewById(R.id.nav_user_photo);

        navEmail.setText(user.getEmail());
        navUsername.setText(user.getDisplayName());
        Glide.with(this).load(user.getPhotoUrl()).into(imageView);

    }

    @Override
    public void actionPerformed(Bundle bundle) {
        int action = bundle.getInt(FragmentActionListener.ACTION_KEY);
        switch (action){
            case FragmentActionListener.ACTION_VALUE_EVENT_DETAILS:
                //Invoke Activity as per requirement
                EventDetailsFragment eventDetailsFragment= new EventDetailsFragment();
                eventDetailsFragment.setFragmentActionListener(this);
                eventDetailsFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.layout_container,eventDetailsFragment).addToBackStack(null)
                        .commit();
                break;


            case FragmentActionListener.ACTION_VALUE_REGISTER:

                RegisterConfirmationDialog registerConfirmationDialog =
                        new RegisterConfirmationDialog();
                registerConfirmationDialog.setFragmentActionListener(this);
                registerConfirmationDialog.setArguments(bundle);
                registerConfirmationDialog.show(fragmentManager,"RegisterConfirmationDialog");
                break;
            case FragmentActionListener.ACTION_VALUE_BACK_TO_HOME:
                setHomeFragment();
                break;
        }
    }



}
