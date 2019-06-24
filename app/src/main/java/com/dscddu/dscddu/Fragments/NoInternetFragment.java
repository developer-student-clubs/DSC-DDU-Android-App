package com.dscddu.dscddu.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dscddu.dscddu.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoInternetFragment extends Fragment {

    private Button close;
    public NoInternetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_no_internet, container, false);
        close = rootView.findViewById(R.id.closeInternetButton);
        close.setOnClickListener(v -> {
            getActivity().moveTaskToBack(true);
            getActivity().finishAndRemoveTask();

        });
        return rootView;
    }


}
