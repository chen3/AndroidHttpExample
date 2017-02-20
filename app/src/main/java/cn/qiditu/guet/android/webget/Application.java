package cn.qiditu.guet.android.webget;

import android.annotation.SuppressLint;
import android.content.Context;

public class Application extends android.app.Application {

    @SuppressLint("StaticFieldLeak")
    private static Context globalContext;
    public static Context getGlobalApplicationContext() {
        return globalContext;
    }

    @Override
    public void onCreate() {
        globalContext = this.getApplicationContext();
        super.onCreate();
    }

}
