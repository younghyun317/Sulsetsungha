package com.example.sulsetsungha.community;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

    ImageButton btnBackCmn, btnCmtSend;
    TextView txtWriteTime, txtDetailContext, txtLikeCnt, txtCmtCnt;
    EditText edtInputComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);

        btnBackCmn = findViewById(R.id.btnBackCmn);
        btnCmtSend = findViewById(R.id.btnCmtSend);
        txtWriteTime = findViewById(R.id.txtWriteTime);
        txtDetailContext = findViewById(R.id.txtDetailContext);
        txtLikeCnt = findViewById(R.id.txtLikeCnt);
        txtCmtCnt = findViewById(R.id.txtCmtCnt);
        edtInputComment = findViewById(R.id.edtInputComment);

        getCommunityDetail(); //게시글 불러오기
        getComment(); //댓글 불러오기

        btnCmtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setComment(); //댓글 달기

                //TODO 액티비티 화면 재갱신 시키는 코드
                Intent intent = getIntent();
                finish(); //현재 액티비티 종료 실시
                overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                startActivity(intent); //현재 액티비티 재실행 실시
                overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
            }
        });

        btnBackCmn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    //글 상세보기 불러오기
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

    //댓글 불러오기
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

                            for (int i=0; i < response.length(); i++) {
                                id = response.getJSONObject(i).getString("id").toString();
                                context = response.getJSONObject(i).getString("context").toString();
                                date = formatTimeString(timeToMill(response.getJSONObject(i).get("date").toString()));
                                //date = response.getJSONObject(i).getString("date").toString();
                                like = response.getJSONObject(i).getString("like").toString();

                                comments.add(new Comment(id, context, date, like));
                                Log.d(TAG, "comments : " + comments);
                            }
                            commentListView = (ListView)findViewById(R.id.listView_Comment);
                            commentAdapter = new CommentAdapter(getApplicationContext(), comments);
                            commentListView.setAdapter(commentAdapter);
                            commentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //각 아이템을 분간 할 수 있는 position과 뷰
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

    //댓글 달기
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
                        Toast toast = Toast.makeText(getApplicationContext(), "댓글 달기 성공!", Toast.LENGTH_LONG);
                        toast.show();
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

        queue.add(jsonObjectRequest);
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

    Map<String, String> give_token(String token) {
        HashMap<String, String> headers = new HashMap<>();
        // Bearer + token 해야됨! 안그럼 인식 못함
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

        public String getPost_id() { return post_id; }

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