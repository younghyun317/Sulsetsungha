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
import android.widget.TextView;
import android.widget.Toast;

import com.example.sulsetsungha.Fragment.HomeFragment;

public class PushActivity extends AppCompatActivity{

    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        content = findViewById(R.id.txt_content);

//        //브로드캐스트로 온 인텐트는 SMS 메세지임
//        Intent passedIntent = getIntent();  //인텐트 수신 함수
//        processIntent(passedIntent);

        Intent it = getIntent();
        processIntent(it);

        // 이 부분이 바로 화면을 깨우는 부분
        // 화면이 잠겨있을 때 보여주기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                // 키잠금 해제하기
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                // 화면 켜기
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //투명배경


        Button btn_accept = findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 수락 버튼 클릭

//                Intent intent = new Intent(getApplicationContext(), SmslistActivity.class);
//                startActivity(intent);
                Toast.makeText(getApplicationContext(), "수락 버튼 눌림", Toast.LENGTH_SHORT).show();
                finish();
            }
            public boolean onTouchEvent(MotionEvent event) {
                //바깥레이어 클릭시 안닫히게
                return event.getAction() != MotionEvent.ACTION_OUTSIDE;

            }
        });

        Button cb = findViewById(R.id.btn_deny);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 거절 버튼 클릭

                Toast.makeText(getApplicationContext(), "거절 버튼 눌림", Toast.LENGTH_SHORT).show();
                // push 창 종료
                finish();
            }
        });

    }//onCreate



    /** SmsActivity가 이미 켜져있는 상태에서도 SMS 수신하도록 **/
    @Override
    protected void onNewIntent(Intent intent) {
//        processIntent(intent);
        super.onNewIntent(intent);
    }  //onNewIntent()

    private void processIntent(Intent intent) {
        if (intent != null) {
            String contents = intent.getStringExtra("내용");
            content.setText(contents);
        }
    } //processIntent()
}