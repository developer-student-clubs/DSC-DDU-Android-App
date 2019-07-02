package com.dscddu.dscddu.Fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dscddu.dscddu.Listeners.FragmentActionListener;
import com.dscddu.dscddu.Listeners.InternetCheck;
import com.dscddu.dscddu.Model_Class.EventDetailsModel;
import com.dscddu.dscddu.Model_Class.ProfileModel;
import com.dscddu.dscddu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * msgInt == 0 --> Success
 * msgInt == 1 --> Already Registered
 * msgInt == 2 --> some Error
 * msgInt == 3 --> No Seats Available
 * */
/**
 * registerInt == 0 --> Not Registered
 * registerInt == 1 --> Already Registered
 * registerInt == 2 --> No Seats Available
 * */
public class EventDetailsFragment extends Fragment {
    private View rootView;
    private Context con;
    private StringBuilder s;
//    private Integer registerInt;
    private Hashtable<Double,String> branchTable;
    private static final String TAG = "EventDetails";
    private TextView desc, time, branch, sem, venue, bring, extra, date;
    private ImageView imageView;
    private Button register;
    private String docID,eventName;
    private ProgressBar progressBar,progressBarRegister;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    Integer registerInt;
    private ProfileModel details;
    private FirebaseUser user;
//    private Integer msgInt = 0;
    private FragmentActionListener fragmentActionListener;

    private static final long twepoch = 1288834974657L;
    private static final long sequenceBits = 17;
    private static final long sequenceMax = 65536;
    private static volatile long lastTimestamp = -1L;
    private static volatile long sequence = 0L;

    public EventDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_details, container, false);
        initUI();
        return rootView;
    }

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener){
        this.fragmentActionListener = fragmentActionListener;
    }

    private void initUI() {
        con = getContext();
        registerInt = 0;
        Bundle bundle = getArguments();
        assert bundle != null;
        docID = bundle.getString("docId");
        eventName = bundle.getString("eName");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        details = new ProfileModel();
        //For Branch
        branchTable = new Hashtable<Double, String>()
        {{
            put(1.0,"CE");
            put( 2.0,"IT");
            put(3.0,"EC");
            put( 4.0,"IC");
            put(5.0,"CH");
        }};

        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBarRegister = rootView.findViewById(R.id.registerProgress);
        progressBarRegister.setVisibility(View.VISIBLE);
        imageView = rootView.findViewById(R.id.eventImage);
        progressBar.setVisibility(View.INVISIBLE);
        desc = rootView.findViewById(R.id.eventDescription);
        time = rootView.findViewById(R.id.eventTimings);
        date = rootView.findViewById(R.id.eventDate);
        branch = rootView.findViewById(R.id.eventBranch);
        sem = rootView.findViewById(R.id.eventSem);
        venue = rootView.findViewById(R.id.eventVenue);
        bring = rootView.findViewById(R.id.eventBring);
        extra = rootView.findViewById(R.id.eventExtra);
        register = rootView.findViewById(R.id.registerEvent);
        register.setText(R.string.check_available);
        register.setEnabled(false);
        AlreadyAppliedTask task = new AlreadyAppliedTask();
        task.execute();

        readData(eventDetails -> {
            desc.setText(eventDetails.getDescription());
            time.setText(eventDetails.getTimings());
            branch.setText(eventDetails.getBranch());
            sem.setText(eventDetails.getSemester());
            venue.setText(eventDetails.getVenue());
            bring.setText(eventDetails.getWhat_to_bring());
            extra.setText(eventDetails.getExtraInfo());
            date.setText(eventDetails.getDate());
            Glide.with(con).load(eventDetails.getImageUrl()).into(imageView);
            progressBar.setVisibility(View.INVISIBLE);
        });

        register.setOnClickListener(v -> {
            progressBar.setVisibility(View.INVISIBLE);
            register.setEnabled(false);
            new InternetCheck(internet -> {
                if(!internet){
                    if(fragmentActionListener!=null){
                        Bundle bun = new Bundle();
                        bun.putInt(FragmentActionListener.ACTION_KEY,
                                FragmentActionListener.ACTION_NO_INTERNET);
                        fragmentActionListener.actionPerformed(bun);
                    }
                }
            });
            if(fragmentActionListener!=null){
                RegisterTask task1 = new RegisterTask();
                task1.execute();

            }

        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(eventName);
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

    private void readData(FirestoreCallback firestoreCallback){
        DocumentReference docRef = db.collection("events").document(docID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    try {
                        EventDetailsModel details = new EventDetailsModel();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        details.setDescription((String)document.get("description"));
                        details.setTimings((String) document.get("timings"));
                        details.setBranch((String) document.get("branch"));
                        details.setSemester((String) document.get("semester"));
                        details.setVenue((String) document.get("venue"));
                        details.setWhat_to_bring((String) document.get("what_to_bring"));
                        details.setExtraInfo((String) document.get("extraInfo"));
                        details.setDate((String) document.get("date"));
                        details.setImageUrl((String) document.get("imageUrl"));

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
        void doCallback(EventDetailsModel eventDetailsModel);
    }

    public class AlreadyAppliedTask extends AsyncTask<Void,Integer,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarRegister.setVisibility(View.VISIBLE);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            db.collection("events").document(docID).collection("participants").document(user.getUid())
                    .get().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    DocumentSnapshot document1 = task1.getResult();
                    if (document1.exists())
                    {
                        Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                        /**
                         * Already Applied to Event - return 1
                         * */
                        onProgressUpdate(1);

                    }
                    else
                    {
                        Log.d(TAG, "No such document");
                        /**
                         * NOT YET REGISTERED - return 0 -- but wait check availability
                         * */
                        DocumentReference docRef = db.collection("events").document(docID);
                        docRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    Double availableSeats = document.get("currentAvailable",Double.class);
//                                    Double totalSeats = document.get("totalSeats",Double.class);

//                                    if(totalSeats == 0){
//
//                                    }
                                    if(availableSeats <= 0){
                                        /**
                                         * NO SEATS AVAILABLE return 2
                                         * */
                                        onProgressUpdate(2);
                                    }else{
                                        /**
                                         *  SEATS AVAILABLE return 0
                                         * */
                                        onProgressUpdate(0);

                                    }

                                } else {
                                    Log.d(TAG, "No such document");
                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                            "Something Went Wrong",Snackbar.LENGTH_LONG).show();
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        "Something Went Wrong",Snackbar.LENGTH_LONG).show();
                            }
                        });

                    }
                } else {
                    Log.d(TAG, "get failed with ", task1.getException());
//                    Snackbar.make(getActivity().findViewById(android.R.id.content),
//                            "Something Went Wrong",Snackbar.LENGTH_LONG).show();
                }
            });



            return null;
        }
        /**
         * registerInt == 0 --> Not Registered
         * registerInt == 1 --> Already Registered
         * registerInt == 2 --> No Seats Available
         * registerInt == 3 --> EVent Registration Not Started
         * */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]){
                case 0:
                    register.setText(R.string.register_text_for_btn);
                    register.setEnabled(true);
                    Log.d("buttonhere","register button");
                    break;
                case 1:
                    register.setText(R.string.already_registered);
                    register.setEnabled(false);
                    Log.d("buttonhere","Already Registered");
                    break;
                case 2:
                    register.setText(R.string.no_seats);
                    register.setEnabled(false);
                    Log.d("buttonhere","No seats");
                    break;

            }
            progressBarRegister.setVisibility(View.INVISIBLE);
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public class RegisterTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarRegister.setVisibility(View.VISIBLE);
            register.setVisibility(View.INVISIBLE);

        }
        /**
         * msgInt == 0 --> Success
         * msgInt == 1 --> Already Registered
         * msgInt == 2 --> some Error
         * msgInt == 3 --> No Seats Available
         * msgInt == 4 --> Registration Not Yet Started --OR-- Registration CLOSED (NOT IMPLEMENTED)
         * */
        @Override
        protected Void doInBackground(Void... voids) {
            DocumentReference docRef = db.collection("events").document(docID).collection(
                    "participants").document(user.getUid());
            docRef.get().addOnCompleteListener(t -> {
                if (t.isSuccessful()) {
                    DocumentSnapshot doc = t.getResult();
                    if (doc.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + doc.getData());

                        /**
                         * USER IS ALREADY REGISTERED return 1
                         * */
                        onProgressUpdate(1);
                    } else
                        {
                        /**
                         * Test START
                         * */
                        DocumentReference userDetailsRef =
                                db.collection("users").document(user.getUid());
                            userDetailsRef.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                        details.setFirstName((String)document.get("firstName"));
                                        details.setLastName((String) document.get("lastName"));
                                        details.setPhoneNumber((String) document.get("phoneNumber"));
                                        details.setSem((Double) document.get("sem"));
                                        details.setCollegeId((String) document.get("collegeId"));
                                        details.setBranch(branchTable.get(document.get("branch")));
                                    } else {
                                        Log.d(TAG, "No such document");
                                        /**
                                         * SOME ERROR
                                         * */
                                        onProgressUpdate(2);
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                    /**
                                     * SOME ERROR
                                     * */
                                    onProgressUpdate(2);

                                }
                            });
                        final DocumentReference sfDocRef = db.collection("events").document(docID);

                            //TODO: Registration will OPEN SOON OPTION - by making totalSeats == 0
                            db.runTransaction(transaction -> {
                                DocumentSnapshot snapshot = transaction.get(sfDocRef);

//                                Double totalSeats = snapshot.getDouble("totalSeats");
                                Double availableSeats = snapshot.getDouble("currentAvailable");

                                Double varr;
                                if(availableSeats <= 0) {
                                    /**
                                     * NO SEATS AVAILABLE -- return 3
                                     * */
                                    varr = 3.0;
                                    transaction.update(sfDocRef, "currentAvailable",
                                            availableSeats);
                                    return varr;
                                }
                                if (availableSeats > 0) {



                                    //transaction.update(sfDocRef, "population", newPopulation);
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("attended", false);
                                    data.put("uid",user.getUid());
                                    data.put("firstName", details.getFirstName());
                                    data.put("lastName", details.getLastName());
                                    data.put("sem", details.getSem());
                                    data.put("branch", details.getBranch());
                                    data.put("phoneNumber", details.getPhoneNumber());
                                    data.put("collegeID", details.getCollegeId());

                                    String qrString = user.getEmail() + generateLongId();

                                    Map<String, Object> dataUser = new HashMap<>();
                                    dataUser.put("attended", false);
                                    dataUser.put("eventName", eventName);
                                    dataUser.put("qrCodeString", qrString);

//                                    AtomicBoolean flag = new AtomicBoolean(false);
                                    WriteBatch batch = db.batch();

                                    // Set the Participants in Particular Document ID
                                    DocumentReference nycRef = db.collection("events").document(docID).collection(
                                            "participants").document(qrString);
                                    batch.set(nycRef, data);

                                    // Set Event Name in Users Event Collection
                                    DocumentReference sfRef = db.collection("users").document(user.getUid()).collection(
                                            "events").document(docID);
                                    batch.set(sfRef, dataUser);
                                    batch.commit().addOnCompleteListener(task1 -> {

                                        /**
                                         * User Registration successful -- return 0
                                         * */
//                                        onProgressUpdate(0);
//                                        flag.set(true);
                                    });
//                                    //Update The Availability to new Value through transaction
//                                    DocumentReference Ref = db.collection("events").document(docID);
//                                    batch.update(Ref, "currentAvailable", availableSeats - 1);
                                    transaction.update(sfDocRef, "currentAvailable",
                                            availableSeats - 1);

                                    return 0.0;
                                }
                                return null;
                            })
                            .addOnSuccessListener(result ->{
                                    Log.d(TAG, "Transaction success: " + result);
                                    if(result == 3.0){
                                        /**
                                         * NO SEATS AVAILABLE -- return 3
                                         * */
                                        onProgressUpdate(3);

                                    }
                                    else if(result == 0.0){
                                        /**
                                         * User Registration successful -- return 0
                                         * */
                                        onProgressUpdate(0);
                                    }
                            })
                            .addOnFailureListener(e -> {
                                    /**
                                    * Some Error -- return 2
                                    * */
                                    onProgressUpdate(2);
                                    Log.w(TAG, "Transaction failure.", e);
                            });
                        /**
                         * Test OVER
                         * */
//                        DocumentReference docRef1 = db.collection("events").document(docID);
//                        docRef1.get().addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                DocumentSnapshot document = task.getResult();
//                                if (document.exists()) {
//                                    try{
//                                        Double totalSeats = document.get("totalSeats",Double.class);
//                                        Double availableSeats = document.get("currentAvailable",Double.class);
//                                        if(availableSeats <= 0)
//                                        {
//                                            /**
//                                             * NO SEATS AVAILABLE -- return 3
//                                             * */
//                                            msgInt =3;
//                                            onProgressUpdate(3);
//                                        }
//                                        else if(availableSeats > 0 && totalSeats > 0)
//                                        {
//
//                                            Map<String, Object> data = new HashMap<>();
//                                            data.put("attended", false);
//
//                                            Map<String, Object> dataUser = new HashMap<>();
//                                            dataUser.put("attended", false);
//                                            dataUser.put("eventName",eventName);
//
//                                            WriteBatch batch = db.batch();
//
//                                            // Set the Participants in Particular Document ID
//                                            DocumentReference nycRef = db.collection("events").document(docID).collection(
//                                                    "participants").document(user.getUid());
//                                            batch.set(nycRef,data);
//
//                                            // Set Event Name in Users Event Collection
//                                            DocumentReference sfRef = db.collection("users").document(user.getUid()).collection(
//                                                    "events").document(docID);
//                                            batch.set(sfRef,dataUser);
//
//                                            //Update The Availability to new Value
//                                            DocumentReference Ref = db.collection("events").document(docID);
//                                            batch.update(Ref,"currentAvailable",availableSeats-1);
//                                            // Commit the batch
//                                            batch.commit().addOnCompleteListener(task1 -> {
////                                                Toast.makeText(getContext(),"Batch Complete",Toast.LENGTH_SHORT).show();
//                                                /**
//                                                 * User Registration successful -- return 0
//                                                 * */
//                                                onProgressUpdate(0);
//                                            }).addOnFailureListener(e -> {
//                                                /**
//                                                 * Batch Write Failed -- return 2
//                                                 * */
//                                                onProgressUpdate(2);
//                                            });
//
//                                        }
//                                        else
//                                        {
//                                            Snackbar.make(getActivity().findViewById(android.R.id.content),
//                                                    "Something Went Wrong",Snackbar.LENGTH_LONG).show();
//                                            msgInt = 2;
//                                            /**
//                                             * Some Error Occured -- May be total seats <= 0
//                                             * return 2
//                                             * */
//                                            Log.d(TAG,"Some Error Occured -- May be total seats " +
//                                                    "<= 0");
//                                            onProgressUpdate(2);
//                                        }
//                                    }catch (NullPointerException e)
//                                        {
//                                        /**
//                                         * Error occur due to No Fields Found Corresponding to
//                                         * variables totalSeats and currentAvailable
//                                         *
//                                         * return 2
//                                         * */
//                                        Log.d(TAG,"No Fields Found Corresponding to variables " +
//                                                "totalSeats and currentAvailable: " + e);
//                                        onProgressUpdate(2);
//                                    }
////                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                                }
//                                else {
//                                    Log.d(TAG, "No such document");
//                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
//                                            "Something Went Wrong",Snackbar.LENGTH_LONG).show();
//                                    msgInt = 2;
//                                    /**
//                                     * Some Error Occured -- Document Doesn't Exist --- return 2
//                                     * */
//                                    onProgressUpdate(2);
//
//                                }
//                            } else {
//                                Log.d(TAG, "get failed with ", task.getException());
//                                Snackbar.make(getActivity().findViewById(android.R.id.content),
//                                        "Something Went Wrong",Snackbar.LENGTH_LONG).show();
//                                msgInt = 2;
//                                /**
//                                 * Some Error Occured -- Maybe due to Internet Connection -
//                                 * ---- return 2
//                                 * */
//                                onProgressUpdate(2);
//                            }
//                        });


                    }
                }
                else {
                    Log.d(TAG, "get failed with ", t.getException());
                    /**
                     * Some Error Occured -- return 2
                     * */
                    onProgressUpdate(2);
                }
            });

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            Bundle b = new Bundle();
            b.putInt(FragmentActionListener.REGISTRATION_MSG,values[0]);
            //TODO: change action_value_register to msgInt and make changes accordingly
            // and display error msg also and remove snakbar afterwards
            // and also check at load of event activity for already applied or not.
            b.putInt(FragmentActionListener.ACTION_KEY,FragmentActionListener.ACTION_VALUE_REGISTER);
            b.putString("docId",docID);
            fragmentActionListener.actionPerformed(b);
            progressBarRegister.setVisibility(View.INVISIBLE);
            register.setVisibility(View.VISIBLE);
            register.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }


    }

    private static synchronized Long generateLongId() {
        long timestamp = System.currentTimeMillis();
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) % sequenceMax;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        Long id = ((timestamp - twepoch) << sequenceBits) | sequence;
        return id;
    }

    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

}
