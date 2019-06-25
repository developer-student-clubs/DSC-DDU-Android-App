package com.dscddu.dscddu.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dscddu.dscddu.Adapters.EventAdapter;
import com.dscddu.dscddu.Listeners.FragmentActionListener;
import com.dscddu.dscddu.Listeners.InternetCheck;
import com.dscddu.dscddu.Model_Class.EventModel;
import com.dscddu.dscddu.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class HomeFragment extends Fragment {
    private Context con;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseFirestore rootRef;
    private View rootView;
    private RecyclerView recyclerView;
    private FragmentActionListener fragmentActionListener;
    private EventAdapter adapter;
    private TextView empty;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Events");
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
    public void setFragmentActionListener(FragmentActionListener fragmentActionListener){
        this.fragmentActionListener = fragmentActionListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initUIElements();
        return rootView;
    }

    private void initUIElements() {
        con = getContext();
        linearLayoutManager = new LinearLayoutManager(con);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        rootRef = FirebaseFirestore.getInstance();
        empty = rootView.findViewById(R.id.emptyViewEventsList);
        empty.setVisibility(View.INVISIBLE);
        Query query = rootRef.collection("events")
                .orderBy("postedOn", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<EventModel> options = new FirestoreRecyclerOptions.Builder<EventModel>()
                .setQuery(query, EventModel.class)
                .build();
        adapter = new EventAdapter(con,options){
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount() == 0){
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else{
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            EventModel e = documentSnapshot.toObject(EventModel.class);
            String eName = e.getEventName();
            String docId = documentSnapshot.getId();
            if(fragmentActionListener!=null){
                Bundle bundle = new Bundle();
                bundle.putInt(FragmentActionListener.ACTION_KEY,
                        FragmentActionListener.ACTION_VALUE_EVENT_DETAILS);
                bundle.putString("docId",docId);
                bundle.putString("eName",eName);
                fragmentActionListener.actionPerformed(bundle);
            }
        });
        adapter.startListening();

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
