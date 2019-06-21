package com.dscddu.dscddu.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dscddu.dscddu.Fragments.HomeFragment;
import com.dscddu.dscddu.Model_Class.EventModel;
import com.dscddu.dscddu.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class EventAdapter extends FirestoreRecyclerAdapter<EventModel,EventAdapter.EventHolder> {
    private Context context;
    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent,
                false);
        return new EventHolder(view);
    }

    public EventAdapter(Context context,FirestoreRecyclerOptions<EventModel> options){
        super(options);
        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull EventModel model) {
        holder.eventName.setText(model.getEventName());
        Glide.with(context).load(model.getImageUrl()).into(holder.imageView);
    }


    public class EventHolder extends RecyclerView.ViewHolder{
        private View view;
        private TextView eventName;
        private ImageView imageView;

        public EventHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            eventName = itemView.findViewById(R.id.eventTitle);
            imageView = itemView.findViewById(R.id.imageEvent);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null)
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                }
            });


        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
