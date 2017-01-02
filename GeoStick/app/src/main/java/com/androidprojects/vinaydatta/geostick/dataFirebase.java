package com.androidprojects.vinaydatta.geostick;

import java.io.Serializable;

/**
 * Created by vinay on 4/30/2016.
 */
public class dataFirebase implements Serializable{

    private String title;
    private String message;
    private String address;
    private String date;
    private String time;
    private boolean repeat;
    private double latitude;
    private double longitude;
    private int uniqueid;

    public dataFirebase(){}

    public dataFirebase(String title,String message,String address,String date,String time,boolean repeat,double latitude,double longitude,int uniqueid){

        this.title = title;
        this.message = message;
        this.address = address;
        this.date = date;
        this.time = time;
        this.repeat = repeat;
        this.latitude = latitude;
        this.longitude = longitude;
        this.uniqueid = uniqueid;

    }

    public String gettitle() {
        return title;
    }

    public String getmessage() {
        return message;
    }

    public String getaddress() {
        return address;
    }

    public String getdate() {
        return date;
    }

    public String gettime() {
        return time;
    }

    public boolean getrepeat() {
        return repeat;
    }
    public double getlatitude() {
        return latitude;
    }
    public double getlongitude() {
        return longitude;
    }
    public double getuniqueid() {
        return uniqueid;
    }
}
