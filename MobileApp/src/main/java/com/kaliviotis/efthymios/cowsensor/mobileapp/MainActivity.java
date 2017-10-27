package com.kaliviotis.efthymios.cowsensor.mobileapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseHelper.getInstance().getAuth().getCurrentUser() != null) {
            CheckUser();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Globals.getInstance().setContext(getApplicationContext());
    }

    private void CheckUser() {
        String path = String.format("/users/%s/", FirebaseHelper.getInstance().getAuth().getCurrentUser().getUid());

        DatabaseReference clientIDRef;
        clientIDRef = FirebaseHelper.getInstance().getDatabase().getReference(path);

        ValueEventListener clientIDRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String clientID = (String)dataSnapshot.child("clientID").getValue();
                if ((dataSnapshot.child("mobileAppUser").exists()) && (dataSnapshot.child("mobileAppUser").getValue().toString().toLowerCase().equals("true"))) {
                    FirebaseHelper.getInstance().setClientID(clientID);
                    Intent intent = new Intent(MainActivity.this, CowListingActivity.class);
                    startActivity(intent);
                } else {
                    FirebaseHelper.getInstance().setClientID(null);
                    FirebaseHelper.getInstance().getAuth().signOut();
                    AppDialogs.DisplayMessage(MainActivity.this, getString(R.string.error_text), getString(R.string.no_use_permission));
                }
                //dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseHelper.getInstance().setClientID(null);
                AppDialogs.DisplayMessage(MainActivity.this, getString(R.string.error_text), getString(R.string.login_problem));
//                clientIDRef.removeEventListener(clientIDRefListener);
            }
        };
        clientIDRef.addListenerForSingleValueEvent(clientIDRefListener);
    }

    public void loginButtonClick(View view) {
        String email;
        String password;

        EditText emailView = findViewById(R.id.emailText);
        EditText passwordView = findViewById(R.id.passwordText);

        email = emailView.getText().toString();
        password = passwordView.getText().toString();

        FirebaseHelper.getInstance().getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseHelper.getInstance().setFirebaseUser(FirebaseHelper.getInstance().getAuth().getCurrentUser());
                            CheckUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            AppDialogs.DisplayMessage(MainActivity.this, getString(R.string.error_text), getString(R.string.wrong_username_message));
//                            FirebaseHelper.getInstance().setFirebaseUser(null);
                        }
                    }
                });
    }
}
