package com.example.sulsetsungha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PushAlarmActivity extends AppCompatActivity {

    private TextView content;

    EditText edt_point;
    Button btn_cancel;
    Button btn_donate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushalarm);

        edt_point = findViewById(R.id.edt_point);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_donate = findViewById(R.id.btn_donate);

    }
}