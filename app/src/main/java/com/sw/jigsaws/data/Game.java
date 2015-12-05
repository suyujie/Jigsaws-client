package com.sw.jigsaws.data;

import android.graphics.Bitmap;

import com.sw.jigsaws.utils.Utils;

public class Game {

    public Long imageId;
    public String url;
    public Bitmap bitmap;
    public boolean isTryPlay;

    public Integer line = 4;
    public Integer column = 3;

    public Game() {

    }

    public void setHard(int easy_hard) {
        switch (easy_hard) {
            case 1:
                this.line = this.column = Utils.randomInt(3, 3);
                break;
            case 2:
                this.line = this.column = Utils.randomInt(3, 4);
                break;
            case 3:
                this.line = this.column = Utils.randomInt(4, 5);
                break;
        }
    }

}