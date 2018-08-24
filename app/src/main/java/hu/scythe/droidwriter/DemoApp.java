package hu.scythe.droidwriter;

import android.app.Application;

public class DemoApp extends Application {

    private static DemoApp sInstance;

    public static DemoApp getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
