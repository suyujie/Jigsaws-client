package com.sw.jigsaws;

import net.youmi.android.AdManager;

public class MainApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AdManager.getInstance(getBaseContext()).init("3cf28cfbb057b9fe", "746667919019d3e4", true);
    }
}
