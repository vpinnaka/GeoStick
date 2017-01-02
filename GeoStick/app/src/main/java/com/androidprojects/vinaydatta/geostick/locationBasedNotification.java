package com.androidprojects.vinaydatta.geostick;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.client.AuthData;
import com.google.gson.Gson;

import java.util.Calendar;

/**
 * Created by vinay on 4/28/2016.
 */
public class locationBasedNotification extends BroadcastReceiver {

    int uniqueKeyId = 0;
    String Title = "";
    String message = "";
    String Address = "";
    double latitude, longitude;
    String modeofsharing,date,time;

    /* Data from the authenticated user */
    private AuthData mAuthData;


    private static final String UNIQUE_ID = "UniqueId";
    private static final String TITLE = "Title";
    private static final String MESSAGE = "Message";
    private static final String ADDRESS = "Address";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private static final String AUTHENTICATION = "Authentication";

    private  static  final String OPERATION = "Operation";
    private  static  final String ADD_NEW = "Addnew";
    private  static  final String VIEW_OLD = "View";

    private  static  final String VIEW_DATA = "View_Data";

    private  static  final String MODE = "Mode";
    private  static  final String PUBLIC = "Public";
    private  static  final String SHARED = "Shared";
    private  static  final String ONLYME = "Onlyme";

    public final static String LOCATION_INTENT = "com.android.geostick.premenentlocation";

    @Override
    public void onReceive(Context context, Intent intent) {

        add_message myid = new add_message();


        Log.i("Broadcast reciever","I am recieving");

        Intent myIntent = new Intent(context, add_message.class);

        myIntent.putExtra(OPERATION,VIEW_OLD);



        uniqueKeyId = intent.getExtras().getInt(UNIQUE_ID);
        Title = intent.getExtras().getString(TITLE);
        message = intent.getExtras().getString(MESSAGE);
        Address =  intent.getExtras().getString(ADDRESS);
        latitude = intent.getExtras().getDouble(LATITUDE);
        longitude = intent.getExtras().getDouble(LONGITUDE);
        modeofsharing = intent.getExtras().getString(MODE);

        myIntent.putExtra(MODE,modeofsharing);

        dataFirebase markerdata = new dataFirebase(Title,message,Address,"","",false,latitude,longitude,(int)uniqueKeyId);
        myIntent.putExtra(VIEW_DATA,markerdata);


        PendingIntent pendingIntent = null;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (intent.getAction().equals(LOCATION_INTENT))
        {

                pendingIntent = PendingIntent.getActivity(context, uniqueKeyId, myIntent, 0);
                locationManager.removeUpdates(pendingIntent);


        }


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(Title)
                .setContentText(message)
                .setSmallIcon(R.drawable.pinkmarker)
                .setContentIntent(pendingIntent).build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
        notificationManager.notify(uniqueKeyId, notification);



    }



   /* public void datefornotification(Context context)
    {

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, locationBasedNotification.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

// Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);

// setRepeating() lets you specify a precise custom interval--in this case,
// 20 minutes.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public void startLocationBroadcastReciever(Context context,double latitude,double longitude,String title,String text,int uniquekey,long interval){


        Intent intent = new Intent("com.arao.localreminder.googlelocation.proximity");
        intent.putExtra("uniquekey", uniquekey);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        LocationManager mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        //PendingIntent proximityintent =  PendingIntent.getBroadcast(context,);
    }*/
}
