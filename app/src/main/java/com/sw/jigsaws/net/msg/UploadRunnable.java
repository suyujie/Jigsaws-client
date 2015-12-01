package com.sw.jigsaws.net.msg;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.sw.jigsaws.data.GameData;
import com.sw.jigsaws.net.NetUtils;

import java.io.ByteArrayOutputStream;

import gamecore.io.ByteArrayGameOutput;
import gamecore.io.GameInput;

/**
 * Created by suiyujie on 2015/11/14.
 */
public class UploadRunnable extends AbstractRunnable {

    private static final String TAG = "UploadRunnable";

    private Bitmap bitmap;
    private Handler handler;

    public UploadRunnable(Bitmap bitmap, Handler handler) {
        this.bitmap = bitmap;
        this.handler = handler;
    }

    @Override
    public void run() {

        UploadMsg uploadMsg = new UploadMsg(bitmap);

        GameInput result = NetUtils.doPostStream(uploadMsg.toBytes());

        Integer commendId = result.getInt();

        byte status = result.get();

        handler.obtainMessage(1, status).sendToTarget();

    }


    public class UploadMsg {

        private Integer commandId = 301;
        private Bitmap bitmap;

        public UploadMsg(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public byte[] toBytes() {
            try {

                ByteArrayGameOutput gop = new ByteArrayGameOutput();

                gop.putInt(commandId);
                gop.putString(GameData.sessionId);

                byte[] bytes = bitMap2Bytes(bitmap);
                gop.putBytes(bytes);

                return gop.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        public byte[] bitMap2Bytes(Bitmap bmp) {

            ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象

            int options = 80;

            while (true) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩

                bmp.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到baos中

                Log.e("---------", "---" + output.toByteArray().length + "---------");

                if (output.toByteArray().length > 80 * 1024) {
                    output.reset();//重置baos即清空output
                    options -= 10;//每次都减少10
                } else {
                    break;
                }
            }

            bmp.recycle();//自由选择是否进行回收

            byte[] result = output.toByteArray();//转换成功了
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

    }

}
