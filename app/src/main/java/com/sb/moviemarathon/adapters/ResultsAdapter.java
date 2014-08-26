package com.sb.moviemarathon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import com.sb.moviemarathon.R;
import com.sb.moviemarathon.models.Movie;

/**
 * Created by atam on 7/29/2014.
 *
 * Link between listview and source of data.  getView is called each time to inflate each row
 */
public class ResultsAdapter extends ArrayAdapter<ArrayList<Movie>> {

    private static class ViewHolder {
        TwoWayView horizLV;
    }

    public ResultsAdapter(Context context, ArrayList<ArrayList<Movie>> schedules) {
        // result_item xml corresponds to type of ArrayAdapter, List<MovieNode>
        super(context, R.layout.result_item, schedules);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ArrayList<Movie> aSchedule = getItem(position);

        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.result_item, parent, false);
            //viewHolder.horizLV = (TwoWayView) convertView.findViewById(R.id.lvItems);
            viewHolder.horizLV = (TwoWayView) convertView.findViewById(R.id.lvItems);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ScheduleAdapter schedToCols = new ScheduleAdapter(getContext(), aSchedule);
        viewHolder.horizLV.setAdapter(schedToCols);

        return convertView;
    }
}
