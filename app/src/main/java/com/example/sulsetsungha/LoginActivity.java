package com.example.sulsetsungha;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

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

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(LoginActivity.this,JoinActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

    }
}