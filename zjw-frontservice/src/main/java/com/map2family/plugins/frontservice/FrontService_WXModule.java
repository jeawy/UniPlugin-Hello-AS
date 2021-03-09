package com.map2family.plugins.frontservice;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FrontService_WXModule extends WXSDKEngine.DestroyableModule {

    private static final int WRITE_COARSE_LOCATION_REQUEST_CODE = 12;
    public String TITLE = "title"; // 标题
    public String BIG_TITLE = "big_title"; // 收起来的时候，显示的标题
    public String CONTENT = "content"; // 内容
    public String RIGHT_BTN = "right_btn"; // 右边按钮
    public String LEFT_BTN = "left_btn"; // 左边按钮

    public FrontService_WXModule() throws ParseException {
    }


    @JSMethod(uiThread = false)
    public void isNotificationEnabled(JSONObject options, JSCallback jsCallback) throws ParseException {
        boolean isEnabled = false;
        if (mWXSDKInstance.getContext() instanceof Activity) {
            NotificationManagerCompat notification = NotificationManagerCompat.from(mWXSDKInstance.getContext());
            isEnabled = notification.areNotificationsEnabled();
        }

        JSONObject result = new JSONObject();
        result.put("isEnabled",  isEnabled);
        jsCallback.invoke(result);
    }

    @JSMethod(uiThread = false)
    public void invokeNotification(JSONObject options, JSCallback jsCallback) throws ParseException {
        boolean isEnabled = false;
        if (mWXSDKInstance.getContext() instanceof Activity) {
            NotificationManagerCompat notification = NotificationManagerCompat.from(mWXSDKInstance.getContext());
            isEnabled = notification.areNotificationsEnabled();
            if (!isEnabled){
                //未打开通知,oppo手机默认不提示通知权限
                AlertDialog alertDialog = new AlertDialog.Builder(mWXSDKInstance.getContext())
                        .setTitle("提示")
                        .setMessage("请在“通知”中打开通知权限")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Intent intent = new Intent();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                    intent.putExtra("android.provider.extra.APP_PACKAGE",  ((Activity) mWXSDKInstance.getContext()).getPackageName());
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
                                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                    intent.putExtra("app_package",  ((Activity) mWXSDKInstance.getContext()).getPackageName());
                                    intent.putExtra("app_uid",  ((Activity) mWXSDKInstance.getContext()).getApplication().getApplicationInfo().uid);
                                    ((Activity) mWXSDKInstance.getContext()).startActivity(intent);
                                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + ((Activity) mWXSDKInstance.getContext()).getPackageName()));
                                } else if (Build.VERSION.SDK_INT >= 15) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    intent.setData(Uri.fromParts("package",  ((Activity) mWXSDKInstance.getContext()).getPackageName(), null));
                                }
                                ((Activity) mWXSDKInstance.getContext()).startActivity(intent);
                            }
                        })
                        .create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        }
    }

    private void goHuaweiSetting()  {
        try {
            String cmd = "cmd appops set " + ((Activity) mWXSDKInstance.getContext()).getPackageName()+ " RUN_IN_BACKGROUND ignore";
            //cmd = "adb shell am make-uid-idle "  + ((Activity) mWXSDKInstance.getContext()).getPackageName();
            Process ps  = Runtime.getRuntime().exec(cmd);
            Log.d("startForegroundService", " startForegroundService()");
        } catch (IOException e) {
            e.printStackTrace();
        };
    }

    @JSMethod(uiThread = false)
    public void start(JSONObject options, JSCallback jsCallback) throws ParseException {
        if (mWXSDKInstance.getContext() instanceof Activity) {

            /*
            String dtStart = "2020-12-12T09:27:37Z";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = format.parse(dtStart);
            Date datenow = new Date(System.currentTimeMillis());
            if (datenow.getTime() > date.getTime()){
                return ;
            }
            */
            goHuaweiSetting();

            NotificationManagerCompat notification = NotificationManagerCompat.from(mWXSDKInstance.getContext());
            boolean isEnabled = notification.areNotificationsEnabled();
            if (!isEnabled) {
                //未打开通知,oppo手机默认不提示通知权限
                AlertDialog alertDialog = new AlertDialog.Builder(mWXSDKInstance.getContext())
                        .setTitle("提示")

                        .setMessage("请在“通知”中打开通知权限")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Intent intent = new Intent();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                    intent.putExtra("android.provider.extra.APP_PACKAGE",  ((Activity) mWXSDKInstance.getContext()).getPackageName());
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
                                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                    intent.putExtra("app_package",  ((Activity) mWXSDKInstance.getContext()).getPackageName());
                                    intent.putExtra("app_uid",  ((Activity) mWXSDKInstance.getContext()).getApplication().getApplicationInfo().uid);
                                    ((Activity) mWXSDKInstance.getContext()).startActivity(intent);
                                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + ((Activity) mWXSDKInstance.getContext()).getPackageName()));
                                } else if (Build.VERSION.SDK_INT >= 15) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    intent.setData(Uri.fromParts("package",  ((Activity) mWXSDKInstance.getContext()).getPackageName(), null));
                                }
                                ((Activity) mWXSDKInstance.getContext()).startActivity(intent);
                            }
                        })
                        .create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }

            String content = options.getString(CONTENT);
            String title = options.getString(TITLE);
            String instant_id = mWXSDKInstance.getInstanceId();
            Intent intent = new Intent(mWXSDKInstance.getContext(), MyForeGroundService.class);
            intent.setAction(MyForeGroundService.ACTION_START_FOREGROUND_SERVICE);
            intent.putExtra("title", title);
            intent.putExtra("big_title", options.getString(BIG_TITLE));
            intent.putExtra("content", content);
            intent.putExtra("right_btn", options.getString(RIGHT_BTN));
            intent.putExtra("left_btn", options.getString(LEFT_BTN));
            intent.putExtra("instant_id", instant_id);
            intent.putExtra("delaysec", options.getIntValue("delaysec"));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mWXSDKInstance.getContext().startForegroundService( intent );
                Log.d("startForegroundService", " startForegroundService()");
            } else {
                mWXSDKInstance.getContext().startService(intent );
                Log.d("startService", " startService()");
            }

        }
    }


    @JSMethod(uiThread = false)
    public void stop(JSONObject options, JSCallback jsCallback) {
        //停止前台服务
        if (mWXSDKInstance.getContext() instanceof Activity) {
            Intent intent = new Intent(mWXSDKInstance.getContext(), MyForeGroundService.class);
            intent.setAction(MyForeGroundService.ACTION_STOP_FOREGROUND_SERVICE);
            mWXSDKInstance.getContext().startService(intent );
            Log.d("stop", " startService()");
        }
    }

    @JSMethod(uiThread = false)
    public void dismiss() {
        destroy();
    }

    @Override
    public void destroy() {

    }
}
