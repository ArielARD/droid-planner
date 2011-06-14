package com.smartpoint.android.planner.core;

import android.app.Application;

public class MainApplication extends Application {

    public static String APPLICATION_TAG = "droid-planner";

    public static class Version {
        public static String MINOR_VERSION = "0b1";
        public static String MAJOR_VERSION = "1.0";
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onTerminate() {
    }
}
