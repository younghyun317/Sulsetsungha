package com.example.sulsetsungha.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MypageFragment extends Fragment{

    String TAG = MypageFragment.class.getSimpleName();
    String ID;

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
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
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
}