# Weather Android App
## Detailed Design and Functionality

### About this app    2

This android app provides following features using established Google design patterns and Google APIs:

1. Automatically determine current location using Location Services
2. Retrieves Weather data from forecast.io REST web service
3. Displays details of current weather on the main screen
4. Displays 7-day forecast
5. Displays hourly forecast
6. Displays current location in a Google Map view and the nearest address determined using reverse GeoCoding

### About this document
Purpose of this document is to provide:
 - functionality provided by this app,
 - detailed technical design for important features

# 1 Main Screens / Activities

The app has following screens or activities:

- Current Weather
- Your Location
- Next 7-days Forecast
- Hourly Forecast

# 2 Current Weather

## 2.1 User Interface and Functionality
![Current Weather](https://raw.githubusercontent.com/maanmehta/screenshots/master/stormy/CurrentWeather.png)

Above screenshot shows the main screen of the app that displays the current weather conditions. It implements following functionality:
* When the user clicks the temperature text, it toggles from celsius to Fahrenheit as shown in the above screenshots
* Location of the user is detected automatically using Location Services.
* We then get the user's current Latitude and Longitude from the Location object
* User's current latitude and longitude are then sent as URL parameters to REST web service call to forecast.io to receive the detailed weather data. This weather dats is received in JSON format and contains current weather details and hourly and daily forecast.
* Screen displays user's current city which is determined using Reverse-Gecoding from the user's Location object
* At the bottom of the screen, there are two buttons that start two new activities - one  displays hourly forecast and the other displays next week's forecast
* Map Activity - If the user clicks on either the City name or the weather condition icon at the top of the screen, a new activity (screen) is displayed that shows users current location in a map fragment and also displays user's nearest address.

## 2.2 Main code artifacts for this Activity
* View (Layout XML) - [activity_main.xml](https://github.com/maanmehta/TreeHsBeginAndro_StormyWeather/blob/master/app/src/main/res/layout/activity_main.xml)
* Activity - [MainActivity.java](https://github.com/maanmehta/TreeHsBeginAndro_StormyWeather/blob/master/app/src/main/java/mun/treehsbeginandro_stormy/ui/MainActivity.java)
* Model Classes - [Forecast.java, Current.java, Hour.java and Day.java](https://github.com/maanmehta/TreeHsBeginAndro_StormyWeather/tree/master/app/src/main/java/mun/treehsbeginandro_stormy/weather)

## 2.3 Adding automatic location determination - Google Play Services Location API

Google Play Services provides provides Location API which you can use to add location awareness to android apps. Google Training website has a step-by-step guide at this [link](https://developer.android.com/training/location/index.html). Follow the steps in this training guide.

### Implement Listeners

Add following Listeners to the `MainActivity.java`:
- GoogleApiClient.ConnectionCallbacks,
- GoogleApiClient.OnConnectionFailedListener,
- LocationListener

```java
public class MainActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
```

### onCreate method

Create and initialize the `GoogleApiClient` and `LocationRequest` objects in the `onCreate` method as follows:

```java
// Build and initialize GoogleApiClient instance
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
```

### onConnected method

Get the current location in the onConnected method as follows

```java
mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

if (mCurrentLocation == null) {
    // Location can be null when the last location is unknown, therefore getLastLocation
    // returns null location, so we need to use requestLocationUpdates api and provide a
    // LocationRequest object
    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
}

// Now that we have location object, we can call our custom method to get Forecast JSON from weather webservice
getForecast();
```

### Permission to get precise location with ACCESS_FINE_LOCATION
For our app, we decided to use fine location detection, so that your app can get as precise a location as possible from the available location providers. For this, we added the following `uses-permission` element in our app manifest file as follows:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```

### Activity lifecycle methods - onStart, onResume, onPause and onStop

In the activity lifecycle methods, connect and disconnect GoogleApiClient object and since in onConnected, we called requestLocationUpdates, in onPause, we should call removeLocationUpdates so we ap is no longer getting loation updates when it has paused

```java
@Override
public void onStart() {
    super.onStart();

    mGoogleApiClient.connect();
}

@Override
protected void onResume() {
    super.onResume();

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
public void onStop() {
    super.onStop();

    mGoogleApiClient.disconnect();
}
```

###onLocationChanged
Since we are requesting location updates, app will continue to get the new location object, therefore, in the `onLocationChanged` method we need to save the changed location to our mCurrentLocation property of the class
```java
@Override
public void onLocationChanged(Location location) {
    mCurrentLocation = location;
}
```

## 2.4 Get Weather forecast for current location - Call REST web service

## 2.5 Reverse GeoCoding - Get nearest address based on current location

## 2.6 Swipe down to refresh screen using SwipeRefreshLayout
![SwipeRefresh1](https://raw.githubusercontent.com/maanmehta/screenshots/master/stormy/SwipeRefresh1.png)

Used `SwipeRefreshLayout` to implement the functionality where the user can swipe or drag down the screen to refresh its contents. When the user drags or swipes down the screen, screen shows a circular spinning progress icon and `onRefresh` event is triggered and the app developer can handle that event and implement the desired functionality. In this app we are calling our `getForecast()` when we handle that event.

### Layout

Following is the layout xml snippet where the SwipeRefreshLayout is the top level layout now that wraps the top RelativeLayout

```xml
<android.support.v4.widget.SwipeRefreshLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/swipeToRefresh"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <RelativeLayout
        android:id="@+id/topRL4MainActivity"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@drawable/bg_gradient_blue"
        android:paddingLeft="@dimen/activity_horizontal_margin"
        android:paddingRight="@dimen/activity_horizontal_margin"
        android:paddingTop="@dimen/activity_vertical_margin"
        android:gravity="center"
        tools:context=".ui.MainActivity">
```

### onRefresh() method
Following is the code snippet from the `onCreate()` method of the `MainActivity.java` where we set `OnRefreshListener` and handle the `OnRefresh()` method.

```java
mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeToRefresh);
mSwipeRefreshLayout.setColorSchemeResources(R.color.Red,R.color.Orange,R.color.Blue,R.color.Green);
mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
     @Override
     public void onRefresh() {
          Log.d(TAG,"************** SWIPE REFRESH EVENT TRIGGERED!!!!!");
          getForecast();
});
```

### Bug with SwipeRefreshLayout
I encountered a bug with SwipeRefreshLayout where the swipe down functionality would not trigger properly. I noticed that circular progress animation did not work properly and also the event was not fired most of the time. Then I stumbled upon a pattern that it worked fine whenever I started my swipe down on a TextField which had an `OnClickListener` set. To verify this, I set `OnClickListener` to another icon on the screen and saw the same behaviour. Therefore, to fix this bug, I set an `OnClickListener` to the top most `RelativeLayout` which was a child element to SwipeRefreshLayout and kept its `onClick()` method just empty. This fixed the bug and now the screen could be dragged down from anywhere. Here is the code snippet from the `onCreate` method of the `MainActivity.java`

```java
// Bug fix for Swipe Refresh - Adding onClickListener to RelativeLayout inside the SwipeRefresh
mRL = (RelativeLayout) findViewById(R.id.topRL4MainActivity);
mRL.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        // Just empty method - do nothing
    }
});
```

# 3 Your Location

## 3.1 User Interface
![Current Weather](https://raw.githubusercontent.com/maanmehta/screenshots/master/stormy/YourLocation.png)

## 3.2 Get Google Maps API Key

Project Id - <automatically named by google maps api when I created a new project)
API Key name: <You provide this on Google wizard>
Package name: Get this from your Android app's Manifest xml file

To get Certificate fingerprint(MD5) code follow these steps

1. Go to - C:\Program Files\Java\jdk1.6.0_26\bin
2. Inside the bin folder run the jarsigner.exe file
3. Open cmd prompt and execute the following two commands:
```dos
C:\Program Files\Java\jdk1.6.0_26\bin
keytool -list -keystore "C:/Users/your user name/.android/debug.keystore"
```
It will ask for Keystore password now. The default is "android" type and enter


My steps - Open Windows prompt
```dos
cd C:\Java\jdk1.8.0_91\bin

C:\Java\jdk1.8.0_91\bin>keytool -list -keystore "C:/Users/Mun/.android/debug.keystore"
Enter keystore password: android

Keystore type: JKS
Keystore provider: SUN

Your keystore contains 1 entry

androiddebugkey, Jul 12, 2016, PrivateKeyEntry,
Certificate fingerprint (SHA1): <removed>


After entering the above SHA-1 fingerprint in google wizard, got the following API key
<removed>

```

You can always see the Credentials you have created by visiting the following URL: https://console.developers.google.com/apis/credentials?project=evident-axle-138523&authuser=1


# 4 Next 7-days Forecast
## 4.1 User Interface
![Current Weather](https://raw.githubusercontent.com/maanmehta/screenshots/master/stormy/Next7DaysForecast.png)

# 5 Hourly Forecast
## 5.1 User Interface
![Current Weather](https://raw.githubusercontent.com/maanmehta/screenshots/master/stormy/HourlyForecast.png)
