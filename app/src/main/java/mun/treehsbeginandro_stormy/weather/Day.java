package mun.treehsbeginandro_stormy.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Day implements Parcelable {

    private String mIcon;
    private long mTime;
    private double mTemperatureMax;
    private double mTemperatureMin;
    private double mHumidity;
    private double mPrecipChance;
    private double mWindSpeed;
    private String mSummary;
    private String mTimezone;

    //default constructor
    public Day() {}

    public static final Creator<Day> CREATOR = new Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTime);
        dest.writeString(mIcon);
        dest.writeDouble(mTemperatureMax);
        dest.writeDouble(mTemperatureMin);
        dest.writeDouble(mHumidity);
        dest.writeDouble(mPrecipChance);
        dest.writeString(mTimezone);
        dest.writeString(mSummary);
        dest.writeDouble(mWindSpeed);

    }

    // private constructor for Day class for reading data from Parcel
    // order of read has to be exact same as the order we wrote it
    private Day(Parcel in){
        mTime = in.readLong();
        mIcon = in.readString();
        mTemperatureMax = in.readDouble();
        mTemperatureMin = in.readDouble();
        mHumidity = in.readDouble();
        mPrecipChance = in.readDouble();
        mTimezone = in.readString();
        mSummary = in.readString();
        mWindSpeed = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getTemperatureMax() {
        //returning celcius
        double celcius = (mTemperatureMax-32) *5/9;
        return (int) Math.round(celcius);
        //return (int) Math.round(mTemperatureMax);
    }

    public void setTemperatureMax(double temperatureMax) {
        mTemperatureMax = temperatureMax;
    }

    public int getTemperatureMin() {
        //returning celcius
        double celcius = (mTemperatureMin-32) *5/9;
        return (int) Math.round(celcius);
        //return (int) Math.round(mTemperatureMax);
    }

    public void setTemperatureMin(double temperatureMin) {
        mTemperatureMin = temperatureMin;
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

    public int getPrecipChance() {
        return (int) Math.round(mPrecipChance*100);
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

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public int getIconId(){
        return Forecast.getIconId(mIcon);
    }

    // return the name of the week based on the time value int he JSON for that day
    public String getDayOfTheWeek(){
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        // find timezone from json and set it to formatter
        formatter.setTimeZone(TimeZone.getTimeZone(getTimezone()));

        // the time in the JSON is in unix format and is in seconds, hence we multiply it by 1000
        // as the the method expects milliseconds
        Date dayTime = new Date(getTime() * 1000);

        return formatter.format(dayTime);
    }

    public String getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM");
        // find timezone from json and set it to formatter
        formatter.setTimeZone(TimeZone.getTimeZone(getTimezone()));

        // the time in the JSON is in unix format and is in seconds, hence we multiply it by 1000
        // as the the method expects milliseconds
        Date dayTime = new Date(getTime() * 1000);

        return formatter.format(dayTime);
    }

    public String getDayOfTheWeekAbbrev(){
        SimpleDateFormat formatter = new SimpleDateFormat("EEE");
        // find timezone from json and set it to formatter
        formatter.setTimeZone(TimeZone.getTimeZone(getTimezone()));

        // the time in the JSON is in unix format and is in seconds, hence we multiply it by 1000
        // as the the method expects milliseconds
        Date dayTime = new Date(getTime() * 1000);

        return formatter.format(dayTime);
    }

    public double getWindSpeed() {
        return (int) Math.round(mWindSpeed * 1.609);
    }

    public void setWindSpeed(double windSpeed) {
        mWindSpeed = windSpeed;
    }

}
