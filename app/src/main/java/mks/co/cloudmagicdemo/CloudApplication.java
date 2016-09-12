package mks.co.cloudmagicdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by Mahesh on 7/9/16.
 */
public class CloudApplication extends Application{
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
