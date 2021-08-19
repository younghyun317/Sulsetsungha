package com.example.sulsetsungha.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sulsetsungha.ChattingActivity;
import com.example.sulsetsungha.R;
import com.example.sulsetsungha.community.CommunityDetailActivity;
import com.example.sulsetsungha.community.CommunityWriteActivity;
import com.example.sulsetsungha.community.MyPostActivity;
import com.example.sulsetsungha.donation.DonationAdapter;
import com.example.sulsetsungha.donation.DonationFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MypageFragment extends Fragment{

    String TAG = MypageFragment.class.getSimpleName();

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    String today = dateFormat.format(Calendar.getInstance().getTime());
    ArrayList<MypageFragment.Sponsor> sponsors;
    ListView mypageListView;
    private static MypageAdapter mypageAdapter;

    String ID;

    private TextView txtMyId, txtMyPoint, txtMyLendCnt, txtMyCanLendCnt, txtMyBorrowCnt;
    private ImageButton btnSetting, btnMyPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = (View)inflater.inflate(R.layout.fragment_mypage, container, false);

        txtMyId = (TextView) view.findViewById(R.id.txtMyId);
        txtMyPoint = (TextView) view.findViewById(R.id.txtMyPoint);
        txtMyLendCnt = (TextView) view.findViewById(R.id.txtMyLendCnt);
        txtMyCanLendCnt = (TextView) view.findViewById(R.id.txtMyCanLendCnt);
        txtMyBorrowCnt = (TextView) view.findViewById(R.id.txtMyBorrowCnt);

        btnSetting = (ImageButton) view.findViewById(R.id.btnSetting);

        getUserInfomation(); //사용자 정보 가져오기
        getUserLend(); //사용자 빌려준 횟수 가져오기
        getUserBorrow(); //사용자 빌린 횟수 가져오

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChattingActivity.class);
                startActivity(intent);
            }
        });

        btnMyPost = (ImageButton) view.findViewById(R.id.btnMyPost);
        btnMyPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyPostActivity.class);
                intent.putExtra("ID", ID);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(intent);

//                Intent i= new Intent(getActivity(), MyPostActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
            }
        });

        //기부 내역 불러오기
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url = "http://3.38.51.117:8000/donation_user/";

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = sharedPreferences.getString("access_token", null);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "response: " + response);
                        try {
                            //후원 리스트
                            sponsors = new ArrayList<>();
                            //Log.d("response", "response : " + response.getJSONObject(0).getString("company").toString());
                            //Log.d("length", "length : " + response.getJSONArray(1).toString());

                            String id, company, title, context, deadline;
                            long dday, target_amount, current_amount;
                            double percent;

                            //int percent;
                            Date currentCal, targetCal; //현재 날짜, 비교 날짜

                            for (int i=0; i < response.length(); i++) {
                                id = response.getJSONObject(i).getString("id").toString();
                                company = response.getJSONObject(i).getString("company").toString();
                                title = response.getJSONObject(i).getString("title").toString();
                                context = response.getJSONObject(i).getString("context").toString();
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

                                sponsors.add(new Sponsor(id, company, title, context, Long.toString(dday), Double.toString(percent)));
                            }

                            mypageListView = (ListView)view.findViewById(R.id.listView_MyDonation);
                            mypageAdapter = new MypageAdapter(getContext(), sponsors);
                            mypageListView.setAdapter(mypageAdapter);

                            mypageAdapter.notifyDataSetChanged();//갱신하기

                            mypageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return give_token(token);
            }
        };

        queue.add(jsonArrayRequest);

        return view;
    }

    private void getUserInfomation() {
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url = "http://3.38.51.117:8000/users/";

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = sharedPreferences.getString("access_token", null);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d(TAG, "response : " + response.getJSONObject(0).getString("point").toString());
                            txtMyId.setText(response.getJSONObject(0).getString("nickname").toString());
                            txtMyPoint.setText(response.getJSONObject(0).getString("point"));
                            txtMyCanLendCnt.setText(response.getJSONObject(0).getString("can_borrow"));

                            ID = response.getJSONObject(0).getJSONObject("user").getString("username").toString();
//                            Intent intent = new Intent(getContext(), MyPostActivity.class);
//                            intent.putExtra("ID", response.getJSONObject(0).getJSONObject("user").getString("username").toString());
//                            //getContext().startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return give_token(token);
            }
        };

        queue.add(jsonArrayRequest);
    }

    private void getUserBorrow() {
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url = "http://3.38.51.117:8000/get/borrow/";

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = sharedPreferences.getString("access_token", null);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "response : " + response.length());
                        txtMyBorrowCnt.setText(String.valueOf(response.length()));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return give_token(token);
            }
        };

        queue.add(jsonArrayRequest);
    }

    private void getUserLend() {
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url = "http://3.38.51.117:8000/get/lend/";

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = sharedPreferences.getString("access_token", null);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "response get lend : " + response.length());
                        txtMyLendCnt.setText(String.valueOf(response.length()));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return give_token(token);
            }
        };

        queue.add(jsonArrayRequest);

    }

    Map<String, String> give_token(String token) {
        HashMap<String, String> headers = new HashMap<>();
        // Bearer + token 해야됨! 안그럼 인식 못함
        headers.put("Authorization", "Bearer " + token);

        return headers;
    }

    class Sponsor {
        private String id;
        private String company;
        private String title;
        private String context;
        private String dday;
        private String donation;
        private int progressbar;
        //private ProgressBar progressbar;

        public Sponsor(String id, String company, String title, String context, String dday, String donation) {
            //this.request = request;
            this.id = id;
            this.company = company;
            this.title = title;
            this.context = context;
            this.dday = dday;
            this.donation = donation;
            //this.progressbar = progressbar;
        }

        public String getId() { return id; }

        public String getCompany() {
            return company;
        }

        public String getTitle() {
            return title;
        }

        public String getContext() { return context; }

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