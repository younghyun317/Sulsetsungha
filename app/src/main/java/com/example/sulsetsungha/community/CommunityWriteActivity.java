package com.example.sulsetsungha.community;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sulsetsungha.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CommunityWriteActivity extends AppCompatActivity {
    String TAG = CommunityWriteActivity.class.getSimpleName();

    EditText edtCmnContext;
    Button btnWriteCancel, btnWritePost, btnAddPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_write);

        edtCmnContext = findViewById(R.id.edtCmnContext);
        btnWritePost = findViewById(R.id.btnWritePost);
        btnWriteCancel = findViewById(R.id.btnWriteCancel);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);

        btnWriteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnWritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click");

                PostContext();
            }
        });
    }

    private void PostContext() {
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://3.38.51.117:8000/community_post/";

        HashMap<String, String> post_json = new HashMap<>();
        post_json.put("context", edtCmnContext.getText().toString());
        post_json.put("photo", null);
        JSONObject parameter = new JSONObject(post_json);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString("access_token", null);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), "게시글 올리기 성공!", Toast.LENGTH_LONG);
                        toast.show();

                        finish();
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

    Map<String, String> give_token(String token) {
        HashMap<String, String> headers = new HashMap<>();
        // Bearer + token 해야됨! 안그럼 인식 못함
        headers.put("Authorization", "Bearer " + token);

        return headers;
    }
}