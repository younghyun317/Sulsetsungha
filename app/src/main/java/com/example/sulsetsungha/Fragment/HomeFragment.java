package com.example.sulsetsungha.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.sulsetsungha.LocationFragment;
import com.example.sulsetsungha.MapFragment;
import com.example.sulsetsungha.R;

public class HomeFragment extends Fragment {

//    private static final int map_Fragment = 1;
//    private static final int loc_Fragment = 2;


    TextView txt_address;
    ImageButton btn_search;

    private MapFragment mapFr;
    private LocationFragment locationFr;

    Bundle bundle;
    String address;

    boolean cnt = true;

//    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.


//    public HomeFragment()
//    {
//        // required
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mapFr = new MapFragment();
        locationFr = new LocationFragment();



        txt_address = v.findViewById(R.id.txt_address);
        btn_search = v.findViewById(R.id.btn_search);


//        mLayout = layout.findViewById(R.id.layout_home);

        //MapFragment에서 전달된 데이터 받기
        bundle = getArguments();

        if (getArguments() != null) {
            address = getArguments().getString("address");
            txt_address.setText(address);
        }

//        btn_request.setText("리스트 보기");
//        FragmentView(map_Fragment);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.layout_fr, mapFr)
                .commit();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }



}

