package com.androidprojects.vinaydatta.geostick;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class homescreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,OnMyLocationButtonClickListener,
        GoogleMap.OnMapLongClickListener,GoogleMap.OnInfoWindowClickListener {


    private Firebase mFirebaseRef;

    /* Data from the authenticated user */
    private AuthData mAuthData;

    private String uniqueid;

    private GoogleMap mMap;

    private static final String TAG = homescreen.class.getSimpleName();

    private static CameraPosition MyLocation = null;

    private Location mLastLocation;

    private static LatLng newGeostickLocation;

    private Marker markerLoaction;
    List <Marker> allfriendslocation;

    private Marker oldmarker;

    List<Address> address;

    List<dataFirebase> allGeosticks;
    List<dataFirebase> IGeosticks;
    List<dataFirebase> YouGeosticks;
    List<dataFirebase> WeGeosticks;



    private String locationAddress;

    private String modeofsharing;

    private FloatingActionButton newgeostick;

    private TextView username,useremail;
    private String datausername = "username";
    private String datauseremail = "email";

    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private static final String UNIQUE_ID = "UniqueId";
    private static final String AUTHENTICATION = "Authentication";
    private  static  final String ADDRESS = "Address";
    private  static  final String LATITUDE = "Latitude";
    private  static  final String LONGITUDE = "Longitude";
    private  static  final String MODE = "Mode";
    private  static  final String PUBLIC = "Public";
    private  static  final String SHARED = "Shared";
    private  static  final String ONLYME = "Onlyme";

    private  static  final String OPERATION = "Operation";
    private  static  final String ADD_NEW = "Addnew";
    private  static  final String VIEW_OLD = "View";

    private  static  final String USERS = "Users";
    private  static  final String USERS_NMAE = "username";
    private  static  final String USER_EMAIL = "email";


    private  static  final String VIEW_DATA = "View_Data";

    private  static  final int PINK_MARKER = R.drawable.pinkmarker;
    private  static  final int GREEN_MARKER = R.drawable.greenmarker;
    private  static  final int BLUE_MARKER = R.drawable.bluemarker;

    private boolean firstclick =true;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        mFirebaseRef = new Firebase(getResources().getString(R.string.firebaseURL));





        allGeosticks = new ArrayList<dataFirebase>();
        IGeosticks = new ArrayList<dataFirebase>();
        YouGeosticks = new ArrayList<dataFirebase>();
        WeGeosticks = new ArrayList<dataFirebase>();

        allfriendslocation = new ArrayList<Marker>();





        newgeostick = (FloatingActionButton) findViewById(R.id.fab);
        uniqueid = new String();
        newgeostick.setOnClickListener(newGeostickListner);

        getsharedauthenticationdata();


        if(mAuthData!=null)
            uniqueid = mAuthData.getUid();





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        useremail = (TextView)header.findViewById(R.id.useremail);
        username = (TextView) header.findViewById(R.id.user);

        /*setuserdetails();

        Log.i("Username",datausername);
        Log.i("Useremail",datauseremail);

        username.setText(datausername);
        useremail.setText(datauseremail);*/

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if (client == null) {
            client = new GoogleApiClient.Builder(this).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).
                    addApi(LocationServices.API).build();

        }






    }

    @Override
    protected void onResume() {
        super.onResume();

        modeofsharing = ONLYME;



        refreshdata();
    }


    private void setuserdetails(){

        SharedPreferences mPrefs = getSharedPreferences(USERS,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(USERS_NMAE, null);
        userdetails user = gson.fromJson(json, userdetails.class);

        datauseremail=(user.getemail());
        datausername =(user.getusername());

    }



    private void getallMarkersforIgeostick(){

        final Firebase dataref = new Firebase(getResources().getString(R.string.firebaseURL)+mAuthData.getUid());

        dataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("I Childern count",""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.i("data value",""+postSnapshot.getValue());

                    dataFirebase data = postSnapshot.getValue(dataFirebase.class);

                    Log.i("data value",""+data.gettitle());

                    IGeosticks.add(data);
                    allGeosticks.add(data);

                    addnewgeostickmarker(data.gettitle(),new LatLng(data.getlatitude(),data.getlongitude()),PINK_MARKER);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

                Log.i("error",firebaseError.toString());


            }
        });


    }


    /*private void getallMarkersforYOUgeostick(){

        final Firebase dataref = new Firebase(getResources().getString(R.string.firebaseURL)+mAuthData.getUid());

        dataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(" You Childern count",""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.i("data value",""+postSnapshot.getValue());

                    dataFirebase data = postSnapshot.getValue(dataFirebase.class);

                    Log.i("data value",""+data.gettitle());

                    YouGeosticks.add(data);
                    allGeosticks.add(data);

                    addnewgeostickmarker(data.gettitle(),new LatLng(data.getlatitude(),data.getlongitude()),GREEN_MARKER);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

                Log.i("error",firebaseError.toString());


            }
        });
    }*/


    private void getallMarkersforWEgeostick(){

        final Firebase dataref = new Firebase(getResources().getString(R.string.firebaseURL)+PUBLIC);

        dataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("We Childern count",""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.i("data value",""+postSnapshot.getValue());

                    dataFirebase data = postSnapshot.getValue(dataFirebase.class);

                    Log.i("data value",""+data.gettitle());

                    WeGeosticks.add(data);
                    allGeosticks.add(data);

                    addnewgeostickmarker(data.gettitle(),new LatLng(data.getlatitude(),data.getlongitude()),GREEN_MARKER);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

                Log.i("error",firebaseError.toString());


            }
        });
    }





    public void getsharedauthenticationdata()
    {
        SharedPreferences mPrefs = getSharedPreferences("AuthData",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(AUTHENTICATION, null);
        mAuthData = gson.fromJson(json, AuthData.class);
    }

    public final FloatingActionButton.OnClickListener newGeostickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            addNewMessage();
        }
    };


    private void addNewMessage()
    {
        getAddressofLocation();
        Intent intent = new Intent(getBaseContext(), add_message.class);

        intent.putExtra(OPERATION,ADD_NEW);
        intent.putExtra(MODE,modeofsharing);
        intent.putExtra(ADDRESS,locationAddress);
        intent.putExtra(LATITUDE,newGeostickLocation.latitude);
        intent.putExtra(LONGITUDE,newGeostickLocation.longitude);
        startActivity(intent);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {


            super.onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homescreen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh_data) {
            refreshdata();
            return true;
        }
        else if(id == R.id.action_settings)
        {
            settings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshdata(){

       // sendmylocation
        getallMarkersforIgeostick();
        // getallMarkersforYOUgeostick();
        getallMarkersforWEgeostick();
    }

    private void settings(){

        startActivity(new Intent(getApplicationContext(),Settings.class));
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_Activity:
                addNewMessage();
                break;
            case R.id.nav_igeostick: {

                modeofsharing = ONLYME;
                mMap.clear();
                getallMarkersforIgeostick();

            }
                break;
            case R.id.nav_yougeostick:{

                modeofsharing = SHARED;
                mMap.clear();
                //getallMarkersforYOUgeostick();
            }
                break;
            case R.id.nav_wegeostick:{

                modeofsharing = PUBLIC;
                mMap.clear();
                getallMarkersforWEgeostick();
            }

                break;
            case R.id.nav_share:
                Toast.makeText(homescreen.this, "Function yet to be defined", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_Help:
                Toast.makeText(homescreen.this, "Function yet to be defined", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:{

                logout();
                Log.i(TAG, "Firebase Logout");
            }

                break;
            default:
                Log.i(TAG, "Invalid nave item");
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void logout() {

        mFirebaseRef.unauth();
        this.mAuthData = null;
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
        // }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        enableMyLocation();



    }


    private void enableMyLocation() {
        if (mMap != null) {
            // Access to the location has been granted to the app.
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
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }


    @Override
    public void onStart() {
        client.connect();
        super.onStart();

    }

    @Override
    public void onStop() {
        client.disconnect();
        super.onStop();

    }

    @Override
    public void onConnected(Bundle bundle) {

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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);

        newGeostickLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        if(mLastLocation != null) {
            MyLocation = new CameraPosition.Builder()
                                           .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                                           .zoom(17).bearing(0).tilt(60)
                                           .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(MyLocation));
        }
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Exception while resolving connection error.", e);
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Log.e(TAG, "Connection to Google Play services failed with error code " + errorCode);
        }

    }

    public void getAddressofLocation()
    {
        try {
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            address = gcd.getFromLocation(newGeostickLocation.latitude, newGeostickLocation.longitude, 5);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //(newGeostickLocation.latitude,newGeostickLocation.longitude,);
        locationAddress = address.get(0).getAddressLine(0)+","+address.get(0).getAddressLine(1)+","+address.get(0).getAddressLine(2);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {




        int markercolor = 0;

        if(modeofsharing.equals(PUBLIC))
            markercolor = GREEN_MARKER;
        else if(modeofsharing.equals(SHARED))
            markercolor = BLUE_MARKER;
        else if(modeofsharing.equals(ONLYME))
            markercolor = PINK_MARKER;

        if(oldmarker!=null){

            oldmarker.remove();
        }

        newGeostickLocation = latLng;
        getAddressofLocation();
        oldmarker = addnewgeostickmarker(locationAddress,newGeostickLocation,markercolor);

    }



    private Marker addnewgeostickmarker(String title,LatLng position,int markerid)
    {

        markerLoaction = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(title)
                            .icon(BitmapDescriptorFactory.fromResource(markerid)));

        return markerLoaction;
    }


    private void view_message(dataFirebase markerdata,String modeofsharing){

        Intent view_message = new Intent(getApplicationContext(),add_message.class);

        view_message.putExtra(OPERATION,VIEW_OLD);
        view_message.putExtra(MODE,modeofsharing);
        view_message.putExtra(VIEW_DATA,markerdata);




        startActivity(view_message);



    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        LatLng position = marker.getPosition();

        Log.i("Info","I am in Info");

        for(dataFirebase markerdata:IGeosticks){

            if(position.latitude == markerdata.getlatitude() && position.longitude == markerdata.getlongitude())
            {
                view_message(markerdata,ONLYME);
                break;
            }
        }

        for(dataFirebase markerdata:WeGeosticks){

            if(position.latitude == markerdata.getlatitude() && position.longitude == markerdata.getlongitude())
            {
                view_message(markerdata,PUBLIC);
                break;
            }
        }

    }
}
