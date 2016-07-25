package mun.treehsbeginandro_stormy.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Hour implements Parcelable{
    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mTimezone;
    private double mWindSpeed;

    public double getWindSpeed() {
        return (int) Math.round(mWindSpeed * 1.609);
    }

    public void setWindSpeed(double windSpeed) {
        mWindSpeed = windSpeed;
    }

    public Hour(){}

    public String getIcon() {
        return mIcon;
    }

    public int getIconId(){
        return Forecast.getIconId(mIcon);
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

    public double getTemperature() {
        //returning celcius
        double celcius = (mTemperature-32) *5/9;
        return (int) Math.round(celcius);
        //return (int) Math.round(mTemperatureMax);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public double getPrecipChance() {
        return mPrecipChance;
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

    @Override
    public int describeContents() {
        return 0;
    }

    public String getHour(){
        SimpleDateFormat formatter = new SimpleDateFormat("h a");
        // find timezone from json and set it to formatter
        formatter.setTimeZone(TimeZone.getTimeZone(getTimezone()));

        // the time in the JSON is in unix format and is in seconds, hence we multiply it by 1000
        // as the the method expects milliseconds
        Date dayTime = new Date(getTime() * 1000);

        return formatter.format(dayTime);
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTime);
        dest.writeString(mIcon);
        dest.writeDouble(mTemperature);
        dest.writeDouble(mHumidity);
        dest.writeDouble(mPrecipChance);
        dest.writeString(mTimezone);
        dest.writeString(mSummary);
        dest.writeDouble(mWindSpeed);

    }

    // private constructor for Day class for reading data from Parcel
    // order of read has to be exact same as the order we wrote it
    private Hour(Parcel in){
        mTime = in.readLong();
        mIcon = in.readString();
        mTemperature = in.readDouble();
        mHumidity = in.readDouble();
        mPrecipChance = in.readDouble();
        mTimezone = in.readString();
        mSummary = in.readString();
        mWindSpeed = in.readDouble();
    }

    public static final Creator<Hour> CREATOR = new Creator<Hour>() {
        @Override
        public Hour createFromParcel(Parcel source) {
            return new Hour(source);
        }

        @Override
        public Hour[] newArray(int size) {
            return new Hour[size];
        }
    };
}
