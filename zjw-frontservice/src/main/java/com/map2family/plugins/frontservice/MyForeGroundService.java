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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.taobao.weex.WXSDKManager.getInstance;


public class MyForeGroundService extends Service {

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String ACTION_PAUSE = "ACTION_PAUSE";

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private PowerManager.WakeLock wl;
    public  String instance_id ;
    Handler handler;
    Runnable runnable;
    private int delaysec = 2000;


    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    NotificationChannel notificationChannel;
    String NOTIFICATION_CHANNEL_ID = "22";
    Thread thread;


    public MyForeGroundService() {
    }
    private AtomicBoolean working = new AtomicBoolean(true);


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        Log.d(TAG_FOREGROUND_SERVICE, getPackageName());
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().");
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                getClass().getCanonicalName());
        if (! wl.isHeld()) {
            wl.acquire();
        }
        boolean isIgnoring = isIgnoringBatteryOptimizations();

        if (!isIgnoring){
            requestIgnoreBatteryOptimizations();
            Log.d(TAG_FOREGROUND_SERVICE, "not in ");
        }
        else{
            Log.d(TAG_FOREGROUND_SERVICE, "is in ");
        }

        if (handler == null){
            handler = new Handler();
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
            if ( instance_id == null) {
                instance_id = intent.getStringExtra("instant_id");
            }

            delaysec = intent.getIntExtra("delaysec", 2000);

            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    startMyForegroundService(big_title, title, content, intent );
                    //Toast.makeText(getApplicationContext(), "Foreground service is started. 121", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    //Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
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


    private static Class getMainActivityClass(Context context) {
        String packageName = context.getPackageName();
        Intent launchIntent =
                context.getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
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
    private void startMyForegroundService( String big_title, String title, String content, Intent  intent)
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

        intent.setPackage(getPackageName());
        Intent resultIntent = new Intent(this, getMainActivityClass(this));


        // Create notification default intent.
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // 唤起app
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
        // Create notification builder.
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID).
                setSmallIcon(R.drawable.ic_home_black_24dp).setContentTitle(big_title);

        // Make notification show big text.
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(content);
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());
        try
        {
            Drawable icon = this.getPackageManager().getApplicationIcon(getPackageName());
            Bitmap bitmap = ((BitmapDrawable)icon).getBitmap();
            builder.setSmallIcon(Icon.createWithBitmap(bitmap) );
            builder.setLargeIcon(bitmap);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }

        builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);

        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(Notification.FLAG_ONGOING_EVENT, notification);
        if (delaysec > 0) {
            runnable = new Runnable() {
                int count = 0;

                @Override
                public void run() {
                    //这里写入要作的事情
                    count = count + 1;
                    actions(Integer.toString(count));
                    handler.postDelayed(runnable, delaysec);
                }
            };
            handler.postDelayed(runnable, delaysec);
        }

    }

    private  void actions(  String msg){
        Map<String,Object> params=new HashMap<>();
        params.put("key",msg);
        getInstance().getSDKInstance(instance_id).fireGlobalEventCallback("position", params);
    }


    private void stopForegroundService()
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");
        if (runnable != null && delaysec > 0)
            handler.removeCallbacks(runnable);
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();

    }
    @Override
    public void onDestroy() {
        System.out.println("onDestroy");
       if (wl != null) {
           if (wl.isHeld()) {
               wl.release();
           }
       }
        super.onDestroy();
    }

}