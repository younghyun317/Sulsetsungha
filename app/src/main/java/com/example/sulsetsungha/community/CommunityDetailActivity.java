package com.example.sulsetsungha.community;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sulsetsungha.community.CommentAdapter;
import com.example.sulsetsungha.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommunityDetailActivity extends AppCompatActivity {
    String TAG = CommunityDetailActivity.class.getSimpleName();

    ArrayList<CommunityDetailActivity.Comment> comments;
    ListView commentListView;
    static CommentAdapter commentAdapter;

    ImageButton btnBackCmn, btnCmtSend, btnPostLike;
    TextView txtWriteTime, txtDetailContext, txtLikeCnt, txtCmtCnt;
    EditText edtInputComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);

        btnBackCmn = findViewById(R.id.btnBackCmn);
        btnCmtSend = findViewById(R.id.btnCmtSend);
        btnPostLike = findViewById(R.id.btnPostLike);
        txtWriteTime = findViewById(R.id.txtWriteTime);
        txtDetailContext = findViewById(R.id.txtDetailContext);
        txtLikeCnt = findViewById(R.id.txtLikeCnt);
        txtCmtCnt = findViewById(R.id.txtCmtCnt);
        edtInputComment = findViewById(R.id.edtInputComment);

        getCommunityDetail(); //????????? ????????????
        getComment(); //?????? ????????????

        btnCmtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setComment(); //?????? ??????

                //TODO ???????????? ?????? ????????? ????????? ??????
                Intent intent = getIntent();
                finish(); //?????? ???????????? ?????? ??????
                overridePendingTransition(0, 0); //????????? ??????????????? ?????????
                startActivity(intent); //?????? ???????????? ????????? ??????
                overridePendingTransition(0, 0); //????????? ??????????????? ?????????
            }
        });

        btnBackCmn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPostLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_LONG);
                toast.show();
                setPostLike();
            }
        });

    }

    //??? ???????????? ????????????
    private void getCommunityDetail() {
        Intent intent = getIntent();
        String context = intent.getStringExtra("Context");
        String date = intent.getStringExtra("Date");
        String like_cnt = intent.getStringExtra("Like");
        String cmt_cnt = intent.getStringExtra("Comment");


        txtDetailContext.setText(context);
        txtWriteTime.setText(date);
        txtLikeCnt.setText(like_cnt);
        txtCmtCnt.setText(cmt_cnt);
    }

    //??? ????????? ?????????
    private void setPostLike() {
        Intent intent = getIntent();
        String postid = intent.getStringExtra("ID");
        String username = intent.getStringExtra("Username");

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://3.38.51.117:8000/like/post/";

        HashMap<String, String> like_json = new HashMap<>();
        like_json.put("id", postid.toString());
        JSONObject parameter = new JSONObject(like_json);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString("access_token", null);

        Drawable unlike_drawable = getResources().getDrawable(R.drawable.button_post_like);
        Drawable like_drawable = getResources().getDrawable(R.drawable.button_post_like_color);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH,
                url,
                parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "response : " + response.toString());
                            if (response.getString("id").equals(postid) && response.getString("user").equals(username)) {
                                btnPostLike.setImageDrawable(unlike_drawable);
                                Toast toast = Toast.makeText(getApplicationContext(), "??? ????????? ??????", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                btnPostLike.setImageDrawable(like_drawable);
                                Toast toast = Toast.makeText(getApplicationContext(), "??? ?????????!", Toast.LENGTH_LONG);
                                toast.show();
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
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return give_token(token);
            }
        };

        queue.add(jsonObjectRequest);
    }

    //?????? ????????????
    private void getComment() {
        Intent intent = getIntent();
        String postid = intent.getStringExtra("ID");

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://3.38.51.117:8000/community_comment/" + "?id=" + postid;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "getComment response : " + response.toString());
                        try {
                            Log.d(TAG, "response : " + response.getJSONObject(0).getString("context").toString());

                            comments = new ArrayList<>();
                            String id, context, date, like;

                            for (int i = 0; i < response.length(); i++) {
                                id = response.getJSONObject(i).getString("id").toString();
                                context = response.getJSONObject(i).getString("context").toString();
                                date = formatTimeString(timeToMill(response.getJSONObject(i).get("date").toString()));
                                //date = response.getJSONObject(i).getString("date").toString();
                                like = response.getJSONObject(i).getString("like").toString();

                                comments.add(new Comment(id, context, date, like));
                                Log.d(TAG, "comments : " + comments);
                            }
                            commentListView = (ListView) findViewById(R.id.listView_Comment);
                            commentAdapter = new CommentAdapter(getApplicationContext(), comments);
                            commentListView.setAdapter(commentAdapter);
                            commentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //??? ???????????? ?????? ??? ??? ?????? position??? ???
                                    //Log.d(TAG, "communityListView : " + communityListView.getItemAtPosition(position));
                                    String selectedItem = (String) view.findViewById(R.id.txtContext).getTag().toString();
                                    Toast.makeText(getApplicationContext(), "Clicked: " + position + " " + selectedItem, Toast.LENGTH_SHORT).show();
                                }
                            });
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

    //?????? ??????
    private void setComment() {
        Intent intent = getIntent();
        String postid = intent.getStringExtra("ID");

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://3.38.51.117:8000/community_comment/";

        HashMap<String, String> comment_json = new HashMap<>();
        comment_json.put("post", postid.toString());
        comment_json.put("context", edtInputComment.getText().toString());
        JSONObject parameter = new JSONObject(comment_json);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString("access_token", null);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), "?????? ?????? ??????!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return give_token(token);
            }
        };

        queue.add(jsonObjectRequest);
    }

    public static String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = null;
        if (diffTime < CommunityFragment.TIME_MAXIMUM.SEC) {
            msg = "?????? ???";
        } else if ((diffTime /= CommunityFragment.TIME_MAXIMUM.SEC) < CommunityFragment.TIME_MAXIMUM.MIN) {
            msg = diffTime + "??? ???";
        } else if ((diffTime /= CommunityFragment.TIME_MAXIMUM.MIN) < CommunityFragment.TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "?????? ???";
        } else if ((diffTime /= CommunityFragment.TIME_MAXIMUM.HOUR) < CommunityFragment.TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "??? ???";
        } else if ((diffTime /= CommunityFragment.TIME_MAXIMUM.DAY) < CommunityFragment.TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "??? ???";
        } else {
            msg = (diffTime) + "??? ???";
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

    Map<String, String> give_token(String token) {
        HashMap<String, String> headers = new HashMap<>();
        // Bearer + token ?????????! ????????? ?????? ??????
        headers.put("Authorization", "Bearer " + token);

        return headers;
    }

    class Comment {
        private String post_id;
        private String comment;
        private String time;
        private String like;

        public Comment(String post_id, String comment, String time, String like) {
            this.post_id = post_id;
            this.comment = comment;
            this.time = time;
            this.like = like;
        }

        public String getPost_id() {
            return post_id;
        }

        public String getComment() {
            return comment;
        }

        public String getTime() {
            return time;
        }

        public String getLike() {
            return like;
        }
    } //class Comment


}