package com.example.mini.stupro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;


public class MainPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    FirebaseAuth fauth;
    String user_id;
    String user_name;
    String user_area;
    DatabaseReference reference;
    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    private LocationRequest request;
    LatLng latLng,summaLatLong;
    Location lastLocation;
    private boolean logedout = false;
    private LatLng preLocation;
    private boolean reqcan=false;
    private Marker pickMarkerRem;
    private boolean srefir=true;
    //TextView t1,t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        NavListen();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Sending Request......", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if(reqcan)
                {
                    reqcan = false;
                    geoQuery.removeAllListeners();
                    if(!srefir) {
                        databaseLocaRef.removeEventListener(databaseLocaRefList);
                        srefir =false;
                    }

                    if(mechId!=null)
                    {
                        DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("Mechanic").child(mechId).child("custAllocId");
                        mechRef.removeValue();
                        mechId=null;
                    }
                    mechThere=false;
                    String userid = fauth.getInstance().getCurrentUser().getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("RequestedCustomers");
                    GeoFire geo = new GeoFire(reference);
                    geo.removeLocation(userid);

                    if(pickMarkerRem !=null && mechMark!=null)
                    {
                        pickMarkerRem.remove();
                        mechMark.remove();
                    }
                    Toast.makeText(MainPageActivity.this, "Request Cancelled.....", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    reqcan = true;
                    String userid = fauth.getInstance().getCurrentUser().getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("RequestedCustomers");
                    GeoFire geo = new GeoFire(reference);
                    geo.setLocation(userid, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));

                    preLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    Toast.makeText(MainPageActivity.this, "Searching for Mechanic", Toast.LENGTH_SHORT).show();
                    MarkerOptions options = new MarkerOptions();
                    options.position(preLocation);
                    options.title("You");
                    pickMarkerRem=mMap.addMarker(options);
                    getCloseMech();
                }

            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        /*if (user == null) {
            Intent intent = new Intent(MainPageActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            user_id = user.getUid();
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String area = dataSnapshot.child("area").getValue(String.class);
                    TextView t1 =   findViewById(R.id.name_tv);
                    TextView t2 =   findViewById(R.id.area_tv);
                    t1.setText(name);
                    t2.setText(area);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }*/

    }

    //get first instance of the mechanic.
    private int range = 1;
    private boolean mechThere = false;
    private String mechId;
    GeoQuery geoQuery;
    private void getCloseMech() {

        DatabaseReference mechLocation = FirebaseDatabase.getInstance().getReference().child("AvailableMechanic");
        GeoFire geoFire = new GeoFire(mechLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(preLocation.latitude, preLocation.longitude), range);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!mechThere && reqcan) {
                    //&& range<3 (condition)
                    mechThere = true;
                    mechId = key;
                    //Log.d("TAG",mechId);
                    DatabaseReference mechRef = FirebaseDatabase.getInstance().getReference().child("Mechanic").child(mechId);
                    String bookCustId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap hashMap = new HashMap();
                    hashMap.put("custAllocId", bookCustId);
                    mechRef.updateChildren(hashMap);

                    getMechPos();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!mechThere) {
                    range++;
                    getCloseMech();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private Marker mechMark;
    private DatabaseReference databaseLocaRef;
    private ValueEventListener databaseLocaRefList;
    private void getMechPos() {
        databaseLocaRef = FirebaseDatabase.getInstance().getReference().child("BookedMechanics").child(mechId).child("l");
        databaseLocaRefList = databaseLocaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && reqcan) {
                    List<Object> mechmap = (List<Object>) dataSnapshot.getValue();
                    double mechLat = 0;
                    double mechLong = 0;
                    if (mechmap.get(0) != null) {
                        mechLat = Double.parseDouble(mechmap.get(0).toString());
                    }
                    if (mechmap.get(1) != null) {
                        mechLong = Double.parseDouble(mechmap.get(1).toString());
                    }
                    LatLng mechAvai = new LatLng(mechLat, mechLong);
                    if (mechMark != null) {
                        mechMark.remove();
                    }

                    Location loc1 = new Location("");
                    loc1.setLatitude(preLocation.latitude);
                    loc1.setLatitude(preLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(mechAvai.latitude);
                    loc2.setLatitude(mechAvai.longitude);

                    float disbw = loc1.distanceTo(loc2);
                    if(disbw<100)
                    {
                        Toast.makeText(MainPageActivity.this, "Mechanic is Here", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Toast.makeText(MainPageActivity.this, "Mechanic is at " + String.valueOf(disbw) + "m from you.", Toast.LENGTH_SHORT).show();
                    }

                    MarkerOptions op = new MarkerOptions();
                    op.position(mechAvai);
                    op.title("Mechanic");
                    mechMark=mMap.addMarker(op);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void NavListen() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {

            googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

        } catch (Resources.NotFoundException e) {
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        mMap.setMyLocationEnabled(true);


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





    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);
        request.setFastestInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);

        lastLocation=location;
        latLng = new LatLng(location.getLatitude(),location.getLongitude());

        if(summaLatLong==null)
        {
            summa();
            summaLatLong=latLng;
        }
    }
    public void summa()
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }
    @Override
    protected void onStop() {
        super.onStop();

    }
    protected  void removeGeo()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BookedCustomers");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.nav_send:
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null)
                {
                    logedout=true;
                    removeGeo();
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(MainPageActivity.this,login.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(this, "USER ALREADY LOGGED OUT", Toast.LENGTH_SHORT).show();
                }
        }
        return true;
    }
}
