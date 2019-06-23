package com.dscddu.dscddu.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dscddu.dscddu.Fragments.EventHistoryFragment;
import com.dscddu.dscddu.Model_Class.EventHistoryModel;
import com.dscddu.dscddu.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class EventHistoryAdapter extends FirestoreRecyclerAdapter<EventHistoryModel,
        EventHistoryAdapter.EventHistoryHolder> {
        private Context context;



    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public EventHistoryAdapter(Context context,
                               @NonNull FirestoreRecyclerOptions<EventHistoryModel> options) {
        super(options);
        this.context = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull EventHistoryHolder holder, int position, @NonNull EventHistoryModel model) {
        holder.eventName.setText(model.getEventName());
        if(model.isAttended()){
            holder.attend.setText("You have Attended");
        }
        else{
            holder.attend.setText("Not Attended");

        }
    }

    @NonNull
    @Override
    public EventHistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_history_card_layout, viewGroup,
                false);
        return new EventHistoryHolder(view);
    }


    public class EventHistoryHolder extends RecyclerView.ViewHolder{
            private View view;
            private TextView eventName,attend;

            public EventHistoryHolder(@NonNull View itemView) {
                super(itemView);
                view = itemView;
                eventName = itemView.findViewById(R.id.history_eventTitle);
                attend = itemView.findViewById(R.id.history_attendence);
//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int position = getAdapterPosition();
//                        if(position != RecyclerView.NO_POSITION && listener != null)
//                            listener.onItemClick(getSnapshots().getSnapshot(position),position);
//                    }
//                });


            }
        }

//        public interface OnItemClickListener {
//            void onItemClick(DocumentSnapshot documentSnapshot, int position);
//        }
//            public EventAdapter.OnItemClickListener listener;
//
//            public void setOnItemClickListener(EventAdapter.OnItemClickListener listener) {
//                this.listener = listener;
//            }
}