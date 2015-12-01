package com.sw.jigsaws.net.msg;

import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.sw.jigsaws.net.NetUtils;

/**
 * Created by suiyujie on 2015/11/14.
 */
public class CommentRunnable extends AbstractRunnable {

    private String TAG = "CommentRunnable";

    private Long imageId;
    private int comment;

    private Handler handler;

    public CommentRunnable(Long imageId, int comment, Handler handler) {
        this.imageId = imageId;
        this.comment = comment;
        this.handler = handler;
    }

    @Override
    public void run() {

        CommentMsg commentMsg = new CommentMsg(imageId, comment);

        JSONObject result = NetUtils.doPostJson(commentMsg.toJson());
        if (result != null) {
            Log.w(TAG, "result = " + result.toString());

            handler.obtainMessage(NetOK, result).sendToTarget();
        } else {
            handler.obtainMessage(NetERROR).sendToTarget();
        }

    }


    class CommentMsg extends AbstractMsg {

        private Integer commandId = 401;
        private Long imageId;
        private int comment;

        public CommentMsg(Long imageId, int comment) {
            this.imageId = imageId;
            this.comment = comment;
        }

        public String toJson() {
            JSONObject json = super.toJson(commandId);
            json.put("imageId", imageId);
            json.put("comment", comment);
            return json.toString();
        }

    }

}
