package com.example.sulsetsungha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends AppCompatActivity {

    String TAG = JoinActivity.class.getSimpleName();

    EditText edtJoinId, edtJoinPw, edtJoinPwChk;
    ImageButton btnSignUp, btnLoginBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        edtJoinId = findViewById(R.id.edtJoinId);
        edtJoinPw = findViewById(R.id.edtJoinPw);
        edtJoinPwChk = findViewById(R.id.edtJoinPwChk);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLoginBack = findViewById(R.id.btnLoginBack);

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        final SharedPreferences.Editor editor = sharedPreferences.edit();

        btnLoginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(JoinActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://3.38.51.117:8000/users/";
        HashMap<String, String> join_json = new HashMap<>();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join_json.put("username", edtJoinId.getText().toString());
                join_json.put("password", edtJoinPw.getText().toString());
                join_json.put("password2", edtJoinPwChk.getText().toString());

                JSONObject parameter = new JSONObject(join_json);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        url,
                        parameter,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Boolean result = response.optBoolean("username");
                                //Log.d("check", "result"+ result);

//                                if (result) {
//                                    if (edtJoinPw.getText().toString().equals(edtJoinPwChk.getText().toString())) {
////                                        editor.putString("join_id", edtJoinId.getText().toString());
////                                        editor.putString("join_pw", edtJoinPw.getText().toString());
////                                        editor.apply();
//
//                                        Intent i = new Intent(JoinActivity.this, LoginActivity.class);
//                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        startActivity(i);
//
//                                        Toast toast = Toast.makeText(getApplicationContext(), "회원가입이 완료 되었습니다.\n 환영합니다 " + edtJoinId + "님!", Toast.LENGTH_LONG);
//                                        toast.show();
//                                    } else {
//                                        Toast toast = Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다. 다시 한번 확인하세요.", Toast.LENGTH_LONG);
//                                        toast.show();
//                                        edtJoinPwChk.setText("");
//                                    }
//                                } else {
//                                    Toast toast = Toast.makeText(getApplicationContext(), "ID가 중복입니다. 다른 ID를 입력하세요.", Toast.LENGTH_LONG);
//                                    toast.show();
//                                }
                                if (edtJoinPw.getText().toString().equals(edtJoinPwChk.getText().toString())) {

                                    Intent i = new Intent(JoinActivity.this, LoginActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);

                                    Toast toast = Toast.makeText(getApplicationContext(), "회원가입이 완료 되었습니다.\n 환영합니다 " + edtJoinId.getText().toString() + "님!", Toast.LENGTH_LONG);
                                    toast.show();
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다. 다시 한번 확인하세요.", Toast.LENGTH_LONG);
                                    toast.show();
                                    edtJoinPwChk.setText("");
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.\n잠시 후 다시 시도해주세요.", Toast.LENGTH_LONG);
                                toast.show();

                                error.printStackTrace();
                                Log.d(TAG, "Join FAIL");
                            }
                        }
                );

                queue.add(jsonObjectRequest);
            }
        });

    }
}