package com.example.sulsetsungha;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sulsetsungha.DonationAdapter;
import com.example.sulsetsungha.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class DonationFragment extends Fragment {

    String TAG = DonationFragment.class.getSimpleName();

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    String today = dateFormat.format(Calendar.getInstance().getTime());
    ArrayList<Sponsor> sponsors;
    ListView donationListView;
    private static DonationAdapter donationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = (View)inflater.inflate(R.layout.fragment_donation, container, false);

        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = "http://3.38.51.117:8000/donation/";



        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            //후원 리스트
                            sponsors = new ArrayList<>();
                            //Log.d("response", "response : " + response.getJSONObject(0).getString("company").toString());
                            //Log.d("length", "length : " + response.getJSONArray(1).toString());

                            String company, title, deadline;
                            long dday, target_amount, current_amount;
                            double percent;

                            //int percent;
                            Date currentCal, targetCal; //현재 날짜, 비교 날짜

                            for (int i=0; i < response.length(); i++) {
                                company = response.getJSONObject(i).getString("company").toString();
                                title = response.getJSONObject(i).getString("title").toString();
                                deadline = response.getJSONObject(i).getString("deadline").toString();
                                target_amount = Long.parseLong(response.getJSONObject(i).getString("target_amount"));
                                current_amount = Long.parseLong(response.getJSONObject(i).getString("current_amount"));

                                //percent = Math.round((current_amount/target_amount)*100);
                                percent = ((current_amount * 1.0)/target_amount)*100;
                                Log.d(TAG, "current_amount : " + String.valueOf(current_amount).toString());
                                Log.d(TAG, "target_amount : " + String.valueOf(target_amount).toString());
                                Log.d(TAG, "percent : " + String.valueOf(percent).toString());
                                currentCal = dateFormat.parse(today);
                                targetCal = dateFormat.parse(deadline);

                                // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
                                // 연산결과 -950400000. long type 으로 return 된다.
                                long calDate = targetCal.getTime() - currentCal.getTime();
                                // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
                                // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
                                long calDateDays = calDate / ( 24*60*60*1000);

                                dday = Math.abs(calDateDays);

//                                Log.d(TAG, "calDateDays : " + calDateDays);
//                                Log.d(TAG, "percent : " + percent);

                                sponsors.add(new Sponsor(company, title, Long.toString(dday), Double.toString(percent)));
                            }
//                            sponsors.add(new Sponsor(response.getJSONArray(0).getJSONObject(0).toString(), response.getJSONArray(0).getJSONObject(1).toString(), today, "10"));
//                            sponsors.add(new Sponsor("후원2", "생리대가 필요하지만 살 수 없는 여성 청소년을 도와주세요.", today, "20"));
//                            sponsors.add(new Sponsor("후원3", "생리대가 필요하지만 살 수 없는 여성 청소년을 도와주세요.", today, "30"));
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
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        queue.add(jsonArrayRequest);

        return view;
    }

    class Sponsor {
        private String company;
        private String title;
        private String dday;
        private String donation;
        private int progressbar;
        //private ProgressBar progressbar;

        public Sponsor(String company, String title, String dday, String donation) {
            //this.request = request;
            this.company = company;
            this.title = title;
            this.dday = dday;
            this.donation = donation;
            //this.progressbar = progressbar;
        }

        public String getCompany() {
            return company;
        }

        public String getTitle() {
            return title;
        }

        public String getDday() {
            return dday;
        }

        public String getDonation() {
            return donation;
        }

        public int getProgressbar() {
            return progressbar;
        }
    } //class Sponsor

}