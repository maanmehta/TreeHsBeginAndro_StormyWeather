package mun.treehsbeginandro_stormy.ui;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import mun.treehsbeginandro_stormy.R;
import mun.treehsbeginandro_stormy.adapters.DayAdapter;
import mun.treehsbeginandro_stormy.weather.Day;

public class DailyForecastActivity extends ListActivity {
    View previousView = null;
    int previousPosition;

    private Day[] mDays;
    private boolean clickFlag;
    private Vibrator mVibrator;

    //private SwipeRefreshLayout mSwipeRefreshLayout;
    private static final String TAG=DailyForecastActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        clickFlag = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        /** CODE FOR SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.dailySwipeToRefresh);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.Red,R.color.Orange,R.color.Blue,R.color.Green);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v(TAG,"************** SWIPE REFRESH EVENT TRIGGERED!!!!!");
                //getForecast();
                showToast();
            }
        });
         */

        // get data (mDays array) from the intent
        Intent intent = getIntent();
        Parcelable[] parcelableArray = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST_KEY);
        mDays = Arrays.copyOf(parcelableArray,parcelableArray.length,Day[].class);

        // pass the data (mDays array) to the adapter via its constructor. Adapter will use
        // that data to display in ListView
        DayAdapter dayAdapter = new DayAdapter(this, mDays);
        setListAdapter(dayAdapter);
    }

    private void showToast() {
        Toast.makeText(this,"Swipe to Refresh event",Toast.LENGTH_SHORT).show();
        //mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mVibrator.vibrate(15);
        TextView mDetailsTextView = (TextView) v.findViewById(R.id.detailsTextView);
        RelativeLayout bottomRL = (RelativeLayout) v.findViewById(R.id.bottomRL);

        String pop = mDays[position].getPrecipChance() + "%";
        String summary = mDays[position].getSummary();
        String humidity = mDays[position].getHumidity() + "%";
        String windSpeed = mDays[position].getWindSpeed() + "";
        String day = mDays[position].getDayOfTheWeekAbbrev();

        String message = String.format("%s\nHumidity: %s \nPOP: %s \nWind: %s km/h",summary,humidity,pop,windSpeed);
        //Toast.makeText(this,message,Toast.LENGTH_LONG).show();

        if (previousPosition!=position)
            clickFlag = false;
        processPreviousRow(previousView);

        if (!clickFlag) {
            bottomRL.setPadding(10,40,0,40);
            bottomRL.setVisibility(View.VISIBLE);
            mDetailsTextView.setVisibility(View.VISIBLE);
            mDetailsTextView.setText(message);
            clickFlag = true;
        } else {
            bottomRL.setPadding(0,0,0,0);
            bottomRL.setVisibility(View.INVISIBLE);
            mDetailsTextView.setVisibility(View.INVISIBLE);
            mDetailsTextView.setText("");

            clickFlag = false;
        }
        previousView = v;
        previousPosition = position;
    }

    private void processPreviousRow(View previousView) {

        if (previousView!=null){


            TextView mDetailsTextView = (TextView) previousView.findViewById(R.id.detailsTextView);
            RelativeLayout bottomRL = (RelativeLayout) previousView.findViewById(R.id.bottomRL);

            bottomRL.setPadding(0,0,0,0);
            bottomRL.setVisibility(View.INVISIBLE);
            mDetailsTextView.setVisibility(View.INVISIBLE);
            mDetailsTextView.setText("");
            //this.recreate();
            previousView.invalidate();
            //this.getListView().draw();

        }

    }
}
