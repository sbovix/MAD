package com.example.silviabova.mylogin;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.jar.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private CameraPosition mCameraPosition;

    //The entry point to Places API
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    //The entry point to the Fused Location Provider
    private FusedLocationProviderClient fusedLocationProviderClient;

    //Default Location(Sydney) and default zoom
    private final LatLng mDefaultLocation = new LatLng(-33.85523341, 151.2106085);
    private static final int DEFAULT_ZOOM= 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;
    private boolean mLocationPErmissionGranted;

    private Location mLastKnownLocation=null;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION= "location";

    private static final int M_MAX_ENTRIES=5;
    private String[] mLikelyPlaeceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    private LocationManager locationManager;
    private Criteria criteria;
    private String bestProvider;

    private double lat, lng;
    private FirebaseAuth mAuth;
    private FirebaseDatabase fb;
    private String isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null){
            mLastKnownLocation= savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition= savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);

        isbn=getIntent().getStringExtra("ISBN");

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient=Places.getPlaceDetectionClient(this, null);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        mAuth = FirebaseAuth.getInstance();
        fb = FirebaseDatabase.getInstance();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //elimina la barra sopra
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mMap!=null){
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, (FrameLayout) findViewById(R.id.map), false);
                TextView title =((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());
                TextView snippet =((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());
                return infoWindow;
            }
        });

        getLocationPermission();

        updateLocationUI();

        getDeviceLocation();

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void getDeviceLocation(){
        try{
            if(mLocationPErmissionGranted){
                final Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful() && task.getResult()!=null){
                            mLastKnownLocation= task.getResult();
                            LatLng ll = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, DEFAULT_ZOOM));
                            lat=mLastKnownLocation.getLatitude();
                            lng=mLastKnownLocation.getLongitude();
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference myRefLat = fb.getReference("/"+user.getUid()+"/Books/"+isbn+"/latitude");
                            DatabaseReference myRefLng = fb.getReference("/"+user.getUid()+"/Books/"+isbn+"/longitude");
                            myRefLat.setValue(lat);
                            myRefLng.setValue(lng);
                        }else{
                            Log.d(TAG, "Current location is null.");
                            Log.e(TAG, "Exception :%s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("Exception : %s", e.getMessage());
        }
    }

    private void getLocationPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mLocationPErmissionGranted=true;
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         mLocationPErmissionGranted=false;
        switch (requestCode){
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocationPErmissionGranted=true;
                }
            }
        }
        updateLocationUI();
    }

    private void showCurrentPlace(){
        if (mMap == null){
            return;
        }
        if(mLocationPErmissionGranted){
            @SuppressLint("MissingPermission") final Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    if(task.isSuccessful() && task.getResult() !=null){
                        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                        int count;
                        if(likelyPlaces.getCount()<M_MAX_ENTRIES){
                            count= likelyPlaces.getCount();
                        }else{
                            count=M_MAX_ENTRIES;
                        }

                        int i=0;
                        mLikelyPlaeceNames = new String[count];
                        mLikelyPlaceAddresses = new String[count];
                        mLikelyPlaceAttributions = new String[count];
                        mLikelyPlaceLatLngs = new LatLng[count];

                        for (PlaceLikelihood placeLikelihoods : likelyPlaces){
                            mLikelyPlaeceNames[i]=(String)placeLikelihoods.getPlace().getName();
                            mLikelyPlaceAddresses[i]=(String) placeLikelihoods.getPlace().getAddress();
                            mLikelyPlaceAttributions[i]=(String) placeLikelihoods.getPlace().getAttributions();
                            mLikelyPlaceLatLngs[i]= placeLikelihoods.getPlace().getLatLng();
                            i++;
                            if(i >(count-1)){
                                break;
                            }
                        }

                        likelyPlaces.release();

                        openPlacesDialog();
                    }else{
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                }
            });
        }else {
            Log.i(TAG, "The user didn't grant location permission.");

            mMap.addMarker(new MarkerOptions().title(getString(R.string.default_info_title)).position(mDefaultLocation).snippet(getString(R.string.default_info_snippet)));

            getLocationPermission();
        }
    }

    private void openPlacesDialog(){
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LatLng markerLatLng = mLikelyPlaceLatLngs[i];
                String markerSnippet = mLikelyPlaceAddresses[i];
                if(mLikelyPlaceAttributions[i]!=null){
                    markerSnippet = markerSnippet +"\n"+mLikelyPlaceAttributions[i];
                }
                mMap.addMarker(new MarkerOptions().title(mLikelyPlaeceNames[i]).position(markerLatLng).snippet(markerSnippet));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng, DEFAULT_ZOOM));
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.pick_place).setItems(mLikelyPlaeceNames, listener).show();
    }

    private void updateLocationUI(){
        if(mMap==null){
            return;
        }
        try{
            if(mLocationPErmissionGranted){
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }else{
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation=null;
                getLocationPermission();
            }
        }catch (SecurityException e){
            Log.e("Eception: %s", e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.mybutton){
            finish();
            Intent i = new Intent(MapsActivity.this, HomeActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
