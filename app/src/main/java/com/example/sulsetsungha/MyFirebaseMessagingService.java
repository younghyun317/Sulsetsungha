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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import com.example.sulsetsungha.LoginActivity;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token){
        Log.d("FCM log", "Refreshed token: "+token);
        sendRegistrationToServer(token);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        if(remoteMessage.getNotification() != null) {
//            Log.d("FCM log","알림 메시지: "+remoteMessage.getNotification().getBody());
//            String msgBody = remoteMessage.getNotification().getBody();
//            String msgTitle = remoteMessage.getNotification().getTitle();
//            Intent intent = new Intent(this, LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            String channelId = "Channel ID";
//            Uri defaultSoundUrl = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle(msgTitle)
//                    .setContentText(msgBody)
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUrl)
//                    .setContentIntent(pendingIntent);
//
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                notificationBuilder = notificationBuilder.setContentTitle(msgTitle)
//                        .setContentText(msgBody)
//                        .setSmallIcon(R.mipmap.ic_launcher);
//
//            }
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                String channelName = "channel Name";
//                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//                channel.setSound(defaultSoundUrl, null);
//                notificationManager.createNotificationChannel(channel);
//            }
//
//            notificationManager.notify(0, notificationBuilder.build());
//        }
        if(!remoteMessage.getData().isEmpty()){
            Log.d(MyFirebaseMessagingService.class.getSimpleName(),"메시지 수신 성공 "+remoteMessage.getData().get("title"));
            getToken();
            sendNotification(remoteMessage);
        }else {
            Log.d(MyFirebaseMessagingService.class.getSimpleName(),"메시지 수신 실패 ");
        }
    }

    public void sendNotification(RemoteMessage remoteMessage) {
        int uniId = (int) System.currentTimeMillis() / 7;

        String channelId = getString(R.string.default_notification_channel_id);
        String channelName = getString(R.string.default_notification_channel_id);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUrl = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // 상단바 알림의 모양 결정
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUrl)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // channel 구분할 필요가 없어 하나로 통일
//        String channelId = "my_channel";


//        CharSequence channelName = "my channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(defaultSoundUrl,null);
            notificationManager.createNotificationChannel(notificationChannel);

        }
        notificationManager.notify(uniId, notificationBuilder.build());
    }

    private void sendRegistrationToServer(String refreshedToken){
        //TODO: 서버로 token 보내기

    }
    public void getToken() {
        //현재 등록된 토큰 가져오기
        Task<String> token = FirebaseMessaging.getInstance().getToken();
        token.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.w("FCM getToken()", "FCM registration token failed", task.getException());
                    return;
                }
                //Get new FCM registration token
                String token = task.getResult();
                Log.d("FCM Token", task.getResult());

                //Log and toast
//              String msg = String.format(token, R.string.msg_token_fmt);
////            String msg = getString(R.string.msg_token_fmt, token);
//              Log.d("FCM getToken()", msg);
//              Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
