package com.dscddu.dscddu.Activities;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import com.dscddu.dscddu.Listeners.InternetCheck;
import com.dscddu.dscddu.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
public class SignUpActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseFirestore db;

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient mGoogleSignInClient;

    private  View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        parentLayout = findViewById(android.R.id.content);
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        findViewById(R.id.signInButton).setOnClickListener(this);



        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        new InternetCheck(internet -> {
                if(!internet){
                    findViewById(R.id.signInButton).setEnabled(false);
                    Snackbar.make(findViewById(android.R.id.content),"Oops!! No Internet " +
                                    "Connections", Snackbar.LENGTH_INDEFINITE).setAction("Close",
                            v -> finishAndRemoveTask()).show();
                }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Google Sign In Failed.",
                        Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            Map<String, Object> user_data = new HashMap<>();
                            user_data.put("displayName",user.getDisplayName());
                            user_data.put("email",user.getEmail());
//                            db.collection("users").document(user.getUid())
//                                    .update(user_data)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
////                                            Toast.makeText(getApplicationContext(),"Added to user" +
////                                                    " Collection",Toast.LENGTH_SHORT).show();
//                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            updateUI(user);
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.w(TAG, "Error writing document", e);
//                                            Snackbar.make(findViewById(android.R.id.content),
//                                                    "Something Went Wrong",Snackbar.LENGTH_LONG).show();
//                                        }
//                                    });


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                //View parentLayout = findViewById(android.R.id.content);
                                Snackbar.make(parentLayout, "Authentication Failed.",
                                        Snackbar.LENGTH_SHORT).show();
                                    updateUI(null);
                            }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Not Use Full Here
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    //NOT USE FULL HERE
    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            boolean pageVisited = sharedPreferences.getBoolean("pageVisited",false);
            if(pageVisited){

                Intent home = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(home);
                finish();
            }
            else{
                DocumentReference docIdRef = db.collection("users").document(user.getUid());
                docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Document exists!");
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("pageVisited",true);
                                editor.apply();
                                Intent h = new Intent(getApplicationContext(),
                                        HomeActivity.class);
                                startActivity(h);
                                finish();
                            } else {
                                Log.d(TAG, "Document does not exist!");
                                Snackbar.make(findViewById(android.R.id.content),"Please fill up " +
                                        "your details",Snackbar.LENGTH_SHORT).show();
                                Intent userPage = new Intent(getApplicationContext(),UserDetailsActivity.class);
                                startActivity(userPage);
                                finish();
                            }
                        } else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });


            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signInButton) {
            signOut();
            signInWithGoogle();
        }
    }

}