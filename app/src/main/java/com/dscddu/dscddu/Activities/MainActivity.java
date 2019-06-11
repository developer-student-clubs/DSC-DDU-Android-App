package com.dscddu.dscddu.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dscddu.dscddu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button logout;
    private FirebaseAuth mAuth;
    private TextView name;
    private Button verifyBtn;
    private FirebaseUser user;
    private boolean emailVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Verify Your Email");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        if(user != null){
            emailVerified = user.isEmailVerified();
            if(emailVerified){
                Toast.makeText(MainActivity.this,"Email is Verified", Toast.LENGTH_LONG).show();
                Log.d("RRR", "verified");

            }
        }
        name = findViewById(R.id.nameText);

        verifyBtn =findViewById(R.id.verifyBtn);
        verifyBtn.setEnabled(true);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Main Activity", "Email sent.");
                                    verifyBtn.setEnabled(false);
                                    skipActivity();
                                }
                            }
                        });

            }
        });
        name.setText("Hey, "+user.getDisplayName());
        logout =findViewById(R.id.logoutBtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void skipActivity() {
//        Intent intent = new Intent(getApplicationContext(),)
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(i);
            finish();
        }else{

            if(emailVerified){
                verifyBtn.setEnabled(false);
                verifyBtn.setVisibility(View.INVISIBLE);

                skipActivity();
            }
        }
    }
}
