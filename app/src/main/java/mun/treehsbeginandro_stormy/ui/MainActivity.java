package mun.treehsbeginandro_stormy.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import mun.treehsbeginandro_stormy.service.Constants;
import mun.treehsbeginandro_stormy.service.FetchAddressIntentService;
import mun.treehsbeginandro_stormy.weather.Current;
import mun.treehsbeginandro_stormy.R;
import mun.treehsbeginandro_stormy.weather.Day;
import mun.treehsbeginandro_stormy.weather.Forecast;
import mun.treehsbeginandro_stormy.weather.Hour;
import okhttp3.*;

public class MainActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG=MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST_KEY = "DailyForecastKey";
    public static final String HOURLY_FORECAST_KEY = "HourlyForecastKey";

    private Forecast mForecast;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private String mCity = "--";


    // This defines a request code to send to Google Play services, which is returned in
    // Activity.onActivityResult(). We will send it to google as part of resolution code block when
    // connection fails in onConnectionFailed method.
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private AddressResultReceiver mResultReceiver;

    /**
    @BindView(R.id.timeValueTextView) TextView mTimeField;
    @BindView(R.id.temperatureValueTextView) TextView mTemperatureField;
    @BindView(R.id.humidityValueTextView) TextView mHumidityField;
    @BindView(R.id.popValueTextView) TextView mPrecipField;
    @BindView(R.id.summaryValueTextView) TextView mSummaryField;
    @BindView(R.id.condIconImageView) ImageView mIconImage;
     */

    private TextView mTimeField;
    private TextView mCityValueTextView;
    private TextView mTemperatureField;
    private TextView mHumidityField;
    private TextView mPrecipField;
    private TextView mSummaryField;
    private ImageView mIconImage;
    //private ImageView mRefreshIconImage;
    //private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Button mSevenDayButton;
    private Button mHourlyButton;
    private Vibrator mVibrator;
    private boolean isCelcius = true;

    private boolean isCelcius() {
        return isCelcius;
    }

    private void setCelcius(boolean celcius) {
        isCelcius = celcius;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButterKnife.bind(this);

        mTimeField = (TextView) findViewById(R.id.timeValueTextView);
        mCityValueTextView = (TextView) findViewById(R.id.cityValueTextView);
        mTemperatureField = (TextView) findViewById(R.id.temperatureValueTextView);
        mHumidityField = (TextView) findViewById(R.id.humidityValueTextView);
        mPrecipField = (TextView) findViewById(R.id.popValueTextView);
        mSummaryField = (TextView) findViewById(R.id.summaryValueTextView);
        mIconImage = (ImageView) findViewById(R.id.condIconImageView);
        mSevenDayButton = (Button) findViewById(R.id.dailyButton);
        mHourlyButton = (Button) findViewById(R.id.hourlyButton);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        mCityValueTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVibrator.vibrate(15);
                //Intent intent = new Intent(MainActivity.this,HourlyForecastActivity.class);
                //intent.putExtra(HOURLY_FORECAST_KEY,mForecast.getHourlyForecast());


                Intent intent = new Intent(MainActivity.this,MapsActivity.class);

                startActivity(intent);
            }
        });

        mSevenDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVibrator.vibrate(15);
                Intent intent = new Intent(MainActivity.this,DailyForecastActivity.class);
                intent.putExtra(DAILY_FORECAST_KEY,mForecast.getDailyForecast());
                startActivity(intent);
            }
        });

        mHourlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVibrator.vibrate(15);
                Intent intent = new Intent(MainActivity.this,HourlyForecastActivity.class);
                intent.putExtra(HOURLY_FORECAST_KEY,mForecast.getHourlyForecast());


                //Intent intent = new Intent(MainActivity.this,MapsActivity.class);

                startActivity(intent);
            }
        });

        /**
        mRefreshIconImage = (ImageView) findViewById(R.id.refreshImageView);
        mRefreshIconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getForecast();

            }
        });



        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

         */

        mTemperatureField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVibrator.vibrate(15);
                toggleTemperature();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeToRefresh);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.Red,R.color.Orange,R.color.Blue,R.color.Green);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v(TAG,"************** SWIPE REFRESH EVENT TRIGGERED!!!!!");
                getForecast();

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


        //getForecast();
    }

    private void getForecast() {
        String apiKey = "006f325d675ba6c9883737d7add4eded";

        //double latitude= 43.6869674;  //Toronto GPS coordinates
        //double longitude = -79.2663151;

        // get lat and long from location object
        double latitude = mCurrentLocation.getLatitude();
        double longitude = mCurrentLocation.getLongitude();
        Log.d(TAG,"******* Lat: " + latitude + " Long: " + longitude);


        String foreCastURL = "https://api.forecast.io/forecast/"
                + apiKey + "/"
                + latitude + ","
                + longitude;
        if (isNetworkAvailable()) {

            // toggleRefreshIcon();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(foreCastURL).build(); // provide url to request
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // toggleRefreshIcon();
                            alertUserErrorViaDialog();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });

                    Log.e(TAG, "****** UNSUCCESSFUL ASYNCH REST CALL ******");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    // toggle refresh icon and progress bar
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //toggleRefreshIcon();
                        }
                    });

                    try {
                        String jsonResponse = response.body().string();
                        if (response.isSuccessful()) {
                            //Log.v(TAG, "****** RESPONSE IS: ******\n" + jsonResponse);
                            Log.v(TAG, "****** RESPONSE IS SUCCESSFUL\n");
                            mForecast = getForecastFromJsonResponse(jsonResponse);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateView();
                                    mVibrator.vibrate(150);
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        } else {
                            Log.e(TAG, "****** UNSUCCESSFUL RESPONSE IS: ******\n" + jsonResponse);
                            alertUserErrorViaDialog();
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "Exception during response", e);
                    }



                }
            });
        } else {
            Toast.makeText(MainActivity.this, R.string.error_network_unavailable_message, Toast.LENGTH_LONG).show();
        }
    }



    /**
    private void toggleRefreshIcon() {
        if (mProgressBar.getVisibility()== View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshIconImage.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshIconImage.setVisibility(View.INVISIBLE);
        }
    }
     */

    private void toggleTemperature() {
        if (!isCelcius) {
            mTemperatureField.setText(Integer.toString(mForecast.getCurrent().getCelcius()));
            setCelcius(true);

        } else {
            mTemperatureField.setText(Integer.toString(mForecast.getCurrent().getFahrenheit()));
            setCelcius(false);

        }
    }


    private Current getCurrentWeatherFromJsonResponse(String jsonResponse) throws JSONException {
        JSONObject foreCastJsonObj = new JSONObject(jsonResponse);

        String timezone = foreCastJsonObj.getString("timezone");
        int idx = timezone.lastIndexOf("/");
        Log.i(TAG,"******* TIMEZONE from Json Response is: ***** " + timezone.substring(idx+1));

        JSONObject currently = foreCastJsonObj.getJSONObject("currently");

        Current current = new Current();
        current.setHumidity(currently.getDouble("humidity"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTime(currently.getLong("time"));
        current.setTimezone(timezone);
        current.setWindSpeed(currently.getDouble("windSpeed"));

        Log.i(TAG,"******* At " + current.getFormattedTime() + ",temperature is: "
                + current.getFahrenheit() + " F "
                + current.getCelcius() + " C "
                + "and condition is: "
                + current.getSummary());


        return current;
    }

    private Hour[]  getHourlyWeatherFromJsonResponse(String jsonResponse) throws JSONException {
        JSONObject foreCastJsonObj = new JSONObject(jsonResponse);
        String timezone = foreCastJsonObj.getString("timezone");
        //Log.i(TAG, "******* TIMEZONE from Json Response is: ***** " + timezone);

        JSONObject hourly = foreCastJsonObj.getJSONObject("hourly");
        JSONArray dataArray = hourly.getJSONArray("data");

        Hour[] mHourlyForecast = new Hour[dataArray.length()];

        for (int i=0;i<dataArray.length();i++){
            JSONObject HourJsonObj = dataArray.getJSONObject(i);
            Hour oneHour = new Hour();
            oneHour.setHumidity(HourJsonObj.getDouble("humidity"));
            oneHour.setIcon(HourJsonObj.getString("icon"));
            oneHour.setPrecipChance(HourJsonObj.getDouble("precipProbability"));
            oneHour.setSummary(HourJsonObj.getString("summary"));
            oneHour.setTemperature(HourJsonObj.getDouble("temperature"));
            oneHour.setTime(HourJsonObj.getLong("time"));
            oneHour.setTimezone(timezone);
            oneHour.setWindSpeed(HourJsonObj.getDouble("windSpeed"));
            mHourlyForecast[i] = oneHour;
        }

        return mHourlyForecast;
    }

    private Day[] getDailyForeCastFromJsonResponse(String jsonResponse) throws JSONException {
        JSONObject foreCastJsonObj = new JSONObject(jsonResponse);
        String timezone = foreCastJsonObj.getString("timezone");
        //Log.i(TAG, "******* TIMEZONE from Json Response is: ***** " + timezone);

        JSONObject daily = foreCastJsonObj.getJSONObject("daily");
        JSONArray dataArray = daily.getJSONArray("data");

        Day[] mDailyForecast = new Day[dataArray.length()];

        for (int i=0;i<dataArray.length();i++){
            JSONObject DayJsonObj = dataArray.getJSONObject(i);
            Day oneDay = new Day();
            oneDay.setHumidity(DayJsonObj.getDouble("humidity"));
            oneDay.setIcon(DayJsonObj.getString("icon"));
            oneDay.setPrecipChance(DayJsonObj.getDouble("precipProbability"));
            oneDay.setSummary(DayJsonObj.getString("summary"));
            oneDay.setTemperatureMax(DayJsonObj.getDouble("temperatureMax"));
            oneDay.setTemperatureMin(DayJsonObj.getDouble("temperatureMin"));
            oneDay.setTime(DayJsonObj.getLong("time"));
            oneDay.setTimezone(timezone);
            oneDay.setWindSpeed(DayJsonObj.getDouble("windSpeed"));
            mDailyForecast[i] = oneDay;
        }

        return mDailyForecast;

    }

    private Forecast getForecastFromJsonResponse(String jsonResponse) throws JSONException{
        Forecast forecast = new Forecast();

        //set current weather object
        forecast.setCurrent(getCurrentWeatherFromJsonResponse(jsonResponse));

        //set daily weather array
        forecast.setDailyForecast(getDailyForeCastFromJsonResponse(jsonResponse));

        //set hourly weather array
        forecast.setHourlyForecast(getHourlyWeatherFromJsonResponse(jsonResponse));

        return forecast;
    }

    private void alertUserErrorViaDialog() {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.show(getFragmentManager(),null);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo !=null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    private void updateView() {
        setCelcius(true);
        mTimeField.setText("At " + mForecast.getCurrent().getFormattedTime() + " forecast was");
        mTemperatureField.setText(Integer.toString(mForecast.getCurrent().getCelcius()));
        mHumidityField.setText(Double.toString(mForecast.getCurrent().getHumidity()));
        mPrecipField.setText(Integer.toString(mForecast.getCurrent().getPrecipChance()) + "%");
        mSummaryField.setText(mForecast.getCurrent().getSummary());
        mCityValueTextView.setText(mCity);
        Drawable drawable = getResources().getDrawable(mForecast.getCurrent().getIconId());
        mIconImage.setImageDrawable(drawable);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "*********** Location services connected.");

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

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation == null) {
            // Location can be null when the last location is unknown, therefore getLastLocation
            // returns null location, so we need to use requestLocationUpdates api and provide a
            // LocationRequest object
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            startGeoCodingIntentService(mCurrentLocation);
        }

        getForecast();
    }


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
            //location updates and drain power and battery. When the app resumes, it will need
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
        startGeoCodingIntentService(location);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        /**Action viewAction = Action.newAction(
         Action.TYPE_VIEW, // TODO: choose an action type.
         "Maps Page", // TODO: Define a title for the content shown.
         // TODO: If you have web page content that matches this app activity's content,
         // make sure this auto-generated web page URL is correct.
         // Otherwise, set the URL to null.
         Uri.parse("http://host/path"),
         // TODO: Make sure this auto-generated app URL is correct.
         Uri.parse("android-app://mun.treehsbeginandro_stormy.ui/http/host/path")
         );
         AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /**Action viewAction = Action.newAction(
         Action.TYPE_VIEW, // TODO: choose an action type.
         "Maps Page", // TODO: Define a title for the content shown.
         // TODO: If you have web page content that matches this app activity's content,
         // make sure this auto-generated web page URL is correct.
         // Otherwise, set the URL to null.
         Uri.parse("http://host/path"),
         // TODO: Make sure this auto-generated app URL is correct.
         Uri.parse("android-app://mun.treehsbeginandro_stormy.ui/http/host/path")
         );
         AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);*/
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

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            Log.d(TAG, "********** Starting onReceiveResult method");

            super.onReceiveResult(resultCode, resultData);

            mCity = resultData.getString(Constants.RCVR_RESULT_CITY_KEY);
            String province = resultData.getString(Constants.RCVR_RESULT_PROV_KEY);

            Log.d(TAG, "**********City is: "+mCity + " and Province is: " + province);
        }
    }
}
