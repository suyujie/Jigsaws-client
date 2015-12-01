package com.sw.jigsaws.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sw.jigsaws.R;
import com.sw.jigsaws.data.Game;
import com.sw.jigsaws.data.GameData;
import com.sw.jigsaws.game.GameJigsawsLayout;
import com.sw.jigsaws.net.msg.CommentRunnable;
import com.sw.jigsaws.net.msg.DownLoadImageRunnable;
import com.sw.jigsaws.net.msg.ImageRunnable;
import com.sw.jigsaws.net.msg.UploadRunnable;
import com.sw.jigsaws.utils.Utils;

public class PlayGame extends Activity implements View.OnClickListener {

    private static final String TAG = "PlayGame";

    private RelativeLayout game_parent_layout;

    private Button button_good;
    private Button button_bad;
    private Button button_upload;
    private Button button_cancel;
    private Button button_giveup;

    private AlertDialog loadingAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        game_parent_layout = (RelativeLayout) findViewById(R.id.game_parent_layout);

        button_good = (Button) this.findViewById(R.id.game_btn_good);
        button_bad = (Button) this.findViewById(R.id.game_btn_bad);
        button_upload = (Button) this.findViewById(R.id.game_btn_upload);
        button_cancel = (Button) this.findViewById(R.id.game_btn_cancel);
        button_giveup = (Button) this.findViewById(R.id.game_btn_giveup);

        button_good.setVisibility(View.GONE);
        button_bad.setVisibility(View.GONE);
        button_upload.setVisibility(View.GONE);
        button_cancel.setVisibility(View.GONE);

        // 添加按钮监听
        button_good.setOnClickListener(this);
        button_bad.setOnClickListener(this);
        button_upload.setOnClickListener(this);
        button_cancel.setOnClickListener(this);
        button_giveup.setOnClickListener(this);

        if (GameData.game != null && GameData.game.isTryPlay) {

            LayoutInflater inflater = LayoutInflater.from(PlayGame.this);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);

            GameJigsawsLayout gameJigsawsLayout = new GameJigsawsLayout(PlayGame.this);
            gameJigsawsLayout.setLayoutParams(lp);//设置布局参数

            game_parent_layout.addView(gameJigsawsLayout);

        } else {
            //从网上下载图片玩
            loadingAlert = new AlertDialog.Builder(PlayGame.this).setTitle("系统提示")//设置对话框标题
                    .setMessage("准备中，游戏马上开始。。。").create();

            loadingAlert.setCanceledOnTouchOutside(false);
            loadingAlert.show();
            //开始下载图片---先下载一个图片地址，然后再下载图片
            Log.i("text", "访问图片。。。。。。。");

            new Thread(new ImageRunnable(readImgIdHandler)).start();
        }

    }


    public void showImage() {

        // 横幅广告
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.setAdSize(AdSize.FULL_BANNER);
//        mAdView.loadAd(adRequest);

        //展现图片,,,拼图成功，有两种  1 正常游戏， 2 试玩游戏
        ImageView view = new ImageView(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(0, 0);
        lp.width = game_parent_layout.getWidth();
        lp.height = game_parent_layout.getHeight();
        view.setLayoutParams(lp);
        view.setImageBitmap(GameData.game.bitmap);

        //移除掉游戏
        game_parent_layout.removeAllViews();
        //加入原图
        game_parent_layout.addView(view);

        if (GameData.game.isTryPlay) {//如果试玩，两个按钮， 上传  取消

            GameData.game.isTryPlay = false;

            button_upload.setVisibility(View.VISIBLE);
            button_cancel.setVisibility(View.VISIBLE);


        } else {//非试玩  ，两个按钮  好图  不好
            button_good.setVisibility(View.VISIBLE);
            button_bad.setVisibility(View.VISIBLE);
        }

    }

    Handler downImgHandler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == 0) {
                loadingAlert = new AlertDialog.Builder(PlayGame.this).setTitle("系统提示")//设置对话框标题
                        .setMessage("网络连接失败，请检查网络。。。")
                        .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            } else {
                // 如果成功，则显示从网络获取到的图片
                GameData.game.bitmap = (Bitmap) msg.obj;
                if (loadingAlert != null) {
                    loadingAlert.dismiss();
                }

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

                lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                GameJigsawsLayout gameJigsawsLayout = new GameJigsawsLayout(PlayGame.this);
                gameJigsawsLayout.setLayoutParams(lp);//设置布局参数

                game_parent_layout.addView(gameJigsawsLayout);
            }
        }
    };

    Handler readImgIdHandler = new Handler() {
        public void handleMessage(Message msg) {

            Log.e(this.getClass().toString(), "====" + msg.what);

            // 从网络获取图片地址
            GameData.game = new Game((Long) msg.obj, null, false, Utils.randomInt(0, 2));

            if (GameData.game.imageId != null) {
                new Thread(new DownLoadImageRunnable(GameData.game.imageId, downImgHandler)).start();
            }

        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            //好图
            case R.id.game_btn_good:
                loadingAlert = new AlertDialog.Builder(PlayGame.this).setTitle("系统提示")//设置对话框标题
                        .setMessage("评论提交中。。。").create();
                loadingAlert.setCanceledOnTouchOutside(false);
                loadingAlert.show();

                button_good.setVisibility(View.GONE);
                button_bad.setVisibility(View.GONE);

                new Thread(new CommentRunnable(GameData.game.imageId, 1, new Handler() {
                    public void handleMessage(Message msg) {
                        Log.e(TAG, "====" + msg.what);
                        loadingAlert.dismiss();
                        Toast.makeText(PlayGame.this, "感谢评价", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })).start();
                break;
            //不好
            case R.id.game_btn_bad:
                loadingAlert = new AlertDialog.Builder(PlayGame.this).setTitle("系统提示")//设置对话框标题
                        .setMessage("评论提交中。。。").setCancelable(false).show();
                button_good.setVisibility(View.GONE);
                button_bad.setVisibility(View.GONE);
                new Thread(new CommentRunnable(GameData.game.imageId, 2, new Handler() {
                    public void handleMessage(Message msg) {
                        Log.e(TAG, "====" + msg.what);
                        Toast.makeText(PlayGame.this, "感谢评价", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })).start();
                break;
            //上传
            case R.id.game_btn_upload:
                loadingAlert = new AlertDialog.Builder(PlayGame.this).setTitle("系统提示")//设置对话框标题
                        .setMessage("图片上传中。。。").create();
                loadingAlert.setCanceledOnTouchOutside(false);
                loadingAlert.show();

                new Thread(new UploadRunnable(GameData.game.bitmap, new Handler() {
                    public void handleMessage(Message msg) {
                        Log.e(TAG, "====" + msg.what);
                        loadingAlert.dismiss();
                        Toast.makeText(PlayGame.this, "上传成功，其他玩家会拼图你的图片", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(PlayGame.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                })).start();
                break;
            //取消
            case R.id.game_btn_cancel:
                GameData.game = null;
                finish();
                startActivity(new Intent(PlayGame.this, SelectPic.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            //放弃
            case R.id.game_btn_giveup:
                GameData.game = null;
                finish();
                startActivity(new Intent(PlayGame.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;

            default:
                break;
        }

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.e("keyCode", "==" + keyCode);

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PlayGame.this);
            builder.setMessage("确认要退出本次游戏么？");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    GameData.game = null;
                    finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();

        }
        return false;
    }

}
