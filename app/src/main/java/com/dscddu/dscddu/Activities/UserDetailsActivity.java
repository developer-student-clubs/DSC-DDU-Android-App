package com.dscddu.dscddu.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dscddu.dscddu.Listeners.InternetCheck;
import com.dscddu.dscddu.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UserActivity";
    public static final Integer STUDENT = 1;
    public static final Integer ORGANIZER = 2;

    private Button submit;
    private EditText fname,lname,phone,collegeID;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView privacyPolicy;
    private Spinner sem,branch;
    private CheckBox checkStudent, checkTerms;
    private FirebaseFirestore db;
    private Hashtable<String,Double> branchTable;
    private Hashtable<String,Double> semTable;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Let us know you Better");
        setContentView(R.layout.activity_user_details);
        sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        sem = findViewById(R.id.spinner_sem);
        branch = findViewById(R.id.spinner_branch);
        String[] branchName = { "Select Branch","CE", "IT", "EC", "IC", "CH"};
        String[] sems = {"Select Sem","1", "2", "3", "4", "5","6","7","8"};
        ArrayAdapter adapterBranch = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1 ,branchName);
        branch.setAdapter(adapterBranch);
        branchTable = new Hashtable<String, Double>()
        {{
            put("CE", 1.0);
            put("IT", 2.0);
            put("EC",3.0);
            put("IC", 4.0);
            put("CH", 5.0);
        }};
        semTable = new Hashtable<String, Double>()
        {{
            put("1", 1.0);
            put("2", 2.0);
            put("3", 3.0);
            put("4", 4.0);
            put("5", 5.0);
            put("6", 6.0);
            put("7", 7.0);
            put("8", 8.0);
        }};
        ArrayAdapter adapterSem = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1 ,sems);
        sem.setAdapter(adapterSem);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        fname = findViewById(R.id.register_fname);
        privacyPolicy = findViewById(R.id.privacyPolicy);
        privacyPolicy.setOnClickListener(this);
        lname = findViewById(R.id.register_lname);
        phone = findViewById(R.id.register_phone);
        collegeID = findViewById(R.id.register_collegeid);
        checkStudent = findViewById(R.id.checkStudent);
        checkTerms = findViewById(R.id.checkPrivacy);


        submit = findViewById(R.id.submit_register);
        submit.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
        updateUI(user);
        new InternetCheck(internet -> {
            if(!internet){
                submit.setEnabled(false);
                Snackbar.make(findViewById(android.R.id.content),"Oops!! No Internet " +
                        "Connections", Snackbar.LENGTH_INDEFINITE).setAction("Close",
                        v -> finishAndRemoveTask()).show();
            }
        });
    }
    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent i = new Intent(getApplicationContext(),SignUpActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.privacyPolicy:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1fEeUyUYV6U1ehHtcJV98xbGNlZthAbBLZoOz5eLBDiY/edit"));
                startActivity(browserIntent);
                break;
            case R.id.submit_register:
                String MobilePattern = "[0-9]{10}";

                if (fname.getText().toString().isEmpty()) {
                    fname.setError("First Name Required");
                    requestFocus(fname);
                    return;
                }
                if (lname.getText().toString().isEmpty()) {
                    lname.setError("Last Name Required");
                    requestFocus(lname);
                    return;
                }
                if (phone.getText().toString().isEmpty()) {
                    phone.setError("Phone Number Required");
                    requestFocus(phone);
                    return;
                }

                if (!phone.getText().toString().matches(MobilePattern)) {
                    phone.setError("Please enter valid 10 digit phone number");
                    requestFocus(phone);
                    return;
                }
                if(collegeID.getText().toString().isEmpty()) {
                    collegeID.setError("College ID Required");
                    requestFocus(collegeID);
                    return;
                }
                if (branch.getSelectedItem().toString().trim().equals("Select Branch")) {
                    Toast.makeText(this, "Please Select Branch", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sem.getSelectedItem().toString().trim().equals("Select Sem")) {
                    Toast.makeText(this, "Please Select Semester", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!checkStudent.isChecked()){
                    Snackbar.make(findViewById(android.R.id.content),"Please Confirm that you are" +
                            " DDU Student",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(!checkTerms.isChecked()){
                    Snackbar.make(findViewById(android.R.id.content),"Please accept Terms and " +
                            "Conditions",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Double branchNum = branchTable.get(branch.getSelectedItem().toString().trim());
                Double semNum = semTable.get(sem.getSelectedItem().toString().trim());
                Map<String, Object> data = new HashMap<>();
                data.put("firstName", fname.getText().toString() );
                data.put("lastName", lname.getText().toString());
                data.put("phoneNumber",phone.getText().toString());
                data.put("collegeId", collegeID.getText().toString());
                data.put("branch", branchNum);
                data.put("sem", semNum);
                data.put("roles",STUDENT);


                db.collection("users").document(user.getUid())
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("pageVisited",true);
                                editor.apply();
                                Intent home = new Intent(getApplicationContext(),HomeActivity.class);
                                startActivity(home);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                                Toast.makeText(getApplicationContext(), "ERROR IN UPDATING", Toast.LENGTH_SHORT).show();

                            }
                        });

                break;
        }
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
