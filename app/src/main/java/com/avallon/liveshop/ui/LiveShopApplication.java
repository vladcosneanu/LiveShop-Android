package com.avallon.liveshop.ui;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class LiveShopApplication extends Application {

    private static LiveShopApplication singleton;

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public static LiveShopApplication getInstance() {
        return singleton;
    }
}
