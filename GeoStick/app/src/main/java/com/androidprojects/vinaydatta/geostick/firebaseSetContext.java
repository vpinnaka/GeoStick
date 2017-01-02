package com.androidprojects.vinaydatta.geostick;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by vinaydatta on 4/3/16.
 */
public class firebaseSetContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

    }
}
