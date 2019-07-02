package com.dscddu.dscddu.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        holder.qrString.setOnClickListener(v -> {
            int position1 =holder.getAdapterPosition();
            if(position1 != RecyclerView.NO_POSITION && listener !=null)
                listener.onItemClickQR(getSnapshots().getSnapshot(position1), position1,model.getQrString());

        });
        holder.feedback.setOnClickListener(v -> {
            int position1 =holder.getAdapterPosition();
            if(position1 != RecyclerView.NO_POSITION && listener !=null)
                listener.onItemClickFeed(getSnapshots().getSnapshot(position1), position1);
        });

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
            private Button feedback,qrString;

            public EventHistoryHolder(@NonNull View itemView) {
                super(itemView);
                view = itemView;
                eventName = itemView.findViewById(R.id.history_eventTitle);
                attend = itemView.findViewById(R.id.history_attendence);
                feedback = itemView.findViewById(R.id.feedbackButton);
                qrString = itemView.findViewById(R.id.getQRcode);
            }
        }

        public interface OnItemClickListener {
            void onItemClickQR(DocumentSnapshot documentSnapshot, int position, String qr);
            void onItemClickFeed(DocumentSnapshot documentSnapshot, int position);

        }
        public OnItemClickListener listener;

        public void setOnItemClickListener(OnItemClickListener listener) {
                this.listener = listener;
        }
}