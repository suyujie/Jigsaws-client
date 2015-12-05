package com.sw.jigsaws.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sw.jigsaws.R;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

public class Home extends Activity implements View.OnClickListener {

    private Button btn_play;
    private Button btn_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        btn_play = (Button) findViewById(R.id.home_btn_play);

        btn_upload = (Button) findViewById(R.id.home_btn_upload);

        btn_play.setOnClickListener(this);
        btn_upload.setOnClickListener(this);

        //有米广告条
        AdView adView = new AdView(this, AdSize.FIT_SCREEN);
        LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
        adLayout.addView(adView);

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_btn_play:
                startActivity(new Intent(Home.this, PlayGame.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case R.id.home_btn_upload:
                startActivity(new Intent(Home.this, SelectPic.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            builder.setMessage("确认要退出么？");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                    System.exit(0);
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
