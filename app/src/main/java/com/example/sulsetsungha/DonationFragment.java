package com.example.sulsetsungha;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DonationFragment extends Fragment {

    ArrayList<Sponsor> sponsors;
    ListView donationListView;
    private static DonationAdapter donationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = (View)inflater.inflate(R.layout.fragment_donation, container, false);

        //후원 리스트
        sponsors = new ArrayList<>();
        sponsors.add(new Sponsor("후원", "생리대가 필요하지만 살 수 없는 여성 청소년을 도와주세요.", Calendar.getInstance().getTime(), 1000000));
        sponsors.add(new Sponsor("후원", "생리대가 필요하지만 살 수 없는 여성 청소년을 도와주세요.", Calendar.getInstance().getTime(), 1000000));
        sponsors.add(new Sponsor("후원", "생리대가 필요하지만 살 수 없는 여성 청소년을 도와주세요.", Calendar.getInstance().getTime(), 1000000));

        donationListView = (ListView)view.findViewById(R.id.listView_donation);
        donationAdapter = new DonationAdapter(getContext(), sponsors);
        donationListView.setAdapter(donationAdapter);
        donationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //각 아이템을 분간 할 수 있는 position과 뷰
                String selectedItem = (String) view.findViewById(R.id.txtName).getTag().toString();
                Toast.makeText(getContext(), "Clicked: " + position +" " + selectedItem, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

//    @Override
//    public void onClick(View v) {
//        //value = Integer.parseInt(editText.getText().toString()); //문자형 -> 정수형 변환
//        //progressBar.setProgress(value);
//        //final EditText edtPoint = new EditText(getContext());
//
//        AlertDialog.Builder dlg = new AlertDialog.Builder(requireContext());
//        dlg.setTitle("후원을 진행하시겠습니까?");
//        //dlg.setView(edtPoint);
//        dlg.setIcon(R.drawable.ic_android_black_24dp);
//        dlg.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        dlg.setPositiveButton("후원", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//
//        dlg.show();
//    }

    class Sponsor {
        private String name;
        private String summary;
        private Date dday;
        private Integer donation;

        public Sponsor(String name, String summary, Date dday, Integer donation) {
            this.name = name;
            this.summary = summary;
            this.dday = dday;
            this.donation = donation;
        }

        public String getName() {
            return name;
        }

        public String getSummary() {
            return summary;
        }

        public Date getDday() {
            return dday;
        }

        public Integer getDonation() {
            return donation;
        }
    } //Sponsor

}