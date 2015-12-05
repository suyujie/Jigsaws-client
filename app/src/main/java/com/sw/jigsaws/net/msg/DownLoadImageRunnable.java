package com.sw.jigsaws.net.msg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.sw.jigsaws.data.GameData;
import com.sw.jigsaws.net.NetUtils;

public class DownLoadImageRunnable extends AbstractRunnable {

    private String TAG = "DownLoadImageRunnable";

    private Handler handler;

    public DownLoadImageRunnable(Handler handler) {
        this.handler = handler;
    }

    @Override

    public void run() {

        byte[] imgBytes = NetUtils.readImageFromNet(GameData.game.url);

        if (imgBytes == null) {
            handler.obtainMessage(NetERROR, null).sendToTarget();
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            handler.obtainMessage(NetOK, bitmap).sendToTarget();
        }

    }
}
