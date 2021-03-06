package com.example.sulsetsungha;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapFragment extends Fragment implements OnMapReadyCallback , ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "[?????? ??????]";
    private static final int GPS_ENABLE_REQUEST_CODE=2001;
    private static final int UPDATE_INTERVAL_MS=1000;
    private static final int FASTEST_UPDATE_INTERVAL_MS=500;


    private GoogleMap mMap;
    private Marker currentMarker = null;
    List<Marker> cMarker = new ArrayList<>();


    //onRequestPermissionResult?????? ????????? ?????? ??? ActivityCompat.requestPermissions ????????? ????????? ?????? ??????
    private static final int PERMISSIONS_REQUEST_CODE=100;
    boolean needRequest=false;


    //Permission ??????
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // ?????? ?????????

    Location mCurrentLocation;
    LatLng currentPosition;

    Circle circle;
    CircleOptions circle500M;
    Bitmap icon;
    Bitmap icon2;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar ???????????? ???????????? View??? ???????????????.

    //LocationFragment??? ????????? ??????
    Bundle bundle;

    boolean check_result = true;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;

    String address;

    Button btn_go2List;
    Button btn_request;
    Button btn_gps;

//    private OnTimePickerSetListener onTimePickerSetListener;

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
        View v = inflater.inflate(R.layout.fragment_map, container, false);


        mLayout = v.findViewById(R.id.layout_map);

        btn_gps = v.findViewById(R.id.btn_gps);
        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = new LatLng(location.getLatitude(), location.getLongitude()); //?????? ???????????? ??????
                mMoveMapByAPI = true;
                setCurrentLocation(location);
            }
        });

        btn_request = v.findViewById(R.id.btn_request);
        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: ???????????? ?????? ?????? ?????????
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String token = sharedPreferences.getString("access_token", null);

                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                        "http://3.38.51.117:8000/send/borrow_notification/",
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d(TAG, "????????? ??????");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Log.d(TAG, "????????? ??????");
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return give_token(token);
                    }
                };

                Volley.newRequestQueue(getActivity()).add(request);

                LatLng center = new LatLng(location.getLatitude(), location.getLongitude());

                if(btn_request.getText().toString().equals("????????????")) {
                    btn_request.setText("????????????");
//                Intent intent = new Intent(getActivity(),PushActivity.class);
//                intent.putExtra("??????", "????????? ?????? ??????");
//                startActivity(intent);

                }
                else if(btn_request.getText().toString().equals("????????????")) {
                    btn_request.setText("????????????");

//                    mMap.clear();
//                    bundle = null;
//
//                    if(circle500M != null) {
//                        circle.remove();
////                        circle500M.center(center);
////                        circle = mMap.addCircle(circle500M);
//                    }

                }


//                onTimePickerSetListener.onTimePickerSet("????????? ?????? ????????? ???????????????!");
            }
        });

        btn_go2List = v.findViewById(R.id.btn_go2list);
        btn_go2List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationFragment fr = new LocationFragment();
                fr.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.layout_fr, fr)
                        .commit();

                bundle = null;
            }
        });

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        //????????? ?????? ????????????
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fr_map);
        mapFragment.getMapAsync(this);

//        getNearUser();

        return v;
    }

    //????????? ????????? ???????????? ??????
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            //TODO: ?????? ?????? ????????? ?????? ??????
            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {

                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                updateCurrentLocation(location); //?????? ?????? ?????? ????????????
                getNearUser(); //?????? ????????? ??????
                /**
                 * ???????????? ??? ??????????????? ??????!! Log ?????? ?????? ?????????!!!
                 */
                //?????? ??????
                String markerSnippet = "??????:" + String.valueOf(location.getLatitude()) + " ??????:" + String.valueOf(location.getLongitude());
                Log.d(TAG, "onLocationResult1 ==> " + markerSnippet);

                //?????? ?????? ??????
//                String currentLat = String.valueOf(location.getLatitude());
//                String currentLng = String.valueOf(location.getLongitude());
//                String user_location = currentLat + ","+currentLng;

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude()); //?????? ????????? ?????? ???????????? ??????


                //?????? ?????? ????????? ?????? ???????????? ??????
                setCurrentLocation(location);

                //?????? ??? ?????? ?????? ???????????? ??????
//                setLocation(locationMap);
//                Log.d("locationMap:", "?????? ?????? ?????? ????????? ==> "+locationMap);
//                locationMap.clear();

                mCurrentLocation = location;
            }

        }

    };

    //?????? ?????????
    public int getDistance(Location location , double lat , double lng){
        float distance;

        Location locationB = new Location("point B");
        locationB.setLatitude(lat);
        locationB.setLongitude(lng);

        distance = location.distanceTo(locationB);

        return (int)distance;
    }
    class toAsc implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return a.compareTo(b) ;
        }
    };

    //???????????? 500m??? ????????? ????????????
    public void getNearUser() {

        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url = "http://3.38.51.117:8000/user/location/";

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = sharedPreferences.getString("access_token", null);


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        boolean isThere=true;
                        //TODO: ???????????? ????????? ???????????? ??? ?????????
                        try {
                            if(response.length() != 0){
                                String uLatlng[];
                                double uLat;
                                double uLng;

                                ArrayList<LatLng> getUloc = new ArrayList<>();
                                ArrayList<String> getUname = new ArrayList<>();
                                ArrayList<String> nearUname = new ArrayList<>();
                                ArrayList<Integer> nearUdis = new ArrayList<>();
                                ArrayList<String> nearU = new ArrayList<>();

                                bundle = new Bundle();

                                String uLoc = null;
                                String uName = null;

                                for(int d=0;d<response.length();d++){
                                    Log.d("??? ????????????>>>", "response : "+response.getJSONObject(d).getString("location"));
//                                    getUloc.add(response.getJSONObject(d).getString("location"));
//                                    getUname.add(response.getJSONObject(d).getString("user"));
                                    uLoc = response.getJSONObject(d).getString("location");
                                    uName = response.getJSONObject(d).getString("user");

                                    Log.d("?????? ???????????? ??????==>","location = " + uLoc);

                                    uLatlng = uLoc.split(",");
                                    uLat = Double.valueOf(uLatlng[0]);
                                    uLng = Double.valueOf(uLatlng[1]);

                                    //?????? ?????? ?????? ???????????? ??????
                                    getUloc.add(new LatLng(uLat, uLng));
                                    getUname.add(uName);

                                    //???????????? ?????? ??????
                                    nearUname.add(uName);
                                    nearUdis.add(getDistance(location, uLat, uLng));

                                }

                                toAsc ascending = new toAsc();
                                Collections.sort(nearUdis, ascending);

                                for(int i =0;i<response.length();i++){
                                    nearU.add(nearUname.get(i));
                                    nearU.add(String.valueOf(nearUdis.get(i)));

                                }

                                setLocation(getUloc, getUname);

                                bundle.putStringArrayList("nearU", nearU);

                                Log.d("nearU==>", "nearU = "+nearU);
                                Log.d("getUloc==>","getUloc size = "+getUloc.size());


                            }
                            else { //????????? ??????

//                                Toast.makeText(getContext(), "?????? ????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                                Log.d("????????? ?????? ?????????","??????????????????==>"+response.length());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast toast = Toast.makeText(getContext(), "server update error", Toast.LENGTH_LONG);
//                        toast.show();

                        error.printStackTrace();
//                        Log.d(MapFragment.class.getSimpleName(), "500m ?????? ??? FAIL");
                        Log.d("[500m ??????]", "Server Update FAIL");

                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return give_token(token);
            }
        };



        queue.add(jsonArrayRequest);


//        locationMap.clear();

    }
    //?????? ?????? ????????? ????????????
    private void updateCurrentLocation(Location location) {
        //?????? ?????? ??????
        String currentLat = String.valueOf(location.getLatitude());
        String currentLng = String.valueOf(location.getLongitude());
        String user_location = currentLat + ", " + currentLng;
        Log.d(TAG, "SetCurrentLocation : " + user_location);

        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = "http://3.38.51.117:8000/update/location/user/";

        HashMap<String, String> location_json = new HashMap<>();
        location_json.put("location", user_location);
        JSONObject parameter = new JSONObject(location_json);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String token = sharedPreferences.getString("access_token", null);
        Log.d(TAG, "token : " + token);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH,
                url,
                parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "update current location : " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//
                        error.printStackTrace();
                        Log.d("[?????? ????????????]", "Location Update FAIL");
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


    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //??????????????? ?????? ????????? ??? ???????????? ??????
//        if(mapView != null)
//        {
//            mapView.onCreate(savedInstanceState);
//        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        if(context instanceof OnTimePickerSetListener) {
//            onTimePickerSetListener = (OnTimePickerSetListener) context;
//        }
//        else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnTimePickerSetListener");
//        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        onTimePickerSetListener = null;
    }

    public interface OnTimePickerSetListener {
        void onTimePickerSet(String contents);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        //?????? ?????? ?????? ?????????, ????????? ????????? ??????
        setDefaultLocation();


        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. ?????? ???????????? ????????? ?????????

            startLocationUpdates(); // 3. ?????? ???????????? ??????
            mMap.setMyLocationEnabled(false);


        }else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.

            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (shouldShowRequestPermissionRationale(REQUIRED_PERMISSIONS[0])) {

                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ??????
                Snackbar.make(mLayout, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.

                        requestPermissions(REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????. ?????? ????????? onRequestPermissionResult?????? ??????.
                requestPermissions(REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }

        }

        mMap.getUiSettings().setMyLocationButtonEnabled(false);


        // ?????? ?????? ??????(15: ?????? ??????~????????? ?????? 1500M)
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){

            @Override
            public boolean onMyLocationButtonClick() {

//                Log.d( TAG, "onMyLocationButtonClick : ????????? ?????? ????????? ?????? ?????????");
//                mMoveMapByAPI = true;
                return true;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {


                if (mMoveMapByUser && check_result){

                    Log.d(TAG, "onCameraMove : ????????? ?????? ????????? ?????? ????????????");
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;

            }
        });


        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {

            }
        });
    }


    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : ????????? ??????");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission()) {
                check_result = true;
                mMap.setMyLocationEnabled(true);
            }

        }

    }
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    //?????? ????????? ?????? ?????? ??????
    public void setLocation(ArrayList<LatLng>uLoc, ArrayList<String>uName){

        if(cMarker.size()!=0){
            cMarker.clear();
        }

        BitmapDrawable bitmapdraw = (BitmapDrawable)getContext().getDrawable(R.drawable.ic_marker);
        icon = bitmapdraw.getBitmap();
//        Bitmap smallMarker = Bitmap.createScaledBitmap(icon, 60, 80, false);

        MarkerOptions markerOptions = new MarkerOptions();

        for(int i = 0;i<uLoc.size();i++){
            markerOptions.position(uLoc.get(i))
                    .title(uName.get(i))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(icon));
            Marker m = mMap.addMarker(markerOptions);
            cMarker.add(m);

        }

//        for(Map.Entry<LatLng,String> entry : locationMap.entrySet()) {
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(entry.getKey())
//                    .title(entry.getValue())
//                    .draggable(true)
//                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
//
//            Marker m = mMap.addMarker(markerOptions);
//            cMarker.add(m);
//        }


    }

    //?????? ?????? ?????? ??????
    public void setCurrentLocation(Location location) {

        mMoveMapByUser = false;


        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        BitmapDrawable bitmapdraw = (BitmapDrawable)getContext().getDrawable(R.drawable.ic_mymarker);
        icon2 = bitmapdraw.getBitmap();
//        Bitmap b = Bitmap.createScaledBitmap(icon2, 25, 25, false);
//        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.ic_mymarker);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng)
                .title("ME")
                .icon(BitmapDescriptorFactory.fromBitmap(icon2))
                .draggable(true);

        currentMarker = mMap.addMarker(markerOptions);

        //?????? ?????? ?????? ?????? ????????????
        address = getCurrentAddress(currentLatLng);

        // ????????????
        if (circle500M == null) {
            circle500M = new CircleOptions().center(currentLatLng) // ??????
                    .radius(500)       // ????????? ?????? : M
                    .strokeWidth(0f)    // ????????? 0f : ?????????
                    .strokeWidth(0f)    // ????????? 0f : ?????????
                    .fillColor(Color.parseColor("#88A9A9A9")); // ?????????
            circle = mMap.addCircle(circle500M);

        } else {
            circle.remove(); // ????????????
            circle500M.center(currentLatLng);
            circle = mMap.addCircle(circle500M);
        }

//        onTimePickerSetListener.onTimePickerSet(address);

        if(mMoveMapByAPI) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mMap.moveCamera(cameraUpdate);
        }

    }

    public void setDefaultLocation() {


        //????????? ??????: Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "???????????? ????????? ??? ??????";
//        String markerSnippet = "?????? ???????????? GPS ?????? ?????? ???????????????";

        mMoveMapByUser = false;


        if (currentMarker != null) currentMarker.remove();

        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.ic_mymarker);
        Bitmap bitmap = bitmapdraw.getBitmap();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION)
                .title(markerTitle)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }

    //?????? ?????? ?????? ????????????
    public String getCurrentAddress(LatLng latlng) {

        //???????????? GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(getContext(), "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            return "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getContext(), "????????? GPS ??????", Toast.LENGTH_LONG).show();
            return "????????? GPS ??????";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getContext(), "?????? ?????????", Toast.LENGTH_LONG).show();
            return "?????? ?????????";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    //??????????????? ????????? ????????? ????????? ?????? ????????????
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED){
            if(hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){
                return true;
            }
            return false;
        }
        else{
            return false;
        }

    }
    /*
     * ActivityCompat.requestPermissions??? ????????? ????????? ????????? ????????? ???????????? ?????????
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????




            // ?????? ???????????? ??????????????? ??????

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                // ???????????? ??????????????? ?????? ??????????????? ???????????????.
                startLocationUpdates();
            }
            else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.

                if (shouldShowRequestPermissionRationale(REQUIRED_PERMISSIONS[0])
                        || shouldShowRequestPermissionRationale(REQUIRED_PERMISSIONS[1])) {


                    // ???????????? ????????? ????????? ???????????? ?????? ?????? ???????????? ????????? ???????????? ??? ?????? ??????
                    Snackbar.make(mLayout, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

//                            finish();
                        }
                    }).show();

                }else {


                    // "?????? ?????? ??????"??? ???????????? ???????????? ????????? ????????? ???????????? ??????(??? ??????)?????? ???????????? ???????????? ??? ?????? ??????
                    Snackbar.make(mLayout, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

//                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GPS_ENABLE_REQUEST_CODE){
            //???????????? GPS ?????? ???????????? ??????
            if (checkLocationServicesStatus()) {
                if (checkLocationServicesStatus()) {

                    Log.d(TAG, "onActivityResult : GPS ????????? ?????????");

                    needRequest = true;

                    return;
                }
            }
        }


    }

    // ??? ????????? ????????? give_token ?????? ???????????? ?????? ?????? ??? ???????????? ???
    Map<String, String> give_token(String token) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        return headers;
    }
}
