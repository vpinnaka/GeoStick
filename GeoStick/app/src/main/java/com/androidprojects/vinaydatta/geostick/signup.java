package com.androidprojects.vinaydatta.geostick;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class signup extends AppCompatActivity {


    private ProgressDialog mAuthProgressDialog;

    private AuthData mAuthData;

    private static final String TAG = signup.class.getSimpleName();
    private static final String AUTHENTICATION = "Authentication";
    private static final String USERS = "Users";
    private  static  final String USERS_NMAE = "username";
    private  static  final String USER_EMAIL = "email";

    private Firebase.AuthStateListener mAuthStateListener;

    private Firebase mFirebaseCLient;
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mUsername;
    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        mUsername = (EditText) findViewById(R.id.username);


        mRegisterButton = (Button) findViewById(R.id.sign_up_button);

        mFirebaseCLient = new Firebase(getResources().getString(R.string.firebaseURL));

        mAuthProgressDialog = new ProgressDialog(this);

        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }

    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            /* Hide all the login buttons */

            /* show a provider specific status text */
            String name = null;
            if (authData.getProvider().equals("password")) {
                name = authData.getUid();
            } else {
                Log.e(TAG, "Invalid provider: " + authData.getProvider());
            }


            if (name != null) {
                Log.i(TAG, "Logged in as " + name + " (" + authData.getProvider() + ")");
            }
        }
        else {
            Log.e(TAG, "authData null");
        }
        this.mAuthData = authData;

    }


    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.hide();
            Log.i(TAG, provider + " auth successful");
            setAuthenticatedUser(authData);

            Firebase userdata = mFirebaseCLient.child(USERS).child(mAuthData.getUid());

            userdetails users= new userdetails(mUsername.getText().toString(),mEmailView.getText().toString(),0,0);

            userdata.setValue(users);
            //shareuserdata(users);

            shareAuthunticationdata(authData);

            startActivity(new Intent(getApplicationContext(), homescreen.class));
            finish();
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.hide();
            showErrorDialog(firebaseError.toString());
        }
    }

    private void shareuserdata(userdetails userdata)
    {
        SharedPreferences mPrefs = getSharedPreferences(USERS,MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userdata);
        prefsEditor.putString(USERS_NMAE, json);
        prefsEditor.commit();
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


    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(email,"Register");
            registerWithPassword(email,password);
        }
    }

    private void showProgress(String user,CharSequence title)
    {

        mAuthProgressDialog.setTitle(title);
        mAuthProgressDialog.setMessage("Creating account for " + user);
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public void registerWithPassword(final String email, final String password) {


        mFirebaseCLient.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {

                mAuthProgressDialog.hide();
                Log.i(TAG, stringObjectMap.toString());
                showProgress(email,"Login");
                loginWithPassword(email, password);

            }

            @Override
            public void onError(FirebaseError firebaseError) {
                mAuthProgressDialog.hide();
                showErrorDialog(firebaseError.toString());
            }
        });
    }

    private void loginWithPassword(String email, String pwd) {

        mFirebaseCLient.authWithPassword(email, pwd, new AuthResultHandler("password"));
    }


    public void loginScreen(View view)
    {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }


}
