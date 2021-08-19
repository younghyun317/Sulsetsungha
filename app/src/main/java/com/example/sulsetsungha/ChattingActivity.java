package com.example.sulsetsungha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChattingActivity extends AppCompatActivity {
    String TAG = ChattingActivity.class.getSimpleName();

    TextView room_name, messages;
    String roomName;
    WebSocketClient webSocketClient;
    EditText chat;
    Button send;
    ImageButton btnBorrowCompleted;

    RecyclerView chat_message;
    ArrayList<ChatItem> chatItems = new ArrayList<>();
    ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        connectWebSocket();

        chat = findViewById(R.id.chat_message);

        chat_message = findViewById(R.id.chat_recycle);
        adapter = new ChatAdapter(chatItems);
        chat_message.setAdapter(adapter);


        send = findViewById(R.id.send_chat);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> message = new HashMap<>();
                message.put("message", chat.getText().toString());
                message.put("room", roomName);

                JSONObject message_json = new JSONObject(message);

                webSocketClient.send(String.valueOf(message_json));
                chat.setText("");
            }
        });

        btnBorrowCompleted = findViewById(R.id.btnBorrowCompleted);
        btnBorrowCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBorrowCompleted();
            }
        });

    }

    private void setBorrowCompleted() {
        Intent intent = getIntent();
        String user_name = intent.getStringExtra("username");

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://3.38.51.117:8000/borrow/";

        HashMap<String, String> borrow_json = new HashMap<>();
        borrow_json.put("borrower_username", user_name);
        JSONObject parameter = new JSONObject(borrow_json);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString("access_token", null);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                parameter,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), "대여가 완료되었습니다.", Toast.LENGTH_LONG);
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

    void connectWebSocket() {
        Intent intent = getIntent();
        room_name = findViewById(R.id.room_name);
        String user_name = intent.getStringExtra("username");


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String my_name = sharedPreferences.getString("my_name", null);

        if (my_name.compareTo(user_name) >= 0) {
            roomName = my_name + user_name;
        }
        else {
            roomName = user_name + my_name;
        }

        room_name.setText(user_name);
        get_message(roomName, my_name);

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
                        try {
                            JSONObject jsonObject = new JSONObject(message);

                            String name = jsonObject.getString("username");
                            String content = jsonObject.getString("message");
                            int view = 0;

                            if (name.equals(my_name)) {
                                view = 1;
                            }
                            chatItems.add(new ChatItem(content, name, view));
                            adapter.notifyDataSetChanged();
                            chat_message.smoothScrollToPosition(chatItems.size() - 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    void get_message(String roomName, String my_name) {
        String url = "http://3.38.51.117:8000/chat/?room=" + roomName;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);

                                    String name = object.getString("user");
                                    String content = object.getString("content");
                                    String date = object.getString("date");

                                    int view = 0;

                                    if (name.equals(my_name)) {
                                        view = 1;
                                    }
                                    chatItems.add(new ChatItem(content, name, view));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d(TAG, "chat fail");
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}