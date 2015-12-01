package com.sw.jigsaws.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by suiyujie on 2015/11/3.
 */
public class ImageSize {

    //使用Bitmap加Matrix来缩放
    public static Bitmap resizeImage(Bitmap b, int x, int y, boolean lashen) {
        int w = b.getWidth();
        int h = b.getHeight();
        float sx = (float) x / w;//要强制转换，不转换我的在这总是死掉。
        float sy = (float) y / h;
        Matrix matrix = new Matrix();

        if (lashen) {//可拉伸
            matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        } else {//不可拉伸
            if (sx < sy)
                matrix.postScale(sx, sx); // 长和宽放大缩小的比例
            else
                matrix.postScale(sy, sy); // 长和宽放大缩小的比例
        }


        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
        return resizeBmp;
    }


    //分辨率压缩,质量压缩
    public static Bitmap compPixelsAndQuality(Bitmap image) {
        return compQuality(compPixels(image));
    }


    //分辨率压缩
    public static Bitmap compPixels(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是960*640分辨率，所以高和宽我们设置为,但是考虑到可能需要旋转，所有都以960为最大
        float hh = 960f;//这里设置高度为960f
        float ww = 960f;//这里设置宽度为960f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;
    }


    public static Bitmap compQuality(Bitmap image) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
        int options = 80;

        while (true) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            image.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到baos中
            if (output.toByteArray().length > 80 * 1024) {
                output.reset();//重置baos即清空baos
                options -= 10;//每次都减少10
            } else {
                break;
            }
        }
        Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(output.toByteArray()), null, null);//把ByteArrayInputStream数据生成图片

        return bitmap;
    }
}
