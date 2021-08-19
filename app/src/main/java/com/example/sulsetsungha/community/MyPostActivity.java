package com.example.sulsetsungha.community;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sulsetsungha.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyPostActivity extends AppCompatActivity {
    String TAG = MyPostActivity.class.getSimpleName();

    ArrayList<CommunityFragment.Community> communities;
    ListView communityListView;
    static MyPostAdapter myPostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        getMyPost();
    }

    //내가 쓴 게시글 가져오기
    private void getMyPost() {
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://3.38.51.117:8000/community_post/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d(TAG, "response : " + response.getJSONObject(0).getString("context").toString());

                            communities = new ArrayList<>();
                            String id, context, date, like, comment, username;

                            for (int i=0; i < response.length(); i++) {
                                Intent intent = getIntent();
                                String post_username = intent.getStringExtra("ID");

                                if (post_username.equals(response.getJSONObject(i).getString("user").toString())) {

                                    id = response.getJSONObject(i).getString("id").toString();
                                    context = response.getJSONObject(i).getString("context").toString();
                                    date = formatTimeString(timeToMill(response.getJSONObject(i).get("date").toString()));
                                    //date = response.getJSONObject(i).getString("date").toString();
                                    like = response.getJSONObject(i).getString("like").toString();
                                    comment = response.getJSONObject(i).getString("like").toString();
                                    username = response.getJSONObject(i).getString("user").toString();

                                    communities.add(new CommunityFragment.Community(id, context, String.valueOf(date), like, comment, username));
                                    Log.d(TAG, "communities : " + communities);
                                }
                            }
                            communityListView = (ListView)findViewById(R.id.listView_MyPost);
                            myPostAdapter = new MyPostAdapter(getApplicationContext(), communities);

                            communityListView.setAdapter(myPostAdapter);
                            myPostAdapter.notifyDataSetChanged();//갱신하기

                            if (communityListView.getCount() > 0) {
                                communityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        //각 아이템을 분간 할 수 있는 position과 뷰
                                        Log.d(TAG, "communityListView : " + communityListView.getItemAtPosition(position));
                                        communityListView.getItemAtPosition(position);
                                        String selectedItem = (String) view.findViewById(R.id.txtContext).getTag().toString();
                                        Toast.makeText(getApplicationContext(), "Clicked: " + position + " " + selectedItem, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

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
    }

    public static class TIME_MAXIMUM{
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public static String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = null;
        if (diffTime < CommunityFragment.TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= CommunityFragment.TIME_MAXIMUM.SEC) < CommunityFragment.TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= CommunityFragment.TIME_MAXIMUM.MIN) < CommunityFragment.TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= CommunityFragment.TIME_MAXIMUM.HOUR) < CommunityFragment.TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= CommunityFragment.TIME_MAXIMUM.DAY) < CommunityFragment.TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }

        return msg;
    }

    public long timeToMill(String time) {
        String parseString = time.replace("T", " ").replace("+09:00", "");
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = fm.parse(parseString);
        } catch (ParseException e) {
            Log.d(TAG, "", e);
        }

        return date.getTime();
    }
}