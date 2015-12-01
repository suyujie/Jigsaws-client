package com.sw.jigsaws.net.msg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.sw.jigsaws.net.NetUtils;

/**
 * Created by suiyujie on 2015/11/14.
 */
public class DownLoadImageRunnable extends AbstractRunnable {

    private String TAG = "DownLoadImageRunnable";

    private Long imageId;
    private Handler handler;

    public DownLoadImageRunnable(Long imageId, Handler handler) {
        this.imageId = imageId;
        this.handler = handler;
    }

    @Override

    public void run() {

        byte[] imgBytes = NetUtils.readImageFromNet(imageId);

        if (imgBytes == null) {
            handler.obtainMessage(NetERROR, null).sendToTarget();
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            handler.obtainMessage(NetOK, bitmap).sendToTarget();
        }

    }
}
