package com.sb.moviemarathon.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sb.moviemarathon.R;
import com.sb.moviemarathon.activities.SelectActivity;
import com.sb.moviemarathon.models.Theatre;

import java.util.List;


/**
 * Created by atam on 7/18/2014.
 */
public class TheatreAdapter extends ArrayAdapter<Theatre> {

    private static class ViewHolder {
        //String tID;
        TextView tID;
        TextView name;
        TextView addr;
        TextView proximity;
        TextView phoneNo;
    }


    public interface TheatreListDialogListener {
        void onFinishTheatreListDialog(String theatreId);
    }


    public TheatreAdapter(Context context, List<Theatre> theatres) {
        super(context, R.layout.theatre_item, theatres);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Theatre theatre = getItem(position);

        boolean debug;

        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.theatre_item, parent, false);

            //viewHolder.tID = theatre.getId();

            viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.addr = (TextView) convertView.findViewById(R.id.tvAddress);
            viewHolder.proximity = (TextView) convertView.findViewById((R.id.tvProximity));
            viewHolder.phoneNo = (TextView) convertView.findViewById(R.id.tvPhone);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // sometimes this gives NPE, improve deserialization exception handling!
        String distance = theatre.getDistanceFrom().toString();

        viewHolder.name.setText(theatre.getName());
        viewHolder.addr.setText(theatre.getAddress());
        viewHolder.proximity.setText(theatre.getDistanceFrom().toString().substring(0,4) +
                                     " mi away");
        viewHolder.phoneNo.setText(theatre.getPhoneNo());

        final View cv = convertView;

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder selectedItem = (ViewHolder) cv.getTag();
                //String selectedId = selectedItem.tID;
                String selectedId = theatre.getId();
                Log.d("MY_DEBUG", "In TheatreAdapter's anon class's callback onClick");
                Log.d("MY_DEBUG", "Still in onItemClick; the selectedTheatreID is " + selectedId);
                // fragments dont have their own context, get Activity fragment belongs to
                Context context = getContext();
                TheatreListDialogListener listener = (TheatreListDialogListener) context;
                listener.onFinishTheatreListDialog(selectedId);
                SelectActivity activity = (SelectActivity) context;
                activity.getTheatreListDialog().dismiss();
            }
        });

        if(position%2 != 0) {
            convertView.setBackgroundColor(0xff7292c5);
            viewHolder.name.setTextColor(Color.WHITE);
            viewHolder.addr.setTextColor(Color.WHITE);
            viewHolder.proximity.setTextColor(Color.WHITE);
            viewHolder.phoneNo.setTextColor(Color.WHITE);
        }
        else {
            convertView.setBackgroundColor(0xff7ea3df);
            viewHolder.name.setTextColor(Color.WHITE);
            viewHolder.addr.setTextColor(Color.WHITE);
            viewHolder.proximity.setTextColor(Color.WHITE);
            viewHolder.phoneNo.setTextColor(Color.WHITE);
        }

        return convertView;
    }
}
