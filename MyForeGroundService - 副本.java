package com.map2family.plugins.frontservice;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import static com.map2family.plugins.frontservice.Tools.base64ToBitmap;

public class MyForeGroundService extends Service {

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String ACTION_PAUSE = "ACTION_PAUSE";

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String CHANNEL_ID = "ForegroundServiceChannel";


    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    NotificationChannel notificationChannel;
    String NOTIFICATION_CHANNEL_ID = "22";

    public MyForeGroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().");

        Bitmap IconLg = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);

        mNotifyManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this, null);
        mBuilder.setContentTitle("My App 1111")
                .setContentText("Always running 1111...")
                .setTicker("Always running 1111...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(IconLg)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[] {10000})
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{10000});
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotifyManager.createNotificationChannel(notificationChannel);

            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID + "log");
            startForeground(4, mBuilder.build());
        }
        else
        {
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotifyManager.notify(3, mBuilder.build());
        }
    }

    private void startMyOwnForeground(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String NOTIFICATION_CHANNEL_ID = CHANNEL_ID;
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon( R.mipmap.ic_launcher )
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);

        }
    }
    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.d(TAG_FOREGROUND_SERVICE, "onTaskRemoved().");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null)
        {
            String title = intent.getStringExtra("title");
            String big_title = intent.getStringExtra("big_title");
            String content = intent.getStringExtra("content");
            String action = intent.getAction();

            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    //startMyForegroundService(big_title, title, content, intent );
                    Toast.makeText(getApplicationContext(), "Foreground service is started. 111", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    //Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PLAY:
                    Toast.makeText(getApplicationContext(), "You click Play button.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PAUSE:
                    Toast.makeText(getApplicationContext(), "You click Pause button.", Toast.LENGTH_LONG).show();
                    break;
            }
        }

        //return super.onStartCommand(intent, flags, startId);
        //如果系统在onStartCommand()返回后杀死了服务，
        // 则将重建服务并用上一个已送过的intent调用onStartCommand()。
        //任何未发送完的intent也都会依次送入。
        // 这适用于那些需要立即恢复工作的活跃服务，比如下载文件。

        return START_REDELIVER_INTENT;
        //return START_STICKY;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            //NotificationManager manager = getSystemService(NotificationManager.class);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    @NonNull
    @TargetApi(26)
    private synchronized String createChannel(String big_title) {

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = big_title + "zjw1";
        String channelid = "3 snap map channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        //NotificationChannel mChannel = new NotificationChannel("snap map channel", name, importance);
        NotificationChannel mChannel = new NotificationChannel(channelid, name, importance);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
            Toast.makeText(getApplicationContext(), "not stopSelf.", Toast.LENGTH_LONG).show();
        } else {
            stopSelf();
            Toast.makeText(getApplicationContext(), "stopSelf.", Toast.LENGTH_LONG).show();
        }
        return  channelid;
    }
    /* Used to build and start foreground service. */
    @TargetApi(26)
    private void startMyForegroundService(String big_title, String title, String content, Intent  intent)
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

        // Create notification default intent.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        // Create notification builder.
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_home_black_24dp).setContentTitle(big_title);

        // Make notification show big text.
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(content);
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_dashboard_black_24dp);
        //// Make the notification max priority.
        //builder.setLargeIcon(largeIconBitmap);
        builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);

/*
            // Add Play button intent in notification.
            Intent playIntent = new Intent(this, MyForeGroundService_Test.class);
            playIntent.setAction(ACTION_PLAY);
            PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
            Notification.Action playAction = new Notification.Action(android.R.drawable.ic_media_play, "暂停", pendingPlayIntent);
            builder.addAction(playAction);

            // Add Pause button intent in notification.
            Intent pauseIntent = new Intent(this, MyForeGroundService_Test.class);
            pauseIntent.setAction(ACTION_PAUSE);
            PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
            Notification.Action prevAction = new Notification.Action(android.R.drawable.ic_media_pause, "停止", pendingPrevIntent);
            builder.addAction(prevAction);

*/
        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(118, notification);

    }

    private void stopForegroundService()
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();

    }
    @Override
    public void onDestroy() {
        System.out.println("onDestroy");
        super.onDestroy();
    }

}