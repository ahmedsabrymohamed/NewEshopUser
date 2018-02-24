package com.fromscratch.mine.myapplicationuser;


import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/*
 * initializing Fresco for loading and displaying images
 */
public class AppStart extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }


}