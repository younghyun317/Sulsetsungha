package com.example.sulsetsungha.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.sulsetsungha.CommunityWriteActivity;
import com.example.sulsetsungha.DonationFragment;
import com.example.sulsetsungha.DonationLikeActivity;
import com.example.sulsetsungha.R;

public class CommunityFragment extends Fragment {

    ImageButton btnCmnWrite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        btnCmnWrite = (ImageButton) view.findViewById(R.id.btnCmnWrite);
        btnCmnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity(), CommunityWriteActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        return view;
    }
}