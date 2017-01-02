package com.androidprojects.vinaydatta.geostick;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;


import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.gson.Gson;

import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Random;

public class add_message extends AppCompatActivity implements View.OnTouchListener, TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private Firebase mFirebaseRef;

    /* Data from the authenticated user */
    private AuthData mAuthData;



    private EditText mTitle, mMessage, mAddress, mStartDate, mStartTime, mEndDate, mEndTime;
    private Button mSaveLocationData;
    private Switch datetimeswitch;
    private static Double latitude, longitude;
    private static int uniqueId;


    private String modeofsharing;

    private String addorviewoperation;

    private String dTitle, dMessage, dAddress, dStartDate, dEndDate, dStarttime, dEndtime;

    private static final String PUBLIC = "Public";
    private  static  final String SHARED = "Shared";
    private  static  final String ONLYME = "Onlyme";

    private static final String IGEOSTICK = "I Geostick";
    private  static  final String YOUGEOSTICK = "You Geostick";
    private  static  final String WEGEOSTICK = "We Geostick";

    private  static  final String MODE = "Mode";
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

    public final static String LOCATION_INTENT = "com.android.geostick.premenentlocation";
    public static String MY_ID = "";


    private static long POINT_RADIUS = 1000; // in Meters

    static int TIME_DIALOG = 0;
    static int DATE_DIALOG = 0;

    private static long TIME_INTERVEL = -1;
    private static long PROX_ALERT_EXPIRATION = -1;

    int targetYear, targetMonth, targetday, targetHour, targetminute;
    //new sendStartHour
    int startYear, startMonth, startday, startHour, startMinute, sendStartHour;


    //DialogFragment newFragment = new TimePickerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.msssageToolbar);
        setSupportActionBar(toolbar);

        mFirebaseRef = new Firebase(getResources().getString(R.string.firebaseURL));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        getsharedauthenticationdata();

        if(mAuthData!=null)
            MY_ID = mAuthData.getUid();




        mTitle = (EditText) findViewById(R.id.messagetitle);
        mMessage = (EditText) findViewById(R.id.messageDiscription);
        mAddress = (EditText) findViewById(R.id.locationaddress);
        mStartDate = (EditText) findViewById(R.id.startdate);
        mStartTime = (EditText) findViewById(R.id.starttime);
        mEndDate = (EditText) findViewById(R.id.enddate);
        mEndTime = (EditText) findViewById(R.id.endtime);

        mSaveLocationData = (Button) findViewById(R.id.savelocationdata);

        datetimeswitch = (Switch) findViewById(R.id.datetimeswitch);


        Bundle bundle = getIntent().getExtras();
        getDataFromBundle(bundle);

        mStartDate.setOnTouchListener(this);
        mEndDate.setOnTouchListener(this);
        mStartTime.setOnTouchListener(this);
        mEndTime.setOnTouchListener(this);

        datetimeswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (datetimeswitch.isChecked()) {
                    mStartTime.setVisibility(View.VISIBLE);
                    mStartDate.setVisibility(View.VISIBLE);
                    mEndDate.setVisibility(View.VISIBLE);
                    mEndTime.setVisibility(View.VISIBLE);
                } else {
                    mStartTime.setVisibility(View.GONE);
                    mStartDate.setVisibility(View.GONE);
                    mEndDate.setVisibility(View.GONE);
                    mEndTime.setVisibility(View.GONE);
                }

            }
        });
        mSaveLocationData.setOnClickListener(mSavedataOnClickListner);





        /*if(mAuthData!=null)
            uniqueId = mAuthData.getUid();*/



        Log.i(UNIQUE_ID, "" + uniqueId);

    }


    private void getDataFromBundle(Bundle bundle)
    {
        addorviewoperation = bundle.getString(OPERATION);
        modeofsharing = bundle.getString(MODE);


        if(addorviewoperation.equals(ADD_NEW))
        {
            latitude =  bundle.getDouble(LATITUDE);
            longitude =  bundle.getDouble(LONGITUDE);
            mAddress.setText(bundle.getString(ADDRESS));
            addgeostickoperation(modeofsharing);
        }else if(addorviewoperation.equals(VIEW_OLD)){


            dataFirebase data = (dataFirebase)bundle.getSerializable(VIEW_DATA);

            viewgeostickoperation(data.gettitle(),data.getmessage(),data.getaddress(),data.getdate(),data.gettime(),data.getrepeat(),data.getlatitude(),data.getlongitude(),(int) data.getuniqueid());
        }


    }

    private String gettitle(String mode){
        if(mode.equals(ONLYME))
            return IGEOSTICK;
        else if(mode.equals(SHARED))
            return YOUGEOSTICK;
        else
            return WEGEOSTICK;
    }

    private void addgeostickoperation(String modeofsharing){

        setTitle(gettitle(modeofsharing));

        uniqueId = getRequestCode();

        mStartTime.setFocusable(false);
        mStartDate.setFocusable(false);
        mEndDate.setFocusable(false);
        mEndTime.setFocusable(false);

        mStartTime.setVisibility(View.GONE);
        mStartDate.setVisibility(View.GONE);
        mEndDate.setVisibility(View.GONE);
        mEndTime.setVisibility(View.GONE);


    }


    private void viewgeostickoperation(String title,String message,String address,String date,String time,boolean repeat,double latitude,double longitude,int uniquekeyId){

        uniqueId = uniquekeyId;

        setTitle(gettitle(modeofsharing));

        mTitle.setText(title);
        mAddress.setText(address);
        mMessage.setText(message);

        mTitle.setFocusable(false);
        mAddress.setFocusable(false);
        mMessage.setFocusable(false);

        mStartTime.setFocusable(false);
        mStartDate.setFocusable(false);
        mEndDate.setFocusable(false);
        mEndTime.setFocusable(false);

        mStartTime.setVisibility(View.GONE);
        mStartDate.setVisibility(View.GONE);
        mEndDate.setVisibility(View.GONE);
        mEndTime.setVisibility(View.GONE);

        mSaveLocationData.setVisibility(View.GONE);



    }



    @Override
    public void onResume() {
        super.onResume();
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialog");
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");

        if (tpd != null) tpd.setOnTimeSetListener(this);
        if (dpd != null) dpd.setOnDateSetListener(this);
    }


    private final View.OnClickListener mSavedataOnClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (verifyDataInAllFields()) {

                if (datetimeswitch.isChecked() && isDateandTimeValid()) {


                    //save data in local database


                    //GET THE REMAINING TIME
                    if (getTimeIntervel() > 0) {
                        TIME_INTERVEL = getTimeIntervel();
                        Log.e("ALert expration time ", "" + TIME_INTERVEL);
                    } else {
                        TIME_INTERVEL = -1;//NEVER EXPIRE
                    }
                    //Location reminder


                    savedateLocationtoReminders(dTitle, dAddress, TIME_INTERVEL);
                    //savedatatofirebase();

                } else {
                    savepermenantLocationReminder(getBaseContext(), dTitle, dMessage, uniqueId, latitude, longitude);

                    savedatatofirebase(dTitle, dMessage,dAddress,"","",false,latitude, longitude,uniqueId,modeofsharing);

                    afterSaveingGeostick();
                }


            } else
                return;
        }

    };


    private void afterSaveingGeostick(){
        Intent intent = new Intent(getApplicationContext(),homescreen.class);
        startActivity(intent);
        finish();
    }


    private void savedatatofirebase(String Title,String Message,String Address,String Date,String Time,boolean repeat,double latitude,double longitude,int uniqueId,String modeofsharing)
    {
        Log.i("UNIQUEID",""+uniqueId);
        String firbaseurl = "";

        dataFirebase senddata = new dataFirebase(Title,Message,Address,Date,Time,repeat,latitude,longitude,uniqueId);
        if(modeofsharing.equals(ONLYME))
        {
            firbaseurl = mAuthData.getUid();
        }
        else
        {
            firbaseurl = PUBLIC;
        }
        Firebase dataref = mFirebaseRef.child(firbaseurl);

        dataref.push().setValue(senddata);
    }

    private void savedateLocationtoReminders(String Title, String Address, long S_TIME_INTERVEL) {

        /*String startdate, enddate;

        startdate = mStartDate.getText().toString();
        enddate = mEndDate.getText().toString();
        locationBasedNotification locationBasedNotification = new locationBasedNotification();
        //locationBasedNotification.datefornotification();
*/
    }


    private int getsharedradius()
    {
        int value;
        SharedPreferences mPrefs = getSharedPreferences("radius",MODE_PRIVATE);

        String radius = mPrefs.getString("radiusdata", null);

        value = Integer.parseInt(radius);

        return value;

    }

    private void savepermenantLocationReminder(Context context, String title, String message, int uniqueId, double latitude, double longitude) {

        Intent intent = new Intent(LOCATION_INTENT);
        intent.putExtra(MODE,modeofsharing);
        intent.putExtra(UNIQUE_ID, uniqueId);
        intent.putExtra(TITLE, title);
        intent.putExtra(MESSAGE, message);
        intent.putExtra(LATITUDE, latitude);
        intent.putExtra(LONGITUDE, longitude);
        POINT_RADIUS = getsharedradius();
        // if no time range is given
        LocationManager mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        PendingIntent proximityIntent = PendingIntent.getBroadcast(context, uniqueId, intent, 0);

        if(proximityIntent == null)
            Log.i("proximityIntent","NULL");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                POINT_RADIUS, // the radius of the central point of the alert region, in meters
                PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );


    }

    private long getTimeIntervel()
    {
        long timeInterval = 0;
        try {
            
            final Calendar end = Calendar.getInstance();
            end.set(targetYear, targetMonth, targetday, targetHour, targetminute,0);
            final Calendar start = Calendar.getInstance();
            
            start.set(startYear, startMonth, startday, startHour, startMinute,0);
            Log.e("Target date with time "+end.getTime(),"Start Time "+start.getTime());
            if (end.getTimeInMillis() > start.getTimeInMillis()) {
                timeInterval = end.getTimeInMillis() - start.getTimeInMillis();
                Log.e("time interval is ",","+timeInterval);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeInterval;
    }

    private int getRequestCode() {
        Random r = new Random(System.currentTimeMillis());
        Log.e("Random num is ",""+r);
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }

    private boolean verifyDataInAllFields()
    {
        boolean status = true;
        mTitle.setError(null);
        mMessage.setError(null);
        mAddress.setError(null);
        if(mTitle.getText().toString().isEmpty()== true) {

            mTitle.setError("Choose a valid title");
            status = false;
        }
        else
            dTitle=mTitle.getText().toString();

        if(mMessage.getText().toString().isEmpty()== true) {
            mMessage.setError("Add a valid message here");
            status = false;
        }
        else
            dMessage=mMessage.getText().toString();

        if(mAddress.getText().toString().isEmpty()== true) {
            mAddress.setError("Address is invalid");
            status = false;
        }
        else
            dAddress=mAddress.getText().toString();


        return status;
    }

    private boolean isDateandTimeValid()
    {
        boolean status = true;

        mStartDate.setError(null);
        mEndDate.setError(null);
        mStartTime.setError(null);
        mEndTime.setError(null);
        

        if(mStartDate.getText().toString().isEmpty()== true) {

            mStartDate.setError("Choose a valid date");
            status = false;
        }
        else
            dStartDate=mStartDate.getText().toString();

        if(mEndDate.getText().toString().isEmpty()== true) {
            mEndDate.setError("Choose a valid date");
            status = false;
        }
        else
            dEndDate=mEndDate.getText().toString();

        if(mStartTime.getText().toString().isEmpty()== true) {
            mStartTime.setError("Address is invalid");
            status = false;
        }
        else
            dStarttime=mStartTime.getText().toString();

        if(mEndTime.getText().toString().isEmpty()== true) {
            mEndTime.setError("Address is invalid");
            status = false;
        }
        else
            dEndtime=mEndTime.getText().toString();

    if(status) {
        if (isDateValid()) {
            if (isDateSame()) {
                if (!isTimeValid()) {
                    mStartTime.setError("");
                    mEndTime.setError("");
                    status = false;
                }

            }

        } else {

            mStartDate.setError("");
            mEndDate.setError("");
            status = false;

        }
    }






        return status;
    }

    private boolean isDateValid(){
        if(startYear<=targetYear)
        {
            if(startMonth<=targetMonth){
                if(startday<=targetday)
                    return true;
                else
                    return false;
            }
            else
                return false;

        }
        else
            return false;
    }

    private boolean isDateSame(){
        if(startYear==targetYear)
        {
            if(startMonth==targetMonth){
                if(startday==targetday)
                    return true;
                else
                    return false;
            }
            else
                return false;

        }
        else
            return false;
    }

    private boolean isTimeValid(){
        if(startHour<=targetHour)
        {
            if(startHour==targetHour) {
                if (startMinute < targetminute)
                    return true;
                else
                    return false;
            }else
                return true;



        }
        else
            return false;
    }




    public void getsharedauthenticationdata()
    {
        SharedPreferences mPrefs = getSharedPreferences("AuthData",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(AUTHENTICATION, null);
        mAuthData = gson.fromJson(json, AuthData.class);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch(view.getId())
        {
            case R.id.startdate:
                mStartDate.setEnabled(false);
                DATE_DIALOG = 0;
                initdatepicker();
                break;
            case R.id.enddate:
                mEndDate.setEnabled(false);
                DATE_DIALOG = 1;
                initdatepicker();
                break;
            case R.id.starttime:
                mStartTime.setEnabled(false);
                TIME_DIALOG = 0;
                inittimepicker();
                break;
            case R.id.endtime:
                mEndTime.setEnabled(false);
                TIME_DIALOG = 1;
                inittimepicker();
                break;
        }
        return false;
    }


    private void initdatepicker()
    {
        try{
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if(DATE_DIALOG ==0) {

                    mStartDate.setEnabled(true);
                }
                else if(DATE_DIALOG ==1) {

                    mEndDate.setEnabled(true);
                }

            }
        });
        dpd.show(getFragmentManager(), "Datepickerdialog");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void inittimepicker()
    {
        try{
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );


        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
                if(TIME_DIALOG ==0) {

                    mStartTime.setEnabled(true);
                }
                else if(TIME_DIALOG ==1) {

                    mEndTime.setEnabled(true);
                }
            }
        });
        tpd.show(getFragmentManager(), "Timepickerdialog");

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String date = (monthOfYear+1)+" - "+dayOfMonth+" - "+year;
        if(DATE_DIALOG ==0) {
            startday = dayOfMonth;
            startMonth = monthOfYear+1;
            startYear = year;

            mStartDate.setText(date);
            mStartDate.setEnabled(true);
        }
        else if(DATE_DIALOG ==1) {
            targetday = dayOfMonth;
            targetMonth = monthOfYear+1;
            targetYear = year;

            mEndDate.setText(date);
            mEndDate.setEnabled(true);
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {


        if(TIME_DIALOG ==0) {

            startHour = hourOfDay;
            sendStartHour = hourOfDay;
            startMinute = minute;

            if(hourOfDay == 0)
                mStartTime.setText( 12 + ":" + ((minute < 10) ? ("0" + minute) : minute) + " " +  "AM" );
            else
                mStartTime.setText(((hourOfDay == 12) ? 12 : hourOfDay%12) + ":" + ((minute < 10) ? ("0" + minute) : minute) + " " + ((hourOfDay>=12) ? "PM" : "AM"));


            mStartTime.setEnabled(true);
        }
        else if(TIME_DIALOG ==1) {

            targetHour = hourOfDay;
            targetminute = minute;

            if(hourOfDay == 0)
                mEndTime.setText( 12 + ":" + ((minute < 10) ? ("0" + minute) : minute) + " " +  "AM" );
            else
                mEndTime.setText(((hourOfDay == 12) ? 12 : hourOfDay%12) + ":" + ((minute < 10) ? ("0" + minute) : minute) + " " + ((hourOfDay>=12) ? "PM" : "AM"));


            mEndTime.setEnabled(true);
        }



    }
}
