package com.sw.jigsaws.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.sw.jigsaws.data.GameData;
import com.sw.jigsaws.net.NetUtils;
import com.sw.jigsaws.utils.Utils;

import java.util.concurrent.TimeUnit;

import gamecore.io.task.TaskCenter;

public class HeartbeatService extends Service {

    private static String TAG = "HeartbeatService";

    @Override
    //Service时被调用
    public void onCreate() {
        Log.i(TAG, "Service onCreate--->");

        TaskCenter.getInstance().scheduleAtFixedRate(new HeartBeat(), Utils.randomInt(1, 5), 100, TimeUnit.SECONDS);

        super.onCreate();
    }

    @Override
    //当调用者使用startService()方法启动Service时，该方法被调用
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "Service onStart--->");
        super.onStart(intent, startId);
    }

    @Override
    //当Service不在使用时调用
    public void onDestroy() {
        Log.i(TAG, "Service onDestroy--->");
        super.onDestroy();
    }

    @Override
    //当使用startService()方法启动Service时，方法体内只需写return null
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 守护任务。
     * 内部线程类,过滤玩家
     */
    protected class HeartBeat implements Runnable {

        @Override
        public void run() {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("commandId", 1001);
            jsonObject.put("sessionId", GameData.sessionId);

            JSONObject resultJson = NetUtils.doPostJson(jsonObject.toJSONString());

            Log.e(TAG, resultJson.toJSONString());

        }
    }
}  