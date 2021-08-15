package com.example.sulsetsungha.Fragment;

import android.app.DownloadManager;
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

        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//        txtMyId.setText(sharedPreferences.getString("username", null));
//        txtMyPoint.setText(sharedPreferences.getString("point", null));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = sharedPreferences.getString("access_token", null);

        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = "http://3.38.51.117:8000/users/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "token : " + token.toString());
                        Log.d(TAG, "response : " + response.toString());
//                        Log.d(TAG, "sharedPreferences : " + sharedPreferences.getString("access_token", null));
//                        txtMyId.setText(sharedPreferences.getString("access_token", null));
//                        txtMyPoint.setText(sharedPreferences.getString("refresh", null));

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return give_token(token);
            }
        };

        queue.add(jsonArrayRequest);
        Log.d(TAG, "jsonArrayRequest : " + jsonArrayRequest.toString());
        return view;
    }

    Map<String, String> give_token(String token) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer" + token);

        return headers;
    }

}