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

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DonationFragment extends Fragment {
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

                            for (int i=0; i < response.length(); i++) {
                                sponsors.add(new Sponsor(response.getJSONObject(i).getString("company").toString(),
                                                         response.getJSONObject(i).getString("title").toString(),
                                                         response.getJSONObject(i).getString("deadline").toString(),
                                                         response.getJSONObject(i).getString("target_amount").toString()));
                                //Log.d("sponsor", "sponsor: " + sponsors);
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
                        } catch (JSONException e) {
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
        //private JSONArray request;
        private String company;
        private String title;
        private String dday;
        private String donation;
        //private ProgressBar progressbar;

        public Sponsor(String company, String title, String dday, String donation) {
            //this.request = request;
            this.company = company;
            this.title = title;
            this.dday = dday;
            this.donation = donation;
            //this.progressbar = progressbar;
        }

//        public JSONArray getRequest() {
//            return request;
//        }


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
//
//        public ProgressBar getProgressbar() {
//            return progressbar;
//        }
    } //class Sponsor

}