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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sulsetsungha.ChattingActivity;
import com.example.sulsetsungha.R;
import com.example.sulsetsungha.community.MyPostActivity;
import com.example.sulsetsungha.donation.DonationAdapter;
import com.example.sulsetsungha.donation.DonationFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MypageFragment extends Fragment{

    String TAG = MypageFragment.class.getSimpleName();
    String ID;

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    String today = dateFormat.format(Calendar.getInstance().getTime());
    //private RecyclerView mRecyclerView;
    private ArrayList<MypageRecyclerViewItem> mList;
    private MypageAdapter mypageAdapter;

    private TextView txtMyId, txtMyPoint, txtMyLendCnt, txtMyCanLendCnt, txtMyBorrowCnt;
    private ImageButton btnSetting, btnMyPost;
    private RecyclerView recycleView_MyDonation;

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

        recycleView_MyDonation = (RecyclerView) view.findViewById(R.id.recycleView_MyDonation);
        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleView_MyDonation.setLayoutManager(layoutManager);

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
                            mList = new ArrayList<>();
                            //Log.d("response", "response : " + response.getJSONObject(0).getString("company").toString());
                            //Log.d("length", "length : " + response.getJSONArray(1).toString());

                            String id, company, title, context, deadline;
                            long dday;
                            int amount;

                            //int percent;
                            Date currentCal, targetCal; //현재 날짜, 비교 날짜

                            for (int i=0; i < response.length(); i++) {
                                id = response.getJSONObject(i).getJSONObject("item").getString("id").toString();
                                company = response.getJSONObject(i).getJSONObject("item").getString("company").toString();
                                title = response.getJSONObject(i).getJSONObject("item").getString("title").toString();
                                context = response.getJSONObject(i).getJSONObject("item").getString("context").toString();
                                deadline = response.getJSONObject(i).getJSONObject("item").getString("deadline").toString();
                                amount = Integer.parseInt(response.getJSONObject(i).getString("amount"));

                                currentCal = dateFormat.parse(today);
                                targetCal = dateFormat.parse(deadline);

                                // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
                                // 연산결과 -950400000. long type 으로 return 된다.
                                long calDate = targetCal.getTime() - currentCal.getTime();
                                // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
                                // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
                                long calDateDays = calDate / ( 24*60*60*1000);

                                dday = Math.abs(calDateDays);

                                addItem(id, company, title, context, Long.toString(dday), amount);
                            }

                            mypageAdapter = new MypageAdapter(mList);
                            recycleView_MyDonation.setAdapter(mypageAdapter);
                            recycleView_MyDonation.setLayoutManager(new LinearLayoutManager(getActivity()));

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

    public void addItem(String id, String company, String title, String context, String dday, int amount){
        MypageRecyclerViewItem item = new MypageRecyclerViewItem();

        item.setId(id);
        item.setCompany(company);
        item.setTitle(title);
        item.setContext(context);
        item.setDday(dday);
        item.setAmount(amount);

        mList.add(item);
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
}