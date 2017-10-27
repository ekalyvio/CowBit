package com.kaliviotis.efthymios.cowsensor.serverapp;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Efthymios on 10/24/2017.
 */

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;// = FirebaseDatabase.getInstance();
    private FirebaseUser mCurrentUser;
    private String mClientID;

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mCurrentUser = null;
        mClientID = null;
    }

    public void Init() {
        mCurrentUser = mAuth.getCurrentUser();
/*        if (currentUser == null)
            return;*/
    }

    public boolean IsUserLogedIn() {
        return (mCurrentUser != null);
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            mCurrentUser = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            mCurrentUser = null;
                        }
                    }
                });
    }

    public void updateChildren(Map<String, Object> children) {
        mDatabase.getReference().updateChildren(children);
    }

    public void readClientID() {

        String path = String.format("/users/%s/clientID", mCurrentUser.getUid());

        DatabaseReference clientIDRef;
        clientIDRef = mDatabase.getReference(path);

        ValueEventListener clientIDRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                clientIDRef.removeEventListener(clientIDRefListener);
                mClientID = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mClientID = null;
//                clientIDRef.removeEventListener(clientIDRefListener);
            }
        };
        clientIDRef.addListenerForSingleValueEvent(clientIDRefListener);
    }

    public String getClientID() {
        return mClientID;
    }

}
