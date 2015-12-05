package com.sw.jigsaws;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.sw.jigsaws.data.GameData;
import com.sw.jigsaws.net.msg.LoginRunnable;
import com.sw.jigsaws.service.HeartbeatService;
import com.sw.jigsaws.ui.Home;
import com.sw.jigsaws.ui.Tutorial;

public class BootStrap extends Activity {

    private static String TAG = "BootStrap";

    private AlertDialog loadingAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String deviceId = TelephonyMgr.getDeviceId();

        Log.w(TAG, deviceId);

        loadingAlert = new AlertDialog.Builder(BootStrap.this).setTitle("系统提示")//设置对话框标题
                .setMessage("联网中。。。").setCancelable(false).show();

        //登陆或者注册
        Log.d("text", "登陆或者注册。。。。。。。");
        new Thread(new LoginRunnable(deviceId, new Handler() {
            public void handleMessage(Message msg) {

                JSONObject loginResult = (JSONObject) msg.obj;

                if (loginResult == null) {
                    loadingAlert = new AlertDialog.Builder(BootStrap.this).setTitle("系统提示")//设置对话框标题
                            .setMessage("网络连接失败，请检查网络。。。").setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                } else {

                    Log.d(TAG, loginResult.toJSONString());

                    loadingAlert.dismiss();

                    GameData.sessionId = loginResult.getString("sessionId");

                    //打开心跳操作
                    Intent intent = new Intent(BootStrap.this, HeartbeatService.class);
                    startService(intent);

                    if (loginResult.getBoolean("reg")) {
                        startActivity(new Intent(BootStrap.this, Tutorial.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else {
                        startActivity(new Intent(BootStrap.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }

                    finish();

                }

            }
        })).start();

    }

}
