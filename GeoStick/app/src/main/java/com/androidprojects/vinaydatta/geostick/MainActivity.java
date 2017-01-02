package com.androidprojects.vinaydatta.geostick;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private Button mLoginButton;
    private Button mRegisterButton;

    private ProgressDialog mAuthProgressDialog;

    /* A reference to the Firebase */
    private Firebase mFirebaseRef;

    /* Data from the authenticated user */
    private AuthData mAuthData;

    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String AUTHENTICATION = "Authentication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginButton = (Button) findViewById(R.id.buttonLogin);
        mRegisterButton = (Button) findViewById(R.id.buttonRegister);
        mAuthProgressDialog = new ProgressDialog(this);
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebaseURL));

        showProgress("Loading","Authenticating User...");
        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {

                setAuthenticatedUser(authData);
            }
        };

        mFirebaseRef.addAuthStateListener(mAuthStateListener);



    }

    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            /* Hide all the login buttons */
            mLoginButton.setVisibility(View.GONE);
            mRegisterButton.setVisibility(View.GONE);

            /* show a provider specific status text */
            String uid = null;
            if (authData.getProvider().equals("password")) {
                uid = authData.getUid();
            } else {
                Log.e(TAG, "Invalid provider: " + authData.getProvider());
            }


            if (uid != null) {
                Log.i(TAG, "Logged in as " + uid + " (" + authData.getProvider() + ")");
                mAuthProgressDialog.hide();

                homeScreen(authData);
            }


        } else {
            mAuthProgressDialog.hide();
            /* No authenticated user show all the login buttons */
            mLoginButton.setVisibility(View.VISIBLE);
            mRegisterButton.setVisibility(View.VISIBLE);

        }
        this.mAuthData = authData;

    }

    private void showProgress(String user,CharSequence title)
    {

        mAuthProgressDialog.setTitle(title);
        mAuthProgressDialog.setMessage("Creating account for " + user);
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();
    }

    public void loginScreen(View view)
    {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }
    public void registerScreen(View view)
    {
        startActivity(new Intent(getApplicationContext(),signup.class));
        finish();
    }

    private void homeScreen(AuthData authData)
    {

        shareAuthunticationdata(authData);
        startActivity(new Intent(getApplicationContext(),homescreen.class));
        finish();
    }

    private void shareAuthunticationdata(AuthData authData)
    {
        SharedPreferences mPrefs = getSharedPreferences("AuthData",MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(authData);
        prefsEditor.putString(AUTHENTICATION, json);
        prefsEditor.commit();
    }




}
