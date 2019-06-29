package com.dscddu.dscddu.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dscddu.dscddu.Listeners.FragmentActionListener;
import com.dscddu.dscddu.R;
import com.google.firebase.auth.FirebaseAuth;
/**
 * msgInt == 0 --> Success
 * msgInt == 1 --> Already Registered
 * msgInt == 2 --> some Error
 * msgInt == 3 --> No Seats Available
 * */
public class RegisterConfirmationDialog extends DialogFragment {

    private Button actionOk;
    private FirebaseAuth mAuth;
    private int msg;
    private ImageView imageView;
    private TextView textMsg;
    private FragmentActionListener fragmentActionListener;

    public RegisterConfirmationDialog(){

    }
    public void setFragmentActionListener(FragmentActionListener fragmentActionListener)
    {
        this.fragmentActionListener = fragmentActionListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirmed,container,false);

        actionOk = view.findViewById(R.id.confirmOk);
        imageView = view.findViewById(R.id.dialogImage);
        textMsg = view.findViewById(R.id.textMesg);
        mAuth = FirebaseAuth.getInstance();
        Bundle bundle = getArguments();
        msg = bundle.getInt(FragmentActionListener.REGISTRATION_MSG);
        switch (msg){
            case 0:
                imageView.setBackgroundResource(R.drawable.ic_check_circle_black_24dp);
                textMsg.setText(R.string.register_success);
                break;
            case 1:
                imageView.setBackgroundResource(R.drawable.ic_error);
                textMsg.setText(R.string.register_alredyRegistered);
                break;
            case 2:
                imageView.setBackgroundResource(R.drawable.ic_error);
                textMsg.setText(R.string.register_error);
                break;
            case 3:
                imageView.setBackgroundResource(R.drawable.ic_error);
                textMsg.setText(R.string.register_noseats);
                break;
        }
        actionOk.setOnClickListener(v -> {
            getDialog().dismiss();
            if(fragmentActionListener!=null){
                Bundle b = new Bundle();
                b.putInt(FragmentActionListener.ACTION_KEY,FragmentActionListener.ACTION_VALUE_BACK_TO_HOME);
                fragmentActionListener.actionPerformed(b);
            }

        });
        return view;
    }



}
