package com.sw.jigsaws.net.msg;

import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.sw.jigsaws.data.GameData;
import com.sw.jigsaws.net.NetUtils;

public class ImageRunnable extends AbstractRunnable {

    private String TAG = "ImageRunnable";

    private Handler handler;

    public ImageRunnable(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {

        ImageMsg imageMsg = new ImageMsg();

        JSONObject result = NetUtils.doPostJson(imageMsg.toJson());

        if (result != null) {
            Log.i(TAG, result.toJSONString());
            GameData.setGame(result.getLong("imageId"), result.getString("url"), false);
            handler.obtainMessage(NetOK, null).sendToTarget();
        } else {
            handler.obtainMessage(NetERROR).sendToTarget();
        }

    }


    public class ImageMsg extends AbstractMsg {

        private Integer commandId = 201;

        public ImageMsg() {

        }

        public String toJson() {
            JSONObject json = super.toJson(commandId);
            return json.toString();
        }

    }
}
