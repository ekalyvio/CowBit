package com.kaliviotis.efthymios.cowsensor.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CowListingActivity extends AppCompatActivity {
    private static final String TAG = "CowListingActivity";

    public static final String Message_CowID = "com.kaliviotis.efthymios.CowID";
    public static final String Message_CowName = "com.kaliviotis.efthymios.CowName";

    DatabaseReference clientCowsRef;
    ChildEventListener clientCowsEventListener;
    ListView cowsListView;
    ArrayList<CowListItem> cowList;
    ArrayAdapter<CowListItem> cowListAdapter;

    private class CowListItem {
        public String Id;
        public String Name;

        public CowListItem(String id, String name) {
            Id = id;
            Name = name;
        }

        public String toString(){
            if (Name.isEmpty())
                return Id;
            return Name;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_listing);

        cowsListView = findViewById(R.id.cowsListView);

        cowList = new ArrayList<CowListItem>();

        cowListAdapter = new ArrayAdapter<CowListItem>(this, android.R.layout.simple_list_item_1, cowList);
        cowsListView.setAdapter(cowListAdapter);

        cowsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        cowsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final CowListItem item = (CowListItem)parent.getItemAtPosition(position);
                Intent intent = new Intent(CowListingActivity.this, CowDetailsActivity.class);
                intent.putExtra(Message_CowID, item.Id);
                intent.putExtra(Message_CowName, item.Name);
                startActivity(intent);
            }

        });

        initEventListener();
    }

    public void signOutButtonClick(View view) {
        FirebaseHelper.getInstance().getAuth().signOut();
        this.finish();
    }

    private class StableArrayAdapter extends ArrayAdapter<CowListItem> {

//        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<CowListItem> objects) {
            super(context, textViewResourceId, objects);
/*            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }*/
        }

/*        @Override
        public long getItemId(int position) {
            CowListItem item = getItem(position);
            return item.Id
//            return mIdMap.get(item);
        }*/

        @Override
        public boolean hasStableIds() {
            return false;
        }

/*        @Override
        public CowListItem getItem(int position) {
            CowListItem item = super.getItem(position);
            return item;
        }*/
    }

    private void initEventListener() {
        clientCowsEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String cowId = dataSnapshot.getKey();
                String name = "";
                if (dataSnapshot.child("name").exists())
                    name = dataSnapshot.child("name").getValue().toString();
                cowList.add(new CowListItem(cowId, name));
                cowListAdapter.notifyDataSetChanged();

                Log.d(TAG, cowId + " - " + name + " - " + s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                String name = "";
                if (dataSnapshot.child("name").exists())
                    name = dataSnapshot.child("name").getValue().toString();
                for(int i=0; i<cowList.size(); ++i) {
                    if (cowList.get(i).Id.equals(key)) {
                        cowList.get(i).Name = name;
                        cowListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for(int i=0; i<cowList.size(); ++i) {
                    if (cowList.get(i).Id.equals(key)) {
                        cowList.remove(i);
                        cowListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        cowList.clear();

        String path = String.format("/clients/%s/cows", FirebaseHelper.getInstance().getClientID());
        clientCowsRef = FirebaseHelper.getInstance().getDatabase().getReference(path);

        clientCowsRef.addChildEventListener(clientCowsEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        clientCowsRef.removeEventListener(clientCowsEventListener);
    }
}
