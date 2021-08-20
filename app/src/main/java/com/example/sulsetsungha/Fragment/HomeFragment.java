package com.example.sulsetsungha.Fragment;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sulsetsungha.GpsTracker;
import com.example.sulsetsungha.LocationFragment;
import com.example.sulsetsungha.MapFragment;
import com.example.sulsetsungha.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment /*implements MapFragment.OnTimePickerSetListener*/{

//    private static final int map_Fragment = 1;
//    private static final int loc_Fragment = 2;


    Switch swc_borrow;

    private MapFragment mapFr;
    private LocationFragment locationFr;

    Bundle bundle;
    String address;

    HashMap<String, String> borrow_json;
    GpsTracker gpsTracker;


    boolean cnt = true;

//    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.


//    public HomeFragment()
//    {
//        // required
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mapFr = new MapFragment();
        locationFr = new LocationFragment();

//        Bundle extra = this.getArguments();
//        if(extra != null) {
//            txt_address.setText(extra.getString("address"));
//        }
//        else {
//            txt_address.setText("no bundle");
//        }
<<<<<<< HEAD

        gpsTracker = new GpsTracker(getContext());
        LatLng latlng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        //txt_address.setText(getCurrentAddress(latlng));
        Log.d("Home Fragment==>","address:"+txt_address);
=======
//
//        gpsTracker = new GpsTracker(getContext());
//        LatLng latlng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
//        txt_address.setText(getCurrentAddress(latlng));
//        Log.d("Home Fragment==>","address:"+txt_address);
>>>>>>> b61dc8a17cee7152a92c292ce970142de81367a4

        getLastLendState();

        // 빌려줄 수 있음/없음 스위치 버튼
        swc_borrow = v.findViewById(R.id.swc_borrow);
        swc_borrow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final RequestQueue queue = Volley.newRequestQueue(getContext());
                final String url = "http://3.38.51.117:8000/update/borrowState/user/";

                borrow_json = new HashMap<>();

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String token = sharedPreferences.getString("access_token", null);
                Log.d(HomeFragment.class.getSimpleName(), "token : " + token);

                if(isChecked){

                    borrow_json.put("lend_state", "true");

                    JSONObject parameter = new JSONObject(borrow_json);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH,
                            url,
                            parameter,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("[빌려줄수 있음/없음]", "Update SUCCESS");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    error.printStackTrace();
                                    Log.d("[빌려줄수 있음/없음]", "Update FAIL");
                                }
                            })
                    {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            return give_token(token);
                        }
                };
                    queue.add(jsonObjectRequest);
//                    Toast.makeText(getContext(), "스위치 ON", Toast.LENGTH_SHORT).show();
                }
                else{
                    borrow_json.put("lend_state", "false");

                    JSONObject parameter = new JSONObject(borrow_json);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH,
                            url,
                            parameter,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("[빌려줄수 있음/없음]", "Update SUCCESS");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
//                                    Toast toast = Toast.makeText(getContext(), "server update error", Toast.LENGTH_LONG);
//                                    toast.show();

                                    error.printStackTrace();
                                    Log.d("[빌려줄수 있음/없음]", "Update FAIL");
                                }
                            })
                    {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            return give_token(token);
                        }
                    };
                    queue.add(jsonObjectRequest);
//                    Toast.makeText(getContext(), "스위치 OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        mLayout = layout.findViewById(R.id.layout_home);

        //MapFragment에서 전달된 데이터 받기
//        bundle = getArguments();
//
//        if (getArguments() != null) {
//            address = getArguments().getString("address");
//            txt_address.setText(address);
//        }

//        btn_request.setText("리스트 보기");
//        FragmentView(map_Fragment);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.layout_fr, mapFr)
                .commit();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

//    @Override
//    public void onTimePickerSet(String contents){
////        Intent it = new Intent(getApplicationContext(), PushActivity.class);
////        it.putExtra("내용", alarm);
////        startActivity(it);
////        Toast.makeText(getApplicationContext(), alarm, Toast.LENGTH_LONG).show();
//        txt_address.setText(contents);
//
//    }

    //현재 위치 주소 알아내기
    public String getCurrentAddress(LatLng latlng) {

        //지오코더 GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getContext(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getContext(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    //마지막 대여가능 상태 가져오기
    public void getLastLendState() {

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
                            Log.d("getLastLendState()==>", "response : " + response.getJSONObject(0).getString("lend_state").toString());
//                            txtMyId.setText(response.getJSONObject(0).getJSONObject("user").getString("username").toString());
//                            txtMyPoint.setText(response.getJSONObject(0).getString("point"));
//
                            String lend_state;
                            lend_state = response.getJSONObject(0).getString("lend_state");
//                            HomeFragment homeFragment = new HomeFragment();
//
//                            Bundle bundle = new Bundle();
//                            bundle.putString("lend_state",lend_state);
//                            homeFragment.setArguments(bundle);

                            Log.d("lend_state==>","lend state : "+lend_state);
                            if(lend_state == "true") {
                                swc_borrow.setChecked(true);
//                                Intent intent = new Intent(getActivity(), PushActivity.class);
//                                intent.putExtra("내용", "생리대 대여 알림");
//                                startActivity(intent);
                            }else {
                                swc_borrow.setChecked(false);
                            }

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

// 이 함수는 나중에 give_token 구현 필요라는 말이 있을 때 사용하면 됨
    Map<String, String> give_token(String token) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        return headers;
}





}

