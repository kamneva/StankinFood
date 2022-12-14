package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hackinhome2021_stankinfood.R;

public class NoEthernetConnectionFragment extends Fragment implements View.OnClickListener{

    public NoEthernetConnectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_ethernet_connection, container, false);
        Button buttonRetryConnection = view.findViewById(R.id.buttonRetryConnection);
        buttonRetryConnection.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        //TODO проверить соединение с интернетом
    }
}