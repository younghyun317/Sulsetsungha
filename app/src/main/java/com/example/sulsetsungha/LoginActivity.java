package com.example.sulsetsungha;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sulsetsungha.Fragment.HomeFragment;
import com.google.android.gms.common.FirstPartyScopes;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;

public class LoginActivity extends AppCompatActivity {

    String TAG = LoginActivity.class.getSimpleName();

    EditText edtId, edtPw;
    ImageButton btnFind, btnJoin, btnLogin;


    double latitude=0;
    double longitude=0;

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        FirebaseApp.initializeApp(this);

        //notice ?????? ????????????
        FirebaseMessaging.getInstance().subscribeToTopic("notice").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(!task.isSuccessful()){ //fail
                    Log.d(TAG, "???????????? ????????????.");
                }
                else{ //success
                    Log.d(TAG,"???????????? ???.");
                }
            }
        });



//        FirebaseApp.initializeApp(this);


        edtId = findViewById(R.id.edtId);
        edtPw = findViewById(R.id.edtPw);
        btnFind = findViewById(R.id.btnFind);
        btnLogin = findViewById(R.id.btnLogin);
        btnJoin = findViewById(R.id.btnJoin);


//        FirebaseInstallations.getInstance().getId()
//                .addOnCom
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if(!task.isSuccessful()) {
//                            Log.w("FCM log", "getInstanceId failed", task.getException());
//                            return;
//                        }
//                        String token = task.getResult();
//                        Log.d("FCM log", "FCM token"+token);
//                        Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
//                    }
//                });
//        Intent fcm = new Intent(getApplicationContext(), MyFirebaseMessagingService.class);
//        startService(fcm);


        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://3.38.51.117:8000/login/";
        HashMap<String, String> login_json = new HashMap<>();

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(LoginActivity.this,JoinActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "lgoin url : " +  url);

                login_json.put("username", edtId.getText().toString());
                login_json.put("password", edtPw.getText().toString());

                JSONObject parameter = new JSONObject(login_json);

                // ????????? header??? ?????? ????????? user??? token??? ???????????? ?????? sharedpreference
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("my_name", edtId.getText().toString()).apply();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        url,
                        parameter,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Log.d(TAG, "response : " + response);
                                try {
                                    // response?????? access toekn??? ????????? sharedpreference??? access_token?????? key??? ??????
                                    String token = response.getString("access");
                                    editor.putString("access_token", token).apply();
                                    Log.d(TAG, "editor : " + editor.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Toast toast = Toast.makeText(getApplicationContext(), "??????????????? " + edtId.getText().toString() + "???!", Toast.LENGTH_LONG);
                                toast.show();
                                gpsTracker = new GpsTracker(LoginActivity.this);
                                latitude = Double.valueOf("37.56");//gpsTracker.getLatitude();//Double.valueOf("37.6248");
                                longitude = Double.valueOf("126.97");//gpsTracker.getLongitude();//Double.valueOf("127.0892");
                                SetFirstCurrentLocation(latitude, longitude);
//                                getLastLendState();

                                FirebaseMessaging.getInstance().subscribeToTopic(edtId.getText().toString())
                                        .addOnCompleteListener( task -> {
                                            if (task.isComplete()) Log.d(TAG, "?????? ??????");
                                            else Log.d(TAG, "?????? ??????");
                                        });

                                Intent i= new Intent(LoginActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(getApplicationContext(), "????????? ?????? ??????????????? ?????? ??????????????????.", Toast.LENGTH_LONG);
                                toast.show();

                                //edtId.setText("");

                                error.printStackTrace();
                                Log.d(TAG, "Login Error");
                            }
                        });

                queue.add(jsonObjectRequest);


            }

        });

    }



    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????

            boolean check_result = true;


            // ?????? ???????????? ??????????????? ???????????????.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //?????? ?????? ????????? ??? ??????
                ;
            }
            else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(LoginActivity.this, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(LoginActivity.this, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. ?????? ???????????? ????????? ?????????
            // ( ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.)


            // 3.  ?????? ?????? ????????? ??? ??????



        } else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.

            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ????????????.
                Toast.makeText(LoginActivity.this, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(LoginActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(LoginActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS ????????? ?????????");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //?????? ?????? ???, ????????? ?????? ????????????
    public void GetLastCurrentLocation(){

    }

    //?????? ?????? ???, ?????? ?????? ????????? ?????????
    public void SetFirstCurrentLocation(double lat, double lng) {
        //?????? ?????? ??????
        String currentLat = String.valueOf(lat);//String.valueOf(location.getLatitude());
        String currentLng = String.valueOf(lng);//String.valueOf(location.getLongitude());
        String user_location = currentLat + ", " + currentLng;
        Log.d(TAG, "user_location : " + user_location);

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://3.38.51.117:8000/user/location/";

        HashMap<String, String> location_json = new HashMap<>();
        location_json.put("location", user_location.toString());
        JSONObject parameter = new JSONObject(location_json);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString("access_token", null);
        Log.d(TAG, "token : " + token);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "first location response : " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast toast = Toast.makeText(getContext(), "server update error", Toast.LENGTH_LONG);
//                        toast.show();
//
                        error.printStackTrace();
                        Log.d("Update first location==>", "Location Update FAIL");
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return give_token(token);
            }
        };

        queue.add(jsonObjectRequest);

    }
    //????????? ???????????? ?????? ????????????
    public void getLastLendState() {


        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final String url = "http://3.38.51.117:8000/users/";

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sharedPreferences.getString("access_token", null);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d(TAG, "response : " + response.getJSONObject(0).getString("point").toString());
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
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("lend_state", lend_state);

                            Log.d("lend_state==>","lend state : "+lend_state);

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

    // ??? ????????? ????????? give_token ?????? ???????????? ?????? ?????? ??? ???????????? ???
    Map<String, String> give_token(String token) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        return headers;
    }




}