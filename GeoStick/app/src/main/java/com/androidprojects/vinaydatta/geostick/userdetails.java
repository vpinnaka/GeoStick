package com.androidprojects.vinaydatta.geostick;

/**
 * Created by vinay on 5/2/2016.
 */
public class userdetails {

    private String username;
    private String email;
    private double latitude;
    private double longitude;

    public userdetails(){}

    public userdetails(String username,String email,double latitude,double longitude) {

        this.username = username;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public String getusername() {
        return username;
    }

    public String getemail() {
        return email;
    }

    public double getlatitude() {
        return latitude;
    }
    public double getlongitude() {
        return longitude;
    }

}
