package com.dscddu.dscddu.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dscddu.dscddu.Adapters.EventHistoryAdapter;
import com.dscddu.dscddu.Listeners.FragmentActionListener;
import com.dscddu.dscddu.Model_Class.EventHistoryModel;
import com.dscddu.dscddu.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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
        Query query = rootRef.collection("users")
                .document(user.getUid()).collection("events");
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
        adapter.setOnItemClickListener(new EventHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClickQR(DocumentSnapshot documentSnapshot, int position, String qr) {
                String id = documentSnapshot.getId();
                DocumentReference path = documentSnapshot.getReference();
                //Toast.makeText(getContext(),qr,Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                LayoutInflater factory = LayoutInflater.from(getContext());
                final View view = factory.inflate(R.layout.barcode_image, null);
                ImageView imageView=view.findViewById(R.id.dialog_imageview);
                String text=qr;
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,1000,1000);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageView.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                alertDialog.setView(view);
                alertDialog.setTitle("QR Code");
                alertDialog.setPositiveButton("Done", (dialog, which) -> dialog.cancel());
                AlertDialog dialog = alertDialog.create();
                dialog.show();

            }

            @Override
            public void onItemClickFeed(DocumentSnapshot documentSnapshot, int position) {
                String id = documentSnapshot.getId();
                DocumentReference path = documentSnapshot.getReference();
            }
        });

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
        recyclerView.setAdapter(adapter);
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
