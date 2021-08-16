package com.example.sulsetsungha;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;


public class LocationFragment extends Fragment {

    private static LocationAdapter locationAdapter;

    ArrayList<LocationItem> itemLocation;

    //    TextView txt_address;
//    ImageButton btn_search;
    Button btn_request;
    Button btn_go2map;


    Bundle bundle;


    public LocationFragment() {
        // Required empty public constructor
    }

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location, container, false);

//        txt_address = v.findViewById(R.id.txt_address);
//        btn_search = v.findViewById(R.id.btn_search);
        btn_request = v.findViewById(R.id.btn_request);

        btn_go2map = v.findViewById(R.id.btn_go2map);
        btn_go2map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                MapFragment fr = new MapFragment();
                transaction.replace(R.id.layout_fr, fr)
                        .commit();
            }
        });

        itemLocation = new ArrayList<>();

        //MapFragment에서 전달된 데이터 받기
//        bundle = getArguments();

        if(getArguments()!=null) {
            ArrayList<Integer> possible = new ArrayList<>();
            possible = getArguments().getIntegerArrayList("possible_distances");
            for(int i=0;i<possible.size();i++){
                itemLocation.add(new LocationItem(i, "빌려줄 수 있어요", possible.get(i)));
            }
        }

//        int distance=50;
//
//        for(int i=0;i<10;i++){
//            distance++;
//            itemLocation.add(new LocationItem(i, "빌려줄 수 있어요", distance));
//        }

        RecyclerView locationList = v.findViewById(R.id.locationList);

        locationAdapter = new LocationAdapter();
        locationList.setAdapter(locationAdapter);
        locationList.setLayoutManager(new LinearLayoutManager(getContext()));


        locationAdapter.setLocationList(itemLocation);


        // Inflate the layout for this fragment
        return v;
    }
}