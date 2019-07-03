package com.dscddu.dscddu.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.dscddu.dscddu.Fragments.NoInternetFragment;
import com.dscddu.dscddu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentActionListener
{
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
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Firebase msg", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        Log.d("Firebase message", token);
                        Toast.makeText(HomeActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
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
//        new InternetCheck(internet -> {
//            if(!internet){
////                Snackbar.make(findViewById(android.R.id.content),"Oops!! No Internet " +
////                        "Connections", Snackbar.LENGTH_INDEFINITE).setAction("Close",
////                        v -> finishAndRemoveTask()).show();
//                getSupportActionBar().setTitle("Oops!");
//                fragmentManager.beginTransaction().replace(R.id.layout_container,
//                        new NoInternetFragment()).commit();
//            }
//        });
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setHomeFragment();

        }
        else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            ProfileFragment profileFragment = new ProfileFragment();
            profileFragment.setFragmentActionListener(this::actionPerformed);
            fragmentManager.popBackStack("eventDetails", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.layout_container,profileFragment)
                    .commit();

        }
        else if (id == R.id.nav_suggestion) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/12KOKGSMmLL-_Mi33QOJwIUzTsYxMu66LpjlMg9t6akk/edit?ts=5d10793e"));
            startActivity(browserIntent);
        }
        else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("Ok", (dialog, id1) -> {

                mAuth.signOut();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent back = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(back);
                finish();
            });
            builder.setNegativeButton("Cancel", (dialog, id12) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        else if(id == R.id.nav_events_registered){
            getSupportActionBar().setTitle(R.string.menu_registered_events);
            fragmentManager.popBackStack("eventDetails", FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
        fragmentManager.popBackStack("eventDetails", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().replace(R.id.layout_container, homeFragment)
                .commit();
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
                        .replace(R.id.layout_container,eventDetailsFragment)
                        .addToBackStack("eventDetails")
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


            case FragmentActionListener.ACTION_NO_INTERNET:
                getSupportActionBar().setTitle("Oops!");
                NoInternetFragment noInternetFragment = new NoInternetFragment();
                fragmentManager.popBackStack("eventDetails", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.layout_container,
                        noInternetFragment).commit();
                break;
        }
    }



}
