# Weather Android App
## Detailed Design and Functionality

### About this app

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

## 2.2 Swipe down to refresh screen using SwipeRefreshLayout
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

# 4 Next 7-days Forecast
## 4.1 User Interface
![Current Weather](https://raw.githubusercontent.com/maanmehta/screenshots/master/stormy/Next7DaysForecast.png)

# 5 Hourly Forecast
## 5.1 User Interface
![Current Weather](https://raw.githubusercontent.com/maanmehta/screenshots/master/stormy/HourlyForecast.png)
