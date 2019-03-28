package com.example.maplocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    /*
    The Fused Location API is a higher-level Google Play Services API
    that wraps the underlying location sensors like GPS. You can accomplish tasks like:
                Register for location connection events
                Connect to the location sensor
                Register for updates or accuracy changes
                Get last location
   */
    public FusedLocationProviderClient ProviderClient;
//widgest
    private EditText edtSearch;
MarkerOptions markerOptions= new MarkerOptions();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        edtSearch=findViewById(R.id.input_search);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //check if permission is granted or not
        getLocationPermission();

    }
    private void init(){
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH || actionId== EditorInfo.IME_ACTION_DONE
                        || event.getAction()== event.ACTION_DOWN
                        || event.getAction()== event.KEYCODE_ENTER){
                        geoLocate();
                }
                return false;
            }
        });
    }

    private void geoLocate() {
        String searchString=edtSearch.getText().toString().trim();
        Geocoder geocoder=new Geocoder(this);
        List<Address> addressList=new ArrayList<>();
        try {
            addressList=geocoder.getFromLocationName(searchString,6);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList.size()>0){
            Address address=addressList.get(0);
            LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
            markerOptions.position(latLng);
            markerOptions.title("User Location");
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,12.5f));
        }

    }

    //check location permission
    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            //if permission is not granted then request it
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1001);
        }
        else{
            // if permission is granted then call getCurrentLocation()
               // getCurrentLocation();
            init();
        }
    }

//handling the request permission using onRequestPermissionsResult()
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //check is request code
        if (requestCode==1001){

            //grantResults stores the requested permissions result
            //check if grantResults == Permission is granted then call getCurrentLocation()

            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
           // getCurrentLocation();
                init();

            }
            else {
                Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT);
            }
        }
    }

    private void getCurrentLocation() {

        ProviderClient= LocationServices.getFusedLocationProviderClient(this);
        try{
            //Returns the best most recent location currently available.
            final Task Location=ProviderClient.getLastLocation();

            Location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
               if(task.isSuccessful()){
                   Log.d(TAG,"oncomplete found");
                   //get the result
                   Location location= (android.location.Location) task.getResult();
                   //get lat & lng value from  Location location= (android.location.Location) task.getResult();
                   LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                   //set the camera on location
                   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,12.f));
               }else{
                   Log.d(TAG,"oncomplete location in null");
               }
                }
            });
        }
        catch (SecurityException e) {
            Log.e(TAG,"getdeviceLoaction: SecutiryException"+e.getMessage());
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
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //getCurrentLocation();
            init();
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
}
