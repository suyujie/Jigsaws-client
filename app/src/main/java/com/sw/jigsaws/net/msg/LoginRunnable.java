package com.sw.jigsaws.net.msg;

import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.sw.jigsaws.net.NetUtils;

/**
 * Created by suiyujie on 2015/11/14.
 */
public class LoginRunnable extends AbstractRunnable {

    private String TAG = "LoginRunnable";

    private String deviceId;
    private Handler handler;

    public LoginRunnable(String deviceId, Handler handler) {
        this.deviceId = deviceId;
        this.handler = handler;
    }

    @Override
    public void run() {

        LoginMsg loginMsg = new LoginMsg(deviceId);

        JSONObject result = NetUtils.doPostJson(loginMsg.toJson());
        if (result != null) {
            Log.w(TAG, "result = " + result.toString());
        }

        // 获取图片成功，向UI线程发送MSG_SUCCESS标识和bitmap对象
        handler.obtainMessage(1, result).sendToTarget();

    }


    public class LoginMsg {

        private Integer commandId = 101;
        private String deviceId;

        public LoginMsg(String deviceId) {
            this.deviceId = deviceId;
        }

        public String toJson() {
            JSONObject json = new JSONObject();
            json.put("commandId", commandId);
            json.put("deviceId", deviceId);
            return json.toString();
        }

    }

}
