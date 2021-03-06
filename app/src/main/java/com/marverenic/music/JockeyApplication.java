package com.marverenic.music;

import android.app.Application;

import com.marverenic.music.utils.Debug;
import com.marverenic.music.utils.Themes;
import com.squareup.picasso.Picasso;

public class JockeyApplication extends Application implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultHandler;
    private long runningActivities = 0;

    @Override
    public void onCreate() {
        setTheme(Themes.getTheme(this));
        super.onCreate();

        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        if (BuildConfig.DEBUG)
            Picasso.setSingletonInstance(new Picasso.Builder(this).indicatorsEnabled(true).build());

        PlayerController.bind(this);
    }

    @Override
    public void uncaughtException (Thread thread, Throwable t) {
        Debug.log(t, this);
        defaultHandler.uncaughtException(thread, t);
    }

    public void activityCreated(){
        PlayerController.bind(this);
        ++runningActivities;
    }

    public void activityResumed() {
    }

    public void activityPaused() {
    }

    public void activityDestroyed(){
        --runningActivities;
        if (runningActivities == 0){
            PlayerController.unbind(this);
        }
    }
}
