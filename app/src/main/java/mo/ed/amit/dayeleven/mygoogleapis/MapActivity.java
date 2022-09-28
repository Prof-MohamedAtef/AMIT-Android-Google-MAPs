package mo.ed.amit.dayeleven.mygoogleapis;

import static mo.ed.amit.dayeleven.mygoogleapis.MapHelper.returnCameraPosition;
import static mo.ed.amit.dayeleven.mygoogleapis.MapHelper.statusCheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import mo.ed.amit.dayeleven.mygoogleapis.databinding.ActivityMainBinding;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        android.location.LocationListener,
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private String GoogleApiKey = "AIzaSyCmV7jfWi807RzLygD97LKezfB_P_JaPKE";
    ActivityMainBinding binding;
    private GoogleMap mGoogleMap;
    private MapStyleOptions mMapStyleOptions;
    private SupportMapFragment mapFragment;
    private LocationManager manager;
    private FusedLocationProviderClient location;
    private Location mLocation;
    private boolean permissionGranted;
    private View parentLayout;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACMENT = 10;

    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICES_RES_REQUEST = 7001;
    private Location mLastLocation;
    private Marker mUserMarker;
    private LatLng mCenterLatLong;
    private CameraPosition googlePlex;
    private SessionManagement sessionMgmt;
    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        parentLayout = findViewById(android.R.id.content);

        sessionMgmt=new SessionManagement(getApplicationContext());
        userName= sessionMgmt.getUserName();
        binding.tvUsername.setText(userName);

        binding.centerCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CenterCamera: ", "Clicked me");
                if (mLocation != null) {
                    if (mGoogleMap!=null){
                        animateCamera(mGoogleMap);
                    }
//                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 19.0f));
                }
            }
        });
        initGoogleMap();
        buildGoogleApiClient();
        createLocationRequest();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        populateMapControls(mGoogleMap);
        if (mLocation != null) {
            LatLng sydney = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Mohamed Atef's Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }


        animateCamera(mGoogleMap);

        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;


                mGoogleMap.clear();

                try {

                    mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MarkerOptions markerOptions(){
        MarkerOptions options=new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.kfc));
        options.title("kfc");
        return options;
    }

    /*
    animate and move camera
    uses returnCameraPosition
     */
    private void animateCamera(GoogleMap mMap) {
        if (mLastLocation != null) {
            googlePlex = returnCameraPosition(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()), 19.0f);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1500, null);
            mMap.addMarker(markerOptions());
        } else {
            Log.d("ERROR", "Cannot get Your Location");
            retryRequestLocationUpdates();
        }
    }

    private void populateMapControls(GoogleMap mMap) {
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setTrafficEnabled(false);
        mGoogleMap.setIndoorEnabled(false);
        mGoogleMap.setBuildingsEnabled(false);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getApplicationContext(), R.raw.map_style));
            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }
        mMap.clear(); //clear old markers
        mMap.getUiSettings().setCompassEnabled(false);
        disableCenterLocationButton(mMap);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    private void disableCenterLocationButton(GoogleMap mMap) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            LocationGrantPermissionTry();
        }
    }

    private void initGoogleMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map_fragment);
        mapFragment.getMapAsync(this);
        manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        location = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        LocationGrantPermissionTry();
    }

    private void LocationGrantPermissionTry() {
        ActivityCompat.requestPermissions(MapActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                Configs.MY_PERMISSIONS_REQUEST_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Configs.MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //If user presses allow
//                Toast.makeText(MapActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();
                permissionGranted = true;
            } else {
                //If user presses deny
                permissionGranted = false;
            }
            if (location == null) {
                statusCheck(manager, getApplicationContext());
            } else {
                //If everything went fine lets get latitude and longitude
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                location.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLocation = location;
                            double currentLatitude = location.getLatitude();
                            double currentLongitude = location.getLongitude();
                        } else {
                            ShowSnackBar(parentLayout, getResources().getString(R.string.internet_location_disabled));
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLocation = location;
        Log.e("OnLocationChanged", "lat: " + location.getLatitude() + "long :" + location.getLongitude());
        mLastLocation = location;
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeMap(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (mGoogleMap != null) {
            mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(19f).tilt(70).build();

            try {
                mGoogleMap.setMyLocationEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
                LocationGrantPermissionTry();
            }
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        } else {
//            Toast.makeText(getApplicationContext(),"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapActivity.this)
                .addConnectionCallbacks(MapActivity.this)
                .addOnConnectionFailedListener(MapActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACMENT);
    }

    private void ShowSnackBar(View parentLayout, String msg) {
        final Snackbar snackBar = Snackbar.make(parentLayout, msg, Snackbar.LENGTH_LONG);
        snackBar.show();
    }

    private void ShowSnackBar(View parentLayout) {
        final Snackbar snackBar = Snackbar.make(parentLayout, getString(R.string.accept_grant), Snackbar.LENGTH_LONG);
        snackBar.setAction("Grant", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (permissionGranted) {
                            if (mLocation != null) {
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 19.0f));
                            }
                        } else {
                            LocationGrantPermissionTry();
                        }
                    }
                }).setActionTextColor(getResources().getColor(android.R.color.holo_green_dark))
                .show();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapActivity.this);
                setUpLocation();
            }
        }
    }

    /*
    SetupLocation and DisplayLocation (focus or animate camera to specific marker/coordinates)
     */
    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request runtime permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                displayLocation();
            }
        }
    }

    /*
    display location and animate camera
     */
    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
            if (mUserMarker != null) {
                mUserMarker.remove();// remove already marker
                if (mGoogleMap != null) {
                    mUserMarker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title("Your Location"));

                    // Move Camera To this position
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 19.0f));
                    // draw animation rotate marker
//                            rotateMarker(mCurrent, -360, mMap);
                }
            } else {
                if (mGoogleMap != null) {
                    mUserMarker = mGoogleMap.addMarker(new MarkerOptions()
//                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.scooter))
                            .position(new LatLng(latitude, longitude))
//                                                    .title("You"));
                            .title("Your Location"));

                    // Move Camera To this position
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 19.0f));
                    // draw animation rotate marker
//                            rotateMarker(mCurrent, -360, mMap);
                }
            }
        } else {
            Log.d("ERROR", "Cannot get Your Location");
            retryRequestLocationUpdates();
            ShowSnackBar(parentLayout, getResources().getString(R.string.internet_location_disabled));
        }
    }

    /*
    create new location request
     */
    private void retryRequestLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    check google play services on your device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }
}