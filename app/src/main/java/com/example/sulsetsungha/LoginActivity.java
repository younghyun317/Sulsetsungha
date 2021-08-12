package com.example.sulsetsungha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    String TAG = LoginActivity.class.getSimpleName();

    EditText edtId, edtPw;
    Button btnJoin, btnLogin, btnFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtId = findViewById(R.id.edtId);
        edtPw = findViewById(R.id.edtPw);
        btnFind = findViewById(R.id.btnFind);
        btnLogin = findViewById(R.id.btnLogin);
        btnJoin = findViewById(R.id.btnJoin);

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

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        url,
                        parameter,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String userid = response.getString("id");
                                    JSONObject result = response.getJSONObject(userid);
                                    String userPw = result.getString("password");

                                    if (edtPw.getText().toString().equals(userPw)) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "환영합니다 " + edtId.getText().toString() + "님!", Toast.LENGTH_LONG);
                                        toast.show();

                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("user_id", edtId.getText().toString());
                                        editor.apply();

                                        Intent i= new Intent(LoginActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                    }
                                    else {
                                        Toast toast = Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인해주세요.", Toast.LENGTH_LONG);
                                        toast.show();

                                        edtPw.setText("");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(getApplicationContext(), "아이디를 다시 확인해주세요.", Toast.LENGTH_LONG);
                                toast.show();

                                edtId.setText("");

                                error.printStackTrace();
                                Log.d(TAG, "Login Error");
                            }
                        });

                queue.add(jsonObjectRequest);
            }
        });
    }
}