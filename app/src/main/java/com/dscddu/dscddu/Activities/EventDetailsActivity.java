package com.dscddu.dscddu.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dscddu.dscddu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EventDetails";
    private TextView desc, time, branch, sem, venue, bring, extra, date;
    private ImageView imageView;
    private Button register;
    private String docID,eventName;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        docID = getIntent().getStringExtra("docId");
        eventName = getIntent().getStringExtra("docName");

        getSupportActionBar().setTitle(eventName);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.eventImage);
        progressBar.setVisibility(View.INVISIBLE);
        desc = findViewById(R.id.eventDescription);
        time = findViewById(R.id.eventTimings);
        date = findViewById(R.id.eventDate);
        branch = findViewById(R.id.eventBranch);
        sem = findViewById(R.id.eventSem);
        venue = findViewById(R.id.eventVenue);
        bring = findViewById(R.id.eventBring);
        extra = findViewById(R.id.eventExtra);
        register = findViewById(R.id.registerEvent);
        register.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(user);
        progressBar.setVisibility(View.VISIBLE);
        final DocumentReference docRef = db.collection("events").document(docID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        desc.setText(document.get("description").toString());
                        time.setText(document.get("timings").toString());
                        branch.setText(document.get("branch").toString());
                        sem.setText(document.get("semester").toString());
                        venue.setText(document.get("venue").toString());
                        bring.setText(document.get("what_to_bring").toString());
                        extra.setText(document.get("extraInfo").toString());
                        date.setText(document.get("date").toString());
                        Glide.with(getApplicationContext()).load(document.get("imageUrl")).into(imageView);
                    } else {
                        Log.d(TAG, "No such document");
                        Snackbar.make(findViewById(android.R.id.content),"Something Went Wrong",
                                Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        progressBar.setVisibility(View.INVISIBLE);

    }

    private void updateUI(FirebaseUser user) {
        if(user == null){
            Intent i = new Intent(getApplicationContext(),SignUpActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerEvent:

                break;
        }
    }
}
