package com.sb.moviemarathon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sb.moviemarathon.R;
import com.sb.moviemarathon.models.Movie;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Created by atam on 7/29/2014.
 */
public class ScheduleAdapter extends ArrayAdapter<Movie> {

    // cache for MovieNode views
    private static class ViewHolder {
        ImageView poster;
        ImageView play;
        TextView showtime;
        TextView runtime;
    }


    public ScheduleAdapter(Context context, List<Movie> aSchedule) {
        // result_item_item xml corresponds to MovieNode model.  aSchedule is multiple MovieNodes
        super(context, R.layout.result_item_item, aSchedule);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Movie movieInSched = getItem(position);

        // inflating individual views in a row/col is expensive, use viewholder cache
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.result_item_item, parent, false);

            // inflate xml views from result_item_item.xml
            viewHolder.poster = (ImageView) convertView.findViewById(R.id.ivPoster);
            viewHolder.play = (ImageView) convertView.findViewById(R.id.ivPlay);
            viewHolder.showtime = (TextView) convertView.findViewById(R.id.tvShowtime);
            viewHolder.runtime = (TextView) convertView.findViewById(R.id.tvRuntime);
            convertView.setTag(viewHolder);

            //viewHolder.play.setTag(movieInSched);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.play.setTag(movieInSched);
        // now set the values for the cached or new views in this col
        ImageLoader.getInstance().displayImage(movieInSched.getImageUrl(), viewHolder.poster);

        //viewHolder.showtime.setText(movieInSched.getShowtime());

        DateTimeFormatter before = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");
        DateTimeFormatter after = DateTimeFormat.forPattern("hh:mm a");
        DateTime dt24 = before.parseDateTime(movieInSched.getRating());
        String showtime12 = after.print(dt24);

        viewHolder.showtime.setText(showtime12);
        /*String runtime = movieInSched.getRuntime();
        String[] tokens = runtime.split(":");
        int numHrs;
        if(tokens[0].startsWith("0"))
            numHrs = Integer.parseInt(tokens[0].substring(1));
        else
            numHrs = Integer.parseInt(tokens[0]);

        int numMins = Integer.parseInt(tokens[1]);

        String readableRuntime = numHrs + " hours"*/
        viewHolder.runtime.setText(movieInSched.getDuration());

        return convertView;
    }

}
