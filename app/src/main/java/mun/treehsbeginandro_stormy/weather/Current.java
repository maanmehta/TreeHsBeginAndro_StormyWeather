package mun.treehsbeginandro_stormy.weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import mun.treehsbeginandro_stormy.R;

public class Current {
    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mTimezone;
    private double mWindSpeed;

    public int getWindSpeed() {
        return (int) Math.round(mWindSpeed * 1.609);
    }

    public void setWindSpeed(double windSpeed) {
        mWindSpeed = windSpeed;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) { mIcon = icon; }

    public int getIconId(){
        return Forecast.getIconId(mIcon);
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getFormattedTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimezone()));
        TimeZone.getTimeZone(getTimezone()).getDisplayName();
        Date currentTime = new Date(getTime() * 1000);
        return formatter.format(currentTime);
    }

    //public int getTemperature() {
        //return (int) Math.round(mTemperature);
    //}

    public int getFahrenheit() {
        return (int) Math.round(mTemperature);
    }

    public int getCelcius(){
        double celcius = (mTemperature-32) *5/9;
        return (int) Math.round(celcius);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    /**
    public double getHumidity() {
        return mHumidity;
    }
    */
    public int getHumidity(){
        return (int) Math.round(mHumidity*100);// json is returning value between 0 and 1, we need to return percentage, so multiplying by 100
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int  getPrecipChance() {
        return (int) Math.round(mPrecipChance*100); // json is returning value between 0 and 1, we need to return percentage, so multiplying by 100 before returnign to the view
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }
}
