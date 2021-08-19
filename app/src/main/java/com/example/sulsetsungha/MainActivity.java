package com.example.sulsetsungha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sulsetsungha.Fragment.HomeFragment;
import com.example.sulsetsungha.Fragment.MypageFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity /*implements MapFragment.OnTimePickerSetListener*/ {

    LinearLayout helpher;
    BottomNavigationView bottomNavigationView;
    DonationFragment donationFragment;
    //DonationAdapter donationAdapter;
    CommunityFragment communityFragment;
    Menu menu;
    String lend_state = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        SettingListener();

        //맨 처음 시작할 탭 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_home);

        //새로고침
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.detach(communityFragment).attach(communityFragment).commit();
//        ft.detach(donationFragment).attach(donationFragment).commit();
    }

    private void init() {
        helpher = findViewById(R.id.help_her);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        menu = bottomNavigationView.getMenu();
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
                    //메뉴 선택시 아이콘
                    menuItem.setIcon(R.drawable.menu_home_select);
                    menu.findItem(R.id.tab_donation).setIcon(R.drawable.menu_donation);
                    menu.findItem(R.id.tab_community).setIcon(R.drawable.menu_community);
                    menu.findItem(R.id.tab_mypage).setIcon(R.drawable.menu_mypage);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.help_her, new HomeFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_donation: {
                    menuItem.setIcon(R.drawable.menu_donation_select);
                    menu.findItem(R.id.tab_home).setIcon(R.drawable.menu_home);
                    menu.findItem(R.id.tab_community).setIcon(R.drawable.menu_community);
                    menu.findItem(R.id.tab_mypage).setIcon(R.drawable.menu_mypage);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.help_her, new DonationFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_community: {
                    menuItem.setIcon(R.drawable.menu_community_select);
                    menu.findItem(R.id.tab_donation).setIcon(R.drawable.menu_donation);
                    menu.findItem(R.id.tab_home).setIcon(R.drawable.menu_home);
                    menu.findItem(R.id.tab_mypage).setIcon(R.drawable.menu_mypage);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.help_her, new CommunityFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_mypage: {
                    menuItem.setIcon(R.drawable.menu_mypage_select);
                    menu.findItem(R.id.tab_donation).setIcon(R.drawable.menu_donation);
                    menu.findItem(R.id.tab_community).setIcon(R.drawable.menu_community);
                    menu.findItem(R.id.tab_home).setIcon(R.drawable.menu_home);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.help_her, new MypageFragment())
                            .commit();
                    return true;
                }
            }
            return false;
        }
    }//TabSelectedListener

//    @Override
    public void onTimePickerSet(LatLng location){
//        Toast.makeText(getApplicationContext(), contents, Toast.LENGTH_LONG).show();
//        HomeFragment fragment = new HomeFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("address", contents);
//
//        fragment.setArguments(bundle);
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