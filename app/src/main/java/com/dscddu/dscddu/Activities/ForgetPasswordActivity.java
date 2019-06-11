package com.dscddu.dscddu.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.dscddu.dscddu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "Forget Password Activity:";
    private EditText email;
    private Button submit;
    private FirebaseAuth mAuth;
    private View parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getSupportActionBar().setTitle("Reset Your Password");
        parentLayout = findViewById(android.R.id.content);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.forgetPass_email);
        submit = findViewById(R.id.forgetPassSubmitBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail = email.getText().toString();
                if (mEmail.isEmpty()) {
                    email.setError("Email Required");
                    requestFocus(email);
                    return;
                }
                mAuth.sendPasswordResetEmail(mEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");

                                    //TODO ADD SNACK BAR HERE
//                                    Snackbar.make(parentLayout, "Email Sent. Check your Inbox.",
//                                            Snackbar.LENGTH_SHORT).show();
                                }
                                else {
//                                    Snackbar.make(parentLayout, "Wrong Email or email not " +
//                                                    "Registered",
//                                            Snackbar.LENGTH_LONG).show();
                                    Log.d(TAG, "Email NOT sent.");

                                }
                            }
                        });
                Intent i = new Intent(ForgetPasswordActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
