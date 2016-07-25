package mun.treehsbeginandro_stormy.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Arrays;

import mun.treehsbeginandro_stormy.R;
import mun.treehsbeginandro_stormy.adapters.HourAdapter;
import mun.treehsbeginandro_stormy.weather.Hour;

public class HourlyForecastActivity extends AppCompatActivity {

    Hour[] mHours;

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        // get data (mDays array) from the intent
        Intent intent = getIntent();
        Parcelable[] parcelableArray = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST_KEY);
        mHours = Arrays.copyOf(parcelableArray,parcelableArray.length,Hour[].class);

        HourAdapter hourAdapter = new HourAdapter(mHours);
        mRecyclerView.setAdapter(hourAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        //best practice to set this boolean for lists that have fixed data set and does not
        // change later. In our case hourly forecast is set once retrieved
        mRecyclerView.setHasFixedSize(true);
    }
}
