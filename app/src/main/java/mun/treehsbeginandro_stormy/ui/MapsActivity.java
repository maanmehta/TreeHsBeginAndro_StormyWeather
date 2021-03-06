package mun.treehsbeginandro_stormy.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
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
import com.google.android.gms.maps.model.MarkerOptions;

import mun.treehsbeginandro_stormy.R;
import mun.treehsbeginandro_stormy.service.Constants;
import mun.treehsbeginandro_stormy.service.FetchAddressIntentService;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private TextView mAddressValueTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RelativeLayout mRL;

    public static final String TAG = MapsActivity.class.getSimpleName();

    // This defines a request code to send to Google Play services, which is returned in
    // Activity.onActivityResult(). We will send it to google as part of resolution code block when
    // connection fails in onConnectionFailed method.
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private AddressResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAddressValueTextView = (TextView) findViewById(R.id.addressValueTextView);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Code for Swipe Refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeToRefresh4Maps);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.Red,R.color.Orange,R.color.Blue,R.color.Green);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v(TAG,"************** MAPS - SWIPE REFRESH EVENT TRIGGERED!!!!!");
                findLocation();
            }
        });

        // Bug fix for Swipe Refresh - Adding onClickListener to RelativeLayout inside the SwipeRefresh
        // This was done only to fix the bug that animation for SwipeRefresh was not working properly
        // So added the empty onClick on the encompassing RelativeLayout
        // Got this idea as I noticed that animation for swipe refresh worked fine only
        // on widgets that had set an onClickListener and not on other parts of the screen, so
        // I added OnClickListener to the whole RelativeLayout and voila
        mRL = (RelativeLayout) findViewById(R.id.topRL4Maps);
        mRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just empty method - do nothing
            }
        });

        mResultReceiver = new AddressResultReceiver(new Handler());

        // create and initialize GoogleApiClient instance
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                //.addApi(AppIndex.API)
                .build();

        // Create the LocationRequest object - this will have minimal impacts on power
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(60 * 60 * 1000)        // 60 minutes, in milliseconds
                .setFastestInterval(1 * 60 * 1000); // 1 minute, in milliseconds
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "*********** onConnected event");

        findLocation();
    }

    private void findLocation() {

        // TODO: Check Permissions code
        /**
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
         */

        // set default text
        mAddressValueTextView.setText("Nearest Address: \n\n" + "loading ...\n   ");

                Log.d(TAG, "*********** findLocation method started. Finding current Location started");
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation == null) {
            // Location can be null when the last location is unknown, therefore getLastLocation
            // returns null location, so we need to use requestLocationUpdates api and provide a
            // LocationRequest object
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(mCurrentLocation);
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, "********** Location Object toString is: " + location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng currentLatLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(currentLatLng)
                .title("I am here NOW!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,16));

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 6000, null);

        // use reverse GeoCoding to find nearest address from the Location (latitude and logitude)
        startGeoCodingIntentService(location);
    }


    /**
    * Start an IntentService that will do reverse GeoCoding to find nearest
    * address from the Location (latitude and longitude)
    */
    private void startGeoCodingIntentService(Location location) {
        Log.d(TAG, "********** Starting GeoCoding Intent Service method");
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,mCurrentLocation);
        startService(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "****** Location services suspended. Please reconnect.");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {

            // remove location Updates when the application pauses so it does not continue to request
            // location updates and drain power and battery. When the app resumes, it will need
            // again start requesting location updates. In our code, onResume, calls connect which
            // calls onConnected with is where we have added code to requestLocationUpdates again
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            // onPause, disconnect GoogleAPIClient, on Resume, we will need to connect again
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        handleNewLocation(location);
    }

    @Override
    public void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    @SuppressLint("ParcelCreator")
    private class AddressResultReceiver extends ResultReceiver {

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        // callback method called then the result is received from the FetchAddressIntentService
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            Log.d(TAG, "********** Starting onReceiveResult method");

            super.onReceiveResult(resultCode, resultData);

            if (resultCode == Constants.SUCCESS_RESULT) {

                // if success code was sent, then get the Address object sent in the bundle
                Address addressObj = resultData.getParcelable(Constants.RCVR_RESULT_ADDRESS_OBJ);
                String province = addressObj.getAdminArea();

                String city = null;
                if (addressObj.getSubLocality()!=null) city = addressObj.getSubLocality();
                else if(addressObj.getLocality()!=null) city = addressObj.getLocality();
                else if (addressObj.getSubAdminArea()!=null) city = addressObj.getSubAdminArea();
                else if (addressObj.getSubThoroughfare()!=null) city = addressObj.getSubThoroughfare();
                else city = "Current Location";

                mAddressValueTextView.setText("Nearest Address: \n\n" + addressObj.getAddressLine(0) + "\n" + addressObj.getAddressLine(1));
                //Toast.makeText(MapsActivity.this, "Nearest address: " + addressObj.getAddressLine(0) + "\n" + addressObj.getAddressLine(1)  + "\n" + addressObj.getAddressLine(2), Toast.LENGTH_LONG).show();
            } else {
                //error scenario, so display error message sent by the FetchAddressIntentService
                Toast.makeText(MapsActivity.this, "Error: " + resultData.getString(Constants.RCVR_RESULT_MSG_KEY), Toast.LENGTH_LONG).show();
            }


            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
