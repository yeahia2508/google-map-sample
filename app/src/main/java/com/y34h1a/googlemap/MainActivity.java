package com.y34h1a.googlemap;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    public static final double SEATTLE_LAT = 47.60621,
            SEATTLE_LNG = -122.33207,
            SYDNEY_LAT = -33.867487,
            SYDNEY_LNG = 151.20699,
            NEWYORK_LAT = 40.714353,
            NEWYORK_LNG = -74.005973;

    public static final float CAMERA_ZOOM = 15;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(servicesOk()){
            setContentView(R.layout.activity_map);

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
        }
        else{
            setContentView(R.layout.activity_main);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

    }

    public boolean servicesOk(){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int isAvailable = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }
        else if(googleApiAvailability.isUserResolvableError(isAvailable)){
            Dialog dialog = googleApiAvailability.getErrorDialog(this,isAvailable,GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Can't connect to google play services",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

    }

    public void goToLocation(double lat, double lng, float cameraZoom){
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, cameraZoom);
        mMap.moveCamera(cameraUpdate);
    }

    public void geoLocate(View v) throws IOException {
        hideSoftKeyboard(v);
        EditText et = (EditText) findViewById(R.id.etLocationSearch);
        String location  = et.getText().toString();
        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        if(list.size() > 0){
            Address add = list.get(0);
            if(add.hasLatitude()){
                String locality = add.getLocality();
                Toast.makeText(this,locality,Toast.LENGTH_SHORT).show();

                double lat = add.getLatitude();
                double lng  = add.getLongitude();

                goToLocation(lat,lng,CAMERA_ZOOM);
            }

        }
        else{
            Toast.makeText(getApplicationContext(),"Place Not found",Toast.LENGTH_SHORT).show();
        }

    }

    public void hideSoftKeyboard(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MapStateManager mgr = new MapStateManager(this);
        mgr.saveMapState(mMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MapStateManager mgr = new MapStateManager(this);
        CameraPosition position = mgr.getSavedCameraPosition();
        if(position!=null && mMap!= null){
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            mMap.moveCamera(update);
        }
    }
}
