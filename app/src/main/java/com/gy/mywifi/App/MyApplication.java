package com.gy.mywifi.App;

import android.app.Application;

import com.gy.mywifi.manager.WifiController;

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        WifiController.instance().init(this);
    }
}
