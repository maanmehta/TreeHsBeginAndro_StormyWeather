package mun.treehsbeginandro_stormy.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import mun.treehsbeginandro_stormy.R;
import mun.treehsbeginandro_stormy.weather.Hour;

public class HourAdapter  extends RecyclerView.Adapter<HourAdapter.HourViewHolder>{

    Hour[] mHours;

    public HourAdapter(Hour[] hours){
        mHours = hours;
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // inflate view object from the item layout xml
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_list_item,parent,false);

        //create and return a new ViewHolder object for this view

        return new HourViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HourViewHolder holder, int position) {
        //get the hour object from mHours array based on the current row position
        Hour theHour = mHours[position];

        //call the bindHour method of our viewHolder which takes the hour object and maps
        // its data to the view items
        holder.bindHourdataToView(theHour);
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }

    public class HourViewHolder extends RecyclerView.ViewHolder {

        TextView mHourValueTextView;
        ImageView mIconHourImageView;
        TextView mHourSummaryTextView;
        TextView mHourTempTextView;

        public HourViewHolder(View itemView) {
            super(itemView);

            mHourValueTextView = (TextView) itemView.findViewById(R.id.hourValueTextView);
            mIconHourImageView = (ImageView) itemView.findViewById(R.id.iconHourImageView);
            mHourSummaryTextView = (TextView) itemView.findViewById(R.id.hourSummaryTextView);
            mHourTempTextView = (TextView) itemView.findViewById(R.id.hourTempTextView);
        }

        // this method binds or maps the data in the hour object to the view items
        public void bindHourdataToView (Hour hour){
            mHourValueTextView.setText(hour.getHour());
            mIconHourImageView.setImageResource(hour.getIconId());
            mHourSummaryTextView.setText(hour.getSummary());
            mHourTempTextView.setText(hour.getTemperature()+"");
        }
    }
}
