package com.example.sulsetsungha.Fragment;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sulsetsungha.ChattingActivity;
import com.example.sulsetsungha.LoginActivity;
import com.example.sulsetsungha.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MypageFragment extends Fragment{

    String TAG = MypageFragment.class.getSimpleName();

    private TextView txtMyId, txtMyPoint, txtMyLendCnt, txtMyBorrowCnt;
    private Button btnDonationList, btnPointSave, btnShop, btnCmnMng;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = (View)inflater.inflate(R.layout.fragment_mypage, container, false);

        txtMyId = (TextView) view.findViewById(R.id.txtMyId);
        txtMyPoint = (TextView) view.findViewById(R.id.txtMyPoint);
        txtMyLendCnt = (TextView) view.findViewById(R.id.txtMyLendCnt);
        txtMyBorrowCnt = (TextView) view.findViewById(R.id.txtMyBorrowCnt);
        btnDonationList = (Button) view.findViewById(R.id.btnDonationList);
        btnPointSave = (Button) view.findViewById(R.id.btnPointSave);
        btnShop = (Button) view.findViewById(R.id.btnShop);
        btnCmnMng = (Button) view.findViewById(R.id.btnCmnMng);

        getUserInfomation(); //사용자 정보 가져오기
        getUserLend(); //사용자 빌려준 횟수 가져오기
        getUserBorrow(); //사용자 빌린 횟수 가져오

        btnShop = view.findViewById(R.id.btnShop);
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChattingActivity.class);
                startActivity(intent);
            }
        });

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
                            txtMyId.setText(response.getJSONObject(0).getJSONObject("user").getString("username").toString());
                            txtMyPoint.setText(response.getJSONObject(0).getString("point"));
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
//                        try {
//                            Log.d(TAG, "response : " + response.length());
//                            txtMyBorrowCnt.setText(response.length());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }

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