package com.dscddu.dscddu.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dscddu.dscddu.Adapters.EventAdapter;
import com.dscddu.dscddu.Adapters.EventHistoryAdapter;
import com.dscddu.dscddu.Listeners.FragmentActionListener;
import com.dscddu.dscddu.Model_Class.EventHistoryModel;
import com.dscddu.dscddu.Model_Class.EventModel;
import com.dscddu.dscddu.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventHistoryFragment extends Fragment {
    private Context con;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseFirestore rootRef;
    private View rootView;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private FragmentActionListener fragmentActionListener;
    private EventHistoryAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public void setFragmentActionListener(FragmentActionListener fragmentActionListener){
        this.fragmentActionListener = fragmentActionListener;
    }
    public EventHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_history, container, false);
        initUI();
        return rootView;
    }

    private void initUI() {
        con = getContext();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        linearLayoutManager = new LinearLayoutManager(con);
        recyclerView = rootView.findViewById(R.id.eventHistoryRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        emptyView = rootView.findViewById(R.id.emptyViewHistory);
        rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("users").document(user.getUid()).collection("events");
//                .orderBy("postedOn", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<EventHistoryModel> options =
                new FirestoreRecyclerOptions.Builder<EventHistoryModel>()
                .setQuery(query, EventHistoryModel.class)
                .build();
        adapter = new EventHistoryAdapter(con,options){
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount() == 0){
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else{
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
//add the listener for the single value event that will function
//like a completion listener for initial data load of the FirebaseRecyclerAdapter
//        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //onDataChange called so remove progress bar
//
//                //make a call to dataSnapshot.hasChildren() and based
//                //on returned value show/hide empty view
//
//                //use helper method to add an Observer to RecyclerView
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        adapter.setOnItemClickListener((documentSnapshot, position) -> {
//            EventModel e = documentSnapshot.toObject(EventModel.class);
//            String eName = e.getEventName();
//            String docId = documentSnapshot.getId();
//            if(fragmentActionListener!=null){
//                Bundle bundle = new Bundle();
//                bundle.putInt(FragmentActionListener.ACTION_KEY,
//                        FragmentActionListener.ACTION_VALUE_EVENT_DETAILS);
//                bundle.putString("docId",docId);
//                bundle.putString("eName",eName);
//                fragmentActionListener.actionPerformed(bundle);
//            }
//        });
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
