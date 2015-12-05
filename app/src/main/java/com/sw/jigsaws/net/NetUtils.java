package com.sw.jigsaws.net;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.sw.jigsaws.data.Config;
import com.sw.jigsaws.utils.StreamTool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import gamecore.io.ByteArrayGameInput;
import gamecore.io.GameInput;

/**
 *
 */
public class NetUtils {

    private static String TAG = "NetUtils";

    public static String gameJsonUrl = Config.domain + "game_json";
    public static String gameStreamUrl = Config.domain + "game_stream";

    public static JSONObject doPostJson(String params) {

        HttpURLConnection conn = null;

        try {
            URL url = new URL(gameJsonUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(1000 * 5);
            conn.getOutputStream().write(params.getBytes("UTF-8"));
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

                Log.e(TAG, "httpCode:" + conn.getResponseCode() + "==" + params);

                return null;
            } else {
                return readAsJson(conn.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect(); //中断连接
            }
        }
        return null;
    }


    public static GameInput doPostStream(byte[] bytes) {

        HttpURLConnection conn = null;

        try {
            URL url = new URL(gameStreamUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(1000 * 20);
            conn.getOutputStream().write(bytes);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            int httpCode = conn.getResponseCode();
            Log.d(TAG + "--------net stream", "httpCode:" + httpCode);
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            } else {
                return readAsGameInput(conn.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect(); //中断连接
            }
        }
        return null;
    }


    public static byte[] readAsStream(InputStream is) throws IOException {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            if (null != is) {
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out.toByteArray();
    }

    public static JSONObject readAsJson(InputStream is) throws IOException {
        byte[] data = readAsStream(is);
        return JSONObject.parseObject(new String(data, Charset.forName("UTF-8")));
    }

    public static GameInput readAsGameInput(InputStream is) throws IOException {
        byte[] data = readAsStream(is);
        ByteArrayGameInput arrayGameInput = new ByteArrayGameInput(data);
        return arrayGameInput;
    }

    public static byte[] readImageFromNet(String imgUrl) {

        try {
            URL url = new URL(imgUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");   //设置请求方法为GET
            conn.setReadTimeout(15 * 1000);    //设置请求过时时间为15秒
            InputStream inputStream = conn.getInputStream();   //通过输入流获得图片数据
            byte[] data = StreamTool.readInputStream(inputStream);     //获得图片的二进制数据
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
