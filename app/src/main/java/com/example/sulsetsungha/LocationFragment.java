package com.example.sulsetsungha;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;


public class LocationFragment extends Fragment {

    private static LocationAdapter locationAdapter;

    ArrayList<Data> itemLocation;

    Button btn_go2Map;


    public LocationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location, container, false);

        btn_go2Map = v.findViewById(R.id.btn_go2Map);
        btn_go2Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 버튼 클릭 시, 지도로 돌아가기
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.layout_location, new HomeFragment())
                        .commit();
                btn_go2Map.setVisibility(View.GONE);

                Log.d( "[지도로 돌아가기 버튼 동작]", "gotoMap : "+"버튼 동작 아주 잘됨~!~!");
            }
        });

        itemLocation = new ArrayList<>();
        int distance=50;

        for(int i=0;i<10;i++){
            distance++;
            itemLocation.add(new Data(i, "빌려줄 수 있어요", distance));
        }

        RecyclerView locationList = v.findViewById(R.id.locationList);

        locationAdapter = new LocationAdapter();
        locationList.setAdapter(locationAdapter);
        locationList.setLayoutManager(new LinearLayoutManager(getContext()));


        locationAdapter.setLocationList(itemLocation);


        // Inflate the layout for this fragment
        return v;
    }
}