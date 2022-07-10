package com.example.busreservationsystem.Notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.busreservationsystem.Helper.Helper;
import com.example.busreservationsystem.MainActivity;
import com.example.busreservationsystem.R;

import java.util.Calendar;

public class Notification {
    static String  CHANNEL_ID = "1";
    public static void setAlert(Context context,Calendar calendar, int notificationId,
                                String notificationTitle, String notificationContent){

        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("notificationTitle", notificationTitle);
        intent.putExtra("notificationContent", notificationContent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 3000
                , pendingIntent);

    }

    public static void notify(Context context, int notificationId
            , String title, String content){
        createNotificationChannel(context);
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context
                , 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_drawable)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());


        //rings when the alarm goes off
        /*
        // we will use vibrator first
        Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        // setting default ringtone
        MediaPlayer mp = MediaPlayer. create (context, alarmUri);

        Toast.makeText(context, alarmUri.toString(), Toast.LENGTH_SHORT).show();
        // play ringtone
        mp.start();

         */
    }

    private static void createNotificationChannel(Context context) {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channelName";

            String description = "channelDescription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String notificationContent = intent.getStringExtra("notificationContent");
            int notificationId = intent.getIntExtra("notificationId", 0);
            String notificationTitle = intent.getStringExtra("notificationTitle");
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            // setting default ringtone
            MediaPlayer mp = MediaPlayer. create (context, alarmUri);
            mp.start();
            Notification.notify(context, notificationId, notificationTitle, notificationContent);
            Toast.makeText(context, "you have a trip after 30 minutes", Toast.LENGTH_LONG).show();
        }
    }
}
