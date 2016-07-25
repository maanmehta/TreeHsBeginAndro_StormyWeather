package mun.treehsbeginandro_stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mun.treehsbeginandro_stormy.R;
import mun.treehsbeginandro_stormy.weather.Day;

public class DayAdapter extends BaseAdapter{

    private Context mContext;
    private Day[] mDays;

    // this constructor is called in the activities onCreate method
    // and mDays array data is passed in. Context is the activity object
    public DayAdapter(Context context, Day[] days){
        mContext = context;
        mDays = days;
    }

    @Override
    public int getCount() {
        // return the length of the mDays array
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        // return the item from the mDays array for the passed in position int
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // if convertView is null it means, it is brand new view,
            // so inflate view for the ROW item from the custom list layout for the list ITEM we created
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item,null);

            //now that we have inflated convertView from our custom layout, lets store or hold
            //this view in a view holder (design pattern) to reuse it later and not recreate it
            // this will help in performance when scrolling lists and avoid recreating
            // of new view objects for each row upon scrolling but reuse existing view using
            // viewholder
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconDayImageView);
            holder.maxTempValueTextView = (TextView) convertView.findViewById(R.id.dayMaxTempTextView);
            holder.minTempValueTextView = (TextView) convertView.findViewById(R.id.dayMinTempTextView);
            holder.dayNameTextView = (TextView) convertView.findViewById(R.id.dayNameTextView);

            // store this holder instance as a tag in the (convert) view (reused or recycled view
            // when scrolled) this can be to used when it is not null in the else block below
            // so we dont have use the
            // repeat use of findViewById to get view items from layout again as we can reuse it
            // better for performance
            convertView.setTag(holder);

        } else {
            // since convertView is not null, it means it is an existing view,
            // so reuse an existing view and reuse its view holder which is saved in its tag
            // instead of creating a new one
            // This is in case of scrolling and use an old existing view
            holder = (ViewHolder) convertView.getTag();
        }

        // Now that we have an empty instance of the holder, we set the values the view objects
        // in ViewHolder using our
        // actual data in our model object - Day. Since mDays array was passed into the adapter
        // in its constructor, we can get the actual Day object from the mDays array via position

        Day day = mDays[position];
        holder.iconImageView.setImageResource(day.getIconId());
        holder.maxTempValueTextView.setText(day.getTemperatureMax() + "");
        holder.minTempValueTextView.setText(day.getTemperatureMin() + "");

        if (position==0){
            holder.dayNameTextView.setText("Today");
        } else
        holder.dayNameTextView.setText(day.getDate());

        return convertView;
    }

    // Create ViewHolder class for this adapter.
    // View Holder class will have properties corresponding to each Item in the row of the list
    private static class ViewHolder {
        ImageView iconImageView;
        TextView maxTempValueTextView;
        TextView minTempValueTextView;
        TextView dayNameTextView;
    }
}
