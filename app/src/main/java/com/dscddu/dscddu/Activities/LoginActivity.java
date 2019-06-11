package com.dscddu.dscddu.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dscddu.dscddu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private Button login;
    private EditText emailEdit, passEdit;
    private String email,pass;
    private View parentLayout;
    private TextView forgetText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        mAuth = FirebaseAuth.getInstance();
        emailEdit = findViewById(R.id.email_EditText);
        passEdit = findViewById(R.id.pass_EditText);
        forgetText = findViewById(R.id.forgetPasswordTextView);
        forgetText.setOnClickListener(this);
        login = findViewById(R.id.loginPageBtn);
        login.setOnClickListener(this);
        parentLayout = findViewById(android.R.id.content);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onClick(View v)
    {
        int i = v.getId();
        if(i == R.id.loginPageBtn){
            email = emailEdit.getText().toString().trim();
            pass = passEdit.getText().toString();

            if (email.isEmpty()) {
                emailEdit.setError("Email Required");
                requestFocus(emailEdit);
                return;
            }
            if (pass.isEmpty()) {
                passEdit.setError("Password Required");
                requestFocus(passEdit);
                return;
            }
            showProgressDialog();
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LOGIN ACTIVITY", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LOGIN ACTIVITY",
                                        "signInWithEmail:failure"+ task.getException());
                                Snackbar.make(parentLayout, "Email or Password Incorrect",
                                        Snackbar.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                        }
                    });
            hideProgressDialog();
        }
        else if( i == R.id.forgetPasswordTextView){
            Intent forget = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
            startActivity(forget);
            finish();
        }
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
