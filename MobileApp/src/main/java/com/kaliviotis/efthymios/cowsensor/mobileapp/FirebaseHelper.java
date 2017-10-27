package com.kaliviotis.efthymios.cowsensor.mobileapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Efthymios on 10/26/2017.
 */

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static FirebaseHelper instance = null;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    //private FirebaseUser mCurrentUser;
    private String mClientID;

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
//        mCurrentUser = null;
        mClientID = null;
    }

    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseDatabase getDatabase() {
        return mDatabase;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

/*    public void setFirebaseUser(FirebaseUser user) {
        mCurrentUser = user;
    }

    public FirebaseUser getFirebaseUser() {
        return mCurrentUser;
    }*/

    public void setClientID(String clientID) {
        mClientID = clientID;
    }

    public String getClientID() {
        return mClientID;
    }
}
