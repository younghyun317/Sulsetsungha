package com.example.sulsetsungha;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import com.example.sulsetsungha.LoginActivity;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final int PRIORITY_MAX = 2;

    public String title = "";
    public String contents = "";

    @Override
    public void onNewToken(String token){
        Log.d("FCM log", "Refreshed token: "+token);
        sendRegistrationToServer(token);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "FROM: "+remoteMessage.getFrom());

        //Check if meesage contains a data payload
        if(remoteMessage.getData().size()>0){
            Log.d(TAG,"메시지 수신 성공 "+remoteMessage.getData());
//            getToken();
            //sendNotification(remoteMessage);
            sendPushNotification(remoteMessage.getData().toString());
        }
        else {
            Log.d(MyFirebaseMessagingService.class.getSimpleName(), "메시지 수신 실패 ");
        }

        //Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {

            Log.d(TAG, "Message Notification Body: "+remoteMessage.getNotification().getBody());

        }

        //Notification 알림
//        sendPushNotification(remoteMessage.getData().toString());
//        if(remoteMessage.getData().get("message") == null){
//            sendPushNotification(remoteMessage.getData().toString());
//        }
//        //data 알림
//        else {
//            sendPushNotification(remoteMessage.getData().get("message"));
//        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private void sendPushNotification(String message){
        System.out.println("received message : "+message);

        //푸시알림 받을 때 백그라운드로 처리하고 싶은 내용
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "handler.postDelayed");
//            }
//        }, 2000);

        try{
            JSONObject jsonRootObject = new JSONObject(message);
            title = jsonRootObject.getString("title");
            contents = jsonRootObject.getString("contents");

        }catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, "JSONException Occured");
        }
        PendingIntent pendingIntent = null;

        //푸시알림 클릭시 실행될 액티비티
        Intent intent = new Intent(this, LoginActivity.class);
//        intent.setAction(Intent.ACTION);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        pendingIntent = PendingIntent.getActivity(this, 8888/*Request code*/, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //알림 builder 설정
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        notificationBuilder
                .setContentTitle(title)
                .setContentText(contents)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "powerenglish:TAG");

        //android Oreo nofication channel if needed
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("description");
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(8888, notificationBuilder.build());

    }

//    public void sendNotification(RemoteMessage remoteMessage) {
//        int uniId = (int) System.currentTimeMillis() / 7;
//
//        String channelId = getString(R.string.default_notification_channel_id);
//        String channelName = getString(R.string.default_notification_channel_id);
//
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Uri defaultSoundUrl = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        // 상단바 알림의 모양 결정
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
//                .setContentTitle(remoteMessage.getData().get("title"))
//                .setContentText(remoteMessage.getData().get("body"))
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setSound(defaultSoundUrl)
//                .setAutoCancel(true)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // channel 구분할 필요가 없어 하나로 통일
////        String channelId = "my_channel";
//
//
////        CharSequence channelName = "my channel";
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//
//            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//            notificationChannel.setSound(defaultSoundUrl,null);
//            notificationManager.createNotificationChannel(notificationChannel);
//
//        }
//        notificationManager.notify(uniId, notificationBuilder.build());
//    }

    private void sendRegistrationToServer(String refreshedToken){
        //TODO: 서버로 token 보내기

    }
//    public void getToken() {
//        //현재 등록된 토큰 가져오기
//        Task<String> token = FirebaseMessaging.getInstance().getToken();
//        token.addOnCompleteListener(new OnCompleteListener<String>() {
//            @Override
//            public void onComplete(@NonNull Task<String> task) {
//                if(!task.isSuccessful()){
//                    Log.w("FCM getToken()", "FCM registration token failed", task.getException());
//                    return;
//                }
//                //Get new FCM registration token
//                String token = task.getResult();
//                Log.d("FCM Token", task.getResult());
//
//                //Log and toast
////              String msg = String.format(token, R.string.msg_token_fmt);
//////            String msg = getString(R.string.msg_token_fmt, token);
////              Log.d("FCM getToken()", msg);
////              Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }


}
