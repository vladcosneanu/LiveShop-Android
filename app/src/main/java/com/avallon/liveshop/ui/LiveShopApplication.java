package com.avallon.liveshop.ui;

import android.app.Application;

public class LiveShopApplication extends Application {

    private static LiveShopApplication singleton;

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;
    }

    public static LiveShopApplication getInstance() {
        return singleton;
    }
}
