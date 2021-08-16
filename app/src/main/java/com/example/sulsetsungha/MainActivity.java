package com.example.sulsetsungha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sulsetsungha.Fragment.HomeFragment;
import com.example.sulsetsungha.Fragment.MypageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements MapFragment.OnTimePickerSetListener {

    LinearLayout helpher;
    BottomNavigationView bottomNavigationView;
    //DonationFragment donationFragment;
    //DonationAdapter donationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        SettingListener();

        //맨 처음 시작할 탭 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_home);
    }

    private void init() {
        helpher = findViewById(R.id.help_her);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void SettingListener() {
        //선택 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.tab_home: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.help_her, new HomeFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_donation: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.help_her, new DonationFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_community: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.help_her, new CommunityFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_mypage: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.help_her, new MypageFragment())
                            .commit();
                    return true;
                }
            }
            return false;
        }
    }//TabSelectedListener

    @Override
    public void onTimePickerSet(String contents){
//        Toast.makeText(getApplicationContext(), contents, Toast.LENGTH_LONG).show();
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("address", contents);

        fragment.setArguments(bundle);
    }

//    public void refesh() {
//        donationAdapter.notifyDataSetChanged();
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        donationAdapter.notifyDataSetChanged();;
//    }




}