package com.dscddu.dscddu.Fragments;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dscddu.dscddu.Listeners.FragmentActionListener;
import com.dscddu.dscddu.Listeners.InternetCheck;
import com.dscddu.dscddu.Model_Class.ProfileModel;
import com.dscddu.dscddu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Hashtable;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class ProfileFragment extends Fragment {

    private View rootView;
    private TextView name,email,branch,collegeid,lname,fname,phone,sem;
    private ImageView imageView;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FragmentActionListener fragmentActionListener;
    private Hashtable<Double,String> branchTable;
    public ProfileFragment() {
        // Required empty public constructor
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener){
        this.fragmentActionListener = fragmentActionListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI();
        return rootView;
    }

    private void initUI() {
        branchTable = new Hashtable<Double, String>()
        {{
            put(1.0,"CE");
            put( 2.0,"IT");
            put(3.0,"EC");
            put( 4.0,"IC");
            put(5.0,"CH");
        }};
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        name = rootView.findViewById(R.id.profile_name);
        imageView = rootView.findViewById(R.id.profile_photo);
        collegeid = rootView.findViewById(R.id.profile_collegeid);
        branch = rootView.findViewById(R.id.profile_branch);
        email = rootView.findViewById(R.id.profile_email);
        fname = rootView.findViewById(R.id.profile_fname);
        lname = rootView.findViewById(R.id.profile_lname);
        phone = rootView.findViewById(R.id.profile_phone);
        sem = rootView.findViewById(R.id.profile_sem);

        readData(profileModel -> {

            name.setText(user.getDisplayName());
            try {
                Glide.with(Objects.requireNonNull(getContext())).load(user.getPhotoUrl()).into(imageView);
            }catch (NullPointerException e){
                Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(android.R.id.content),"Something went " +
                        "Wrong",Snackbar.LENGTH_LONG).show();
            }
            collegeid.setText(profileModel.getCollegeId());
            branch.setText(profileModel.getBranch());
            email.setText(user.getEmail());
            fname.setText(profileModel.getFirstName());
            lname.setText(profileModel.getLastName());
            phone.setText(profileModel.getPhoneNumber());
            sem.setText(String.valueOf( profileModel.getSem().intValue()));
        });

    }

    private void readData(FirestoreCallback firestoreCallback){
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (Objects.requireNonNull(document).exists()) {
                    try {
                        ProfileModel details = new ProfileModel();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        details.setFirstName((String)document.get("firstName"));
                        details.setLastName((String) document.get("lastName"));
                        details.setPhoneNumber((String) document.get("phoneNumber"));
                        details.setSem((Double) document.get("sem"));
                        details.setCollegeId((String) document.get("collegeId"));
                        details.setBranch(branchTable.get(document.get("branch")));

                        firestoreCallback.doCallback(details);

                    }catch (IllegalAccessError e){
                        Log.d(TAG, "Something is missing in document");
                        Snackbar.make(getActivity().findViewById(android.R.id.content),"Something Went Wrong",
                                Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "No such document");
                    Snackbar.make(getActivity().findViewById(android.R.id.content),"Something Went" +
                                    " Wrong",
                            Snackbar.LENGTH_LONG).show();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Snackbar.make(getActivity().findViewById(android.R.id.content),"Something Went " +
                                "Wrong",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private interface FirestoreCallback{
        void doCallback(ProfileModel profileModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        new InternetCheck(internet -> {
            if(!internet){
                if(fragmentActionListener!=null){
                    Bundle bundle = new Bundle();
                    bundle.putInt(FragmentActionListener.ACTION_KEY,
                            FragmentActionListener.ACTION_NO_INTERNET);
                    fragmentActionListener.actionPerformed(bundle);
                }
            }
        });
    }
}
