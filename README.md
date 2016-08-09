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

## 2.1 User Interface
![Current Weather](https://raw.githubusercontent.com/maanmehta/screenshots/master/stormy/CurrentWeather.png)

## 2.2 Refresh - SwipeRefresh
![SwipeRefresh1](https://raw.githubusercontent.com/maanmehta/screenshots/master/stormy/SwipeRefresh1.png)

```java
//Code for Swipe Refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.Red,R.color.Orange,R.color.Blue,R.color.Green);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v(TAG,"************** SWIPE REFRESH EVENT TRIGGERED!!!!!");
                getForecast();
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
