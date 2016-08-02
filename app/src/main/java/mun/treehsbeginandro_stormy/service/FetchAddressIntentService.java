package mun.treehsbeginandro_stormy.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mun.treehsbeginandro_stormy.R;

public class FetchAddressIntentService extends IntentService {

    private static final String TAG = "FetchAddressIntentSvc";

    /**
     * The receiver object where results are forwarded from this service. This receiver object's
     * reference is sent by th Activity that will initiate thsi IntentService and is parceled
     * as part of the Intent data. After we have the results, we will call the send method of
     * this receiver object and sent the resultCode and data as part of the Bundle object
     */
    protected ResultReceiver mReceiver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread. Ths constructor is used to create an instance
        // of this IntentService and we need to send a label to name the worker thread
        // to execute this service asynchronously
        //Log.d(TAG, "********** starting FetchAddressIntentService constructor method");

        super(TAG);
        Log.d(TAG, "********** starting FetchAddressIntentService() constructor method");
    }

    /**
     * Tries to get the location address using a Geocoder. If successful, sends an address to a
     * result receiver. If unsuccessful, sends an error message instead.
     * Note: We define a {@link android.os.ResultReceiver} in * MainActivity to process content
     * sent from this service.
     *
     * This service calls this method from the default worker thread with the intent that started
     * the service. When this method returns, the service automatically stops.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "********** onHandleIntent method");

        String errorMessage = "";

        // get reference tot he ResultsReceiver object sent as part of Intent data by the calling
        // activity
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // check if Receiver object is null and log and send back an appropriate error message
        if (mReceiver==null){
            Log.wtf(TAG,"No ResultsRceiver received from the calling Activity. Therefore, there is no where to send back results");
            return;
        }

        // get the Location object sent by the calling Activity as part of the Intent data
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        // check if Receiver object is null and log and send back an appropriate error message
        if (location==null) {
            errorMessage = getString(R.string.no_location_data_provided);
            Log.wtf(TAG,errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT,errorMessage,null);
            return;
        }

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addressList=null;

        // for testing only - Hardcoding location lat and longitude to Sandbanks to test missing city Names in getSubLocality

        // Hardcoded gps coordinates for 12 Kildonan
        //double latitude= 43.6869674;  //12 Kildonan GPS coordinates
        //double longitude = -79.2663151; // 12 Kildonan

        //Setting hardcoded gps coordinates for Sandbanks Outlet beach area to test missing major city Name
        //double latitude= 43.8979913;  //Sandbanks GPS coordinates
        //double longitude = -77.2254795; // Sandbanks
        //location.setLatitude(latitude);
        //location.setLongitude(longitude);

        try {
            addressList = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1 // just getting one address from geocoder
            );
        } catch (IOException e) {
            errorMessage="IOException during reverse Geocoding";
            Log.e(TAG,errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT,errorMessage,null);
            return;
        }

        if (addressList==null || addressList.size()==0){
            errorMessage="No addresses returned by Geocoder service";
            Log.e(TAG,errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT,errorMessage,null);
            return;
        } else {
            Address addressObj = addressList.get(0);
            Log.d(TAG,"******* SUCCESS reverse Geocoding - Address obtained" + addressObj.getAddressLine(0)+ "\nAddress toString is: " + addressObj.toString());

            deliverResultToReceiver(Constants.SUCCESS_RESULT,"Success",addressObj);
        }

    }

    /**
     * Sends a resultCode and bundle to the receiver.
     * Adding String message and Address object tot he bundle before sending
     */
    private void deliverResultToReceiver(int resultCode, String message, Address address) {

        Log.d(TAG, "********** starting deliverResultToReceiver method");

        // create a bundle to send back as part of the send method back to Results Receiver or executor
        // of this service
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RCVR_RESULT_MSG_KEY, message);
        //bundle.putString(Constants.RCVR_RESULT_CITY_KEY, cityName);
        bundle.putParcelable(Constants.RCVR_RESULT_ADDRESS_OBJ,address);

        //send back the resultCode and the bundle data to the Results Receiver
        mReceiver.send(resultCode, bundle);
    }

}
