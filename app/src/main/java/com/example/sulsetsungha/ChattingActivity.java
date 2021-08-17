package com.example.sulsetsungha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class ChattingActivity extends AppCompatActivity {
    String TAG = ChattingActivity.class.getSimpleName();

    TextView room_name, messages;
    String roomName;
    WebSocketClient webSocketClient;
    EditText chat;
    ImageButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        connectWebSocket();

        chat = findViewById(R.id.chat_message);

        send = findViewById(R.id.send_chat);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> message = new HashMap<>();
                message.put("message", chat.getText().toString());
                message.put("room", roomName);

                JSONObject message_json = new JSONObject(message);

                webSocketClient.send(String.valueOf(message_json));
            }
        });
    }

    void connectWebSocket() {
        room_name = findViewById(R.id.room_name);
        roomName = "room_name";

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString("access_token", null);

        Log.d(TAG, "websocket token" + token);

        URI uri = null;
        try {
            uri =  new URI("ws://"
                    + "3.38.51.117:8000"
                    + "/ws/chat/"
                    + roomName
                    + "/?token="
                    + token
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "web socket open");

            }

            @Override
            public void onMessage(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messages = findViewById(R.id.message_textView);
                        messages.setText(messages.getText().toString() + "\n" + message);
                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "socket closed unexpectedly");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "socket error");
            }
        };

        webSocketClient.connect();
    }
}