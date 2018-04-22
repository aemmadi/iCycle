package com.coppellcoders.icycle;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBuffer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by rkark on 4/21/2018.
 */

public class HomeActivity extends Activity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private EditText X;
    private EditText Y;
    private Button find;
    private Button logOut;
    private double xPos;
    private double yPos;
    private RecyclerView recyclerView;
    private ArrayList<Place> places;
    private PlaceAdapter pAdapter;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        TextView txt = findViewById(R.id.nametxt);

        Intent intent = getIntent();
        String name = intent.getExtras().getString("name");

        if(name!=null){

txt.setText("Recyclable Centers for " + name.substring(0,1).toUpperCase()+name.substring(1));

        }else{

            txt.setText("Recyclable Centers");

        }

/*
        logOut = findViewById(R.id.logout);
        X = findViewById(R.id.X);
        Y = findViewById(R.id.Y);
*/

        places = new ArrayList<>();

        recyclerView = findViewById(R.id.list);
      //  find = findViewById(R.id.find);
//        find.setOnClickListener(this);
      //  logOut = findViewById(R.id.logout);
     //   logOut.setOnClickListener(this);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Place cur = places.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr="+xPos+","+yPos+"&daddr="+cur.x+","+cur.y+""));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        getUserLocation();
        getLocations();

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
        /*    case R.id.logout:
                Toast.makeText(getApplicationContext(), "Nig", Toast.LENGTH_LONG).show();
                mAuth.signOut();
                Intent i = new Intent(this, SignUpActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.find:
                if(X.getText().toString().equals("") && Y.getText().toString().equals("")){
                    getUserLocation();
                }else{
                    xPos = Double.parseDouble(X.getText().toString());
                    yPos = Double.parseDouble(Y.getText().toString());
                }
                Log.v("X-Coordinate", xPos+"");
                Log.v("Y-Coordinate", yPos+"");
                getLocations();
                break;*/
        }
    }
    public void getUserLocation(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(this, "Please Grant the Location Permission", Toast.LENGTH_SHORT).show();
        }else {
            Location lastKnown = locationManager.getLastKnownLocation(locationProvider);
            xPos = lastKnown.getLatitude();
            yPos = lastKnown.getLongitude();
        }
    }
    public void getLocations() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Fetching locations..");
        dialog.show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("locations");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap value = (HashMap) dataSnapshot.getValue();
                JSONObject data = new JSONObject(value);
                try {
                    JSONArray locs = data.getJSONArray("most_loc");
                    processData(locs);
                    Collections.sort(places);
                    System.out.println(places);
                    initRec();
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Data", "Got Data" + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("Data", "Failed to Get Data");
            }
        });
    }
    public void processData(JSONArray vals) throws JSONException {
        for(int i = 0; i < vals.length(); i++){
            JSONObject cur = (JSONObject) vals.get(i);
            double x = Double.parseDouble(cur.get("x").toString());
            double y = Double.parseDouble(cur.get("y").toString());
            String name = cur.get("name").toString();
            Place temp = new Place(y, x, 0, name);
            temp.findDistance(xPos, yPos);
            places.add(temp);

        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Logged In
        if(currentUser == null) {
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
            finish();
        }
    }
    public void initRec(){
        pAdapter = new PlaceAdapter(places);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pAdapter);
        recyclerView.addItemDecoration(new SpacingItemDecoration(this, 16));
    }


}

