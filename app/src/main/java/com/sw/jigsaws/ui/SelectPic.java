package com.sw.jigsaws.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.sw.jigsaws.R;
import com.sw.jigsaws.data.GameData;
import com.sw.jigsaws.utils.ImageSize;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectPic extends Activity implements OnClickListener {

    private static String TAG = "SelectPic";

    private ImageView imgPreView;
    private Button button_rotation, button_tryPlay, btn_take_photo, btn_select_img, btn_cancel;
    private Bitmap image;
    private AlertDialog loadingAlert;
    private String photo_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_img);

        imgPreView = (ImageView) this.findViewById(R.id.pic_view);

        button_tryPlay = (Button) this.findViewById(R.id.button_tryplay);
        button_rotation = (Button) this.findViewById(R.id.button_rotation);

        btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
        btn_select_img = (Button) this.findViewById(R.id.btn_select_img);
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);

        //  imgPreView.setVisibility(View.GONE);
        button_tryPlay.setVisibility(View.GONE);
        button_rotation.setVisibility(View.GONE);

        // 添加按钮监听
        btn_cancel.setOnClickListener(this);
        btn_select_img.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
        button_tryPlay.setOnClickListener(this);
        button_rotation.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Log.d(TAG, "----" + requestCode + "  " + resultCode + "  ");

        if (resultCode != RESULT_OK) {
            return;
        }

        //拍照
        if (requestCode == 1) {

            if (photo_name != null) {
                image = BitmapFactory.decodeFile(photo_name);
                photo_name = null;
            } else {
                try {
                    image = (Bitmap) intent.getExtras().get("data");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        //选择图片
        if (requestCode == 2) {
            if (intent.getData() != null) {
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), intent.getData());
                } catch (Exception e) {
                    e.printStackTrace();

                    AlertDialog.Builder builder = new AlertDialog.Builder(SelectPic.this).setTitle("出错了").setMessage("图片读取出问题了。。。");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();

                }
            }
        }

        if (image != null) {

            imgPreView.setVisibility(View.VISIBLE);
            button_tryPlay.setVisibility(View.VISIBLE);
            button_rotation.setVisibility(View.VISIBLE);

            btn_take_photo.setVisibility(View.GONE);
            btn_select_img.setVisibility(View.GONE);

            imgPreView.setImageResource(R.mipmap.logo);

            loadingAlert = new AlertDialog.Builder(SelectPic.this).setTitle("请稍后")//设置对话框标题
                    .setMessage("图片调整中。。。").show();

            //压缩图片
            new Thread(new CompImagesRunnable(image, new Handler() {
                public void handleMessage(Message msg) {
                    loadingAlert.dismiss();
                    image = (Bitmap) msg.obj;
                    loadingAlert.dismiss();
                    imgPreView.setImageBitmap(image);
                }
            })).start();
        }
    }


    private String getFileName() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String fileName = Environment.getExternalStorageDirectory() + "/" + formatter.format(date) + ".PNG";

        return fileName;
    }

    public String filePath;

    public void onClick(View v) {
        switch (v.getId()) {
            //拍照
            case R.id.btn_take_photo:
                try {
                    if (isSDAvaiable()) {
                        photo_name = getFileName();

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photo_name)));
                        startActivityForResult(intent, 1);
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            //相册选取
            case R.id.btn_select_img:
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 2);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            //取消
            case R.id.btn_cancel:
                finish();
                break;

            //试玩
            case R.id.button_tryplay:
                if (image != null) {
                    GameData.setGame(null, null, true);
                    GameData.setGame(image);
                    startActivity(new Intent(SelectPic.this, PlayGame.class));
                }
                break;
            //旋转
            case R.id.button_rotation:
                if (image != null) {
                    rotation();
                }
                break;

            default:
                break;
        }

    }

    private boolean isSDAvaiable() {
        String externalStorageState = Environment.getExternalStorageState();
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }

    }

    private void rotation() {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(90);
        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        imgPreView.setImageBitmap(image);
    }


    public class CompImagesRunnable implements Runnable {

        private Bitmap bitmap;
        private Handler handler;

        public CompImagesRunnable(Bitmap bitmap, Handler handler) {
            this.bitmap = bitmap;
            this.handler = handler;
        }

        @Override
        public void run() {
            image = ImageSize.compPixelsAndQuality(image);
            handler.obtainMessage(1, image).sendToTarget();
        }
    }

}
