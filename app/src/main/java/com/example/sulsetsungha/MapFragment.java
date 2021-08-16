package com.example.sulsetsungha;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.ImageButton;
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
import com.example.sulsetsungha.Fragment.HomeFragment;
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

import static android.content.Context.LOCATION_SERVICE;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapFragment extends Fragment implements OnMapReadyCallback , ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "[위치 확인]";
    private static final int GPS_ENABLE_REQUEST_CODE=2001;
    private static final int UPDATE_INTERVAL_MS=1000;
    private static final int FASTEST_UPDATE_INTERVAL_MS=500;

    //    final static int earth = 6371000; //지구 반지름(M)
    final static float dis = 500;

    private GoogleMap mMap;
    private Marker currentMarker = null;
    List<Marker> cMarker = new ArrayList<>();

    //onRequestPermissionResult에서 수신된 결과 중 ActivityCompat.requestPermissions 사용한 퍼미션 요청 구별
    private static final int PERMISSIONS_REQUEST_CODE=100;
    boolean needRequest=false;


    //Permission 정의
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소

    Location mCurrentLocation;
    LatLng currentPosition;
    LatLng currentPosition2;

    Circle circle;
    CircleOptions circle500M;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    HashMap<LatLng, String> locationMap;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.

    //LocationFragment에 데이터 전달
    Bundle bundle_location;

    boolean check_result = true;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;

    String address;

    Button btn_go2List;
    Button btn_request;
    ImageButton btn_gps;

    private OnTimePickerSetListener onTimePickerSetListener;

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
                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                mMoveMapByAPI = true;
                setCurrentLocation(location);
            }
        });

        btn_request = v.findViewById(R.id.btn_request);
        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 요청하기 버튼 클릭 이벤트
                Intent intent = new Intent(getActivity(),PushActivity.class);
                intent.putExtra("내용", "생리대 대여 알림");
                startActivity(intent);
//                onTimePickerSetListener.onTimePickerSet("생리대 대여 요청이 도착했어요!");
            }
        });

        btn_go2List = v.findViewById(R.id.btn_go2list);
        btn_go2List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationFragment fr = new LocationFragment();
                fr.setArguments(bundle_location);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.layout_fr, fr)
                        .commit();
            }
        });

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        //실시간 위치 받아오기
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fr_map);
        mapFragment.getMapAsync(this);

        return v;
    }

    //실시간 받아온 위치정보 처리
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            //TODO: 본인 현재 위치에 대한 정보
            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {

                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);
//                List<Float> distances = new ArrayList<>();
//                List<LatLng> users = new ArrayList<>();

                //서버에서 500m내 사용자 가져오기
                getNearUser();


//                //주변 사용자 위치
//                double user_lat2 = location.getLatitude()+0.0018;
//                double user_lon2 = location.getLongitude()+0.0009;
//                distances.add(getDistance(location, user_lat2, user_lon2));
//                users.add(new LatLng(user_lat2, user_lon2));
//
//                double user_lat3 = location.getLatitude()-0.0020;
//                double user_lon3 = location.getLongitude()-0.0007;
//                distances.add(getDistance(location, user_lat3, user_lon3));
//                users.add(new LatLng(user_lat3, user_lon3));
//
//                double user_lat4 = location.getLatitude()+0.0004;
//                double user_lon4 = location.getLongitude()-0.0015;
//                distances.add(getDistance(location, user_lat4, user_lon4));
//                users.add(new LatLng(user_lat4, user_lon4));
//
//                locationMap = new HashMap<>(); //key: 좌표, value: 좌표 해당 주소
//                ArrayList<Integer> possible = new ArrayList<>();
//
//
//                //본인 외 사용자 현재 위치 정보: 500M 내 위도&경도 범위에 있는 위치만 마커 생성
//                for(int i = 0;i<distances.size();i++){
//                    if(distances.get(i) <= dis){
//                        currentPosition2 = users.get(i);
//                        locationMap.put(currentPosition2, "빌려줄 수 있어요");
//                        int dt = Math.round(distances.get(i));
//                        possible.add(dt);
//                    }
//                }
//                Collections.sort(possible, toAsc);


//                //LocationFragment2에 데이터 전달하기
//                bundle_location = new Bundle();
//                bundle_location.putIntegerArrayList("possible_distances", possible);
                /**
                 * 마지막에 꼭 삭제해야할 코드!! Log 확인 위한 코드임!!!
                 */
                //본인 위치
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude()) + " 경도:" + String.valueOf(location.getLongitude());
                Log.d(TAG, "onLocationResult1 ==> " + markerSnippet);

                //본인 현재 위치
//                String currentLat = String.valueOf(location.getLatitude());
//                String currentLng = String.valueOf(location.getLongitude());
//                String user_location = currentLat + ","+currentLng;

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());


                //TODO: 현재 위치 서버에 올리기
                SetFirstCurrentLocation(); //최초 접속시
                SetCurrentLocation(); //업데이트


                //본인 현재 위치에 마커 생성하고 이동
                setCurrentLocation(location);

                //본인 외 위치 마커 생성하고 이동
//                setLocation(locationMap);
                Log.d("locationMap:", "본인 위치 외의 위치들 ==> "+locationMap);
//                locationMap.clear();

                mCurrentLocation = location;
            }

        }

    };

    //서버에서 500m내 사용자 가져오기
    public void getNearUser() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = sharedPreferences.getString("access_token", null);

        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url = "http://3.38.51.117:8000/location/user";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //TODO: 서버에서 받아온 데이터로 할 동작들
                        try {
                            Log.d("뭘 가져오나", "response : " + response.toString());
                            String loc = response.getJSONObject(0).getString("location").toString();
                            String loc_1[] = loc.split(",");
//                                     Log.d(TAG, "가져온 목록: "+loc_1);


//                                    txtMyId.setText(response.getJSONObject(0).getJSONObject("user").getString("username").toString());
//                                    txtMyPoint.setText(response.getJSONObject(0).getString("point"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getContext(), "server update error", Toast.LENGTH_LONG);
                        toast.show();

                        error.printStackTrace();
                        Log.d(MapFragment.class.getSimpleName(), "Location Update FAIL");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return give_token(token);
            }
        };

        queue.add(jsonArrayRequest);
    }

    //최초 접속 시, 현재 위치 서버에 올리기
    private void SetFirstCurrentLocation() {
        //본인 현재 위치
        String currentLat = String.valueOf(location.getLatitude());
        String currentLng = String.valueOf(location.getLongitude());
        String user_location = currentLat + ", " + currentLng;
        Log.d(TAG, "user_location : " + user_location);

        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = "http://3.38.51.117:8000/user/location/";

        HashMap<String, String> location_json = new HashMap<>();
        location_json.put("location", user_location.toString());
        JSONObject parameter = new JSONObject(location_json);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String token = sharedPreferences.getString("access_token", null);
        Log.d(TAG, "token : " + token);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response : " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast toast = Toast.makeText(getContext(), "server update error", Toast.LENGTH_LONG);
//                        toast.show();
//
//                        error.printStackTrace();
//                        Log.d(MapFragment.class.getSimpleName(), "Location Update FAIL");
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

    //현재 위치 서버에 업데이트
    private void SetCurrentLocation() {
        //본인 현재 위치
        String currentLat = String.valueOf(location.getLatitude());
        String currentLng = String.valueOf(location.getLongitude());
        String user_location = currentLat + ", " + currentLng;
        Log.d(TAG, "user_location : " + user_location);

        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = "http://3.38.51.117:8000/update/location/user/";

        HashMap<String, String> location_json = new HashMap<>();
        location_json.put("location", user_location.toString());
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
                        Log.d(TAG, "update response : " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast toast = Toast.makeText(getContext(), "server update error", Toast.LENGTH_LONG);
//                        toast.show();
//
//                        error.printStackTrace();
//                        Log.d(MapFragment.class.getSimpleName(), "Location Update FAIL");
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

        //액티비티가 처음 생성될 때 실행되는 함수
//        if(mapView != null)
//        {
//            mapView.onCreate(savedInstanceState);
//        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnTimePickerSetListener) {
            onTimePickerSetListener = (OnTimePickerSetListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimePickerSetListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        onTimePickerSetListener = null;
    }

    public interface OnTimePickerSetListener {
        void onTimePickerSet(String contents);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        //현재 위치 찾지 못하면, 디폴트 위치로 이동
        setDefaultLocation();


        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면

            startLocationUpdates(); // 3. 위치 업데이트 시작


        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (shouldShowRequestPermissionRationale(REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.

                        requestPermissions(REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다. 요청 결과는 onRequestPermissionResult에서 수신.
                requestPermissions(REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }

        }

        mMap.getUiSettings().setMyLocationButtonEnabled(false);


        // 지도 확대 정도(15: 화면 왼쪽~오른쪽 거리 1500M)
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){

            @Override
            public boolean onMyLocationButtonClick() {

                Log.d( TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
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


                if (mMoveMapByUser == true && check_result){

                    Log.d(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
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

                Log.d(TAG, "startLocationUpdates : 퍼미션 없음");
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

    //거리 구하기
    public float getDistance(Location location , double lat , double lng){
        float distance;

        Location locationB = new Location("point B");
        locationB.setLatitude(lat);
        locationB.setLongitude(lng);

        distance = location.distanceTo(locationB);

        return distance;
    }

    Comparator<Integer> toAsc = new Comparator<Integer>() {
        @Override
        public int compare(Integer a, Integer b) {
            return a.compareTo(b) ;
        }
    };

    //주변 사용자 위치 마커 설정
    public void setLocation(HashMap<LatLng, String> locationMap){

        if(cMarker.size()!=0){
            for(int m=0;m<cMarker.size();m++){
                cMarker.get(m).remove();
            }
            cMarker.clear();
        }

//        for(Map.Entry<LatLng,String> entry : locationMap.entrySet()) {
//
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(entry.getKey())
//                    .title(entry.getValue())
//                    .draggable(true)
//                    .icon(BitmapDescriptorFactory.defaultMarker(27.5f));
//
//            Marker m = mMap.addMarker(markerOptions);
//            cMarker.add(m);
//        }
    }

    //본인 위치 마커 설정
    public void setCurrentLocation(Location location) {

        mMoveMapByUser = false;


//        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(currentLatLng)
//                .title(markerTitle)
//                .draggable(false);
//
//        currentMarker = mMap.addMarker(markerOptions);

        //본인 현재 위치 주소 보여주기
        address = getCurrentAddress(currentLatLng);

        onTimePickerSetListener.onTimePickerSet(address);


        // 반경추가
        if (circle500M == null) {
            circle500M = new CircleOptions().center(currentLatLng) // 원점
                    .radius(500)       // 반지름 단위 : M
                    .strokeWidth(0f)    // 선너비 0f : 선없음
                    .strokeWidth(0f)    // 선너비 0f : 선없음
                    .fillColor(Color.parseColor("#88A9A9A9")); // 배경색
            circle = mMap.addCircle(circle500M);

        } else {
            circle.remove(); // 반경삭제
            circle500M.center(currentLatLng);
            circle = mMap.addCircle(circle500M);
        }

        if(mMoveMapByAPI) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mMap.moveCamera(cameraUpdate);
        }

    }

    public void setDefaultLocation() {


        //디폴트 위치: Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
//        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        mMoveMapByUser = false;


        if (currentMarker != null) currentMarker.remove();

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(DEFAULT_LOCATION)
//                .title(markerTitle)
//                .draggable(true)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }

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

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;

    }
    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면




            // 모든 퍼미션을 허용했는지 체크

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (shouldShowRequestPermissionRationale(REQUIRED_PERMISSIONS[0])
                        || shouldShowRequestPermissionRationale(REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱 사용 가능
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

//                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱 사용 가능
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

//                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");

                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }

    // 이 함수는 나중에 give_token 구현 필요라는 말이 있을 때 사용하면 됨
    Map<String, String> give_token(String token) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        return headers;
    }
}
