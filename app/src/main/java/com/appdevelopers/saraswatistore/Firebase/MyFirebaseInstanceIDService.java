package com.appdevelopers.saraswatistore.Firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.appdevelopers.saraswatistore.MainActivity;
import com.appdevelopers.saraswatistore.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Bitmap bitmap;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);
        String imageUri = remoteMessage.getData().get("image");

        //String imageUri = remoteMessage.getNotification().getIcon();

        bitmap = getBitmapfromUrl(imageUri);

        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),bitmap);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d(TAG,"Refreshed Token: "+s);
    }

    public void showNotification(String title, String message, Bitmap image){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"MyNotifications")
                .setContentTitle(title)
                .setLargeIcon(image)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                .setColor(Color.BLUE)
                .setContentText(message)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("MyNotifications","MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        //NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        //manager.notify(999,builder.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl){
        try{
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
