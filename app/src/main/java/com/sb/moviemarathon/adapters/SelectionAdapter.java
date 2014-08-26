package com.sb.moviemarathon.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sb.android.widget.NonScrollableGridView;

import com.sb.moviemarathon.R;
import com.sb.moviemarathon.models.*;

public class SelectionAdapter extends ArrayAdapter<Movie> {

    private final List<Movie> allMovies;
    private final ArrayList<Movie> selectedMovies;
    private Context c;
    private LayoutInflater inflater;

    private static class ViewHolder {
        CheckBox cb;
        ImageView posterIV;
        ImageView playBtnIV;
        TextView movieTitleTV;
        TextView castTV;
        TextView ratingTV;
        TextView runtimeTV;
        NonScrollableGridView showtimesNSGV;
    }
    
	public SelectionAdapter(Context context, List<Movie> movieOptions) {
		super(context, 0, movieOptions);
        allMovies = movieOptions;
        selectedMovies = new ArrayList<Movie>();
        c = context;

	}
	

    public ArrayList<Movie> getSelectedMovies() {
        return selectedMovies;
    }


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        final Movie movie = getItem(position);

        final ViewHolder viewHolder;
        /* convertView will be null if convertView isn't from recycler
         * the last earlier row isn't totally out of view when scrolling
         */
		if(convertView == null) {
            Log.d("ADAPTER", "convertView is null; Position=" + position + " and " +
                  " movie=" + movie.getTitle());
			LayoutInflater inflater = LayoutInflater.from(getContext());

			convertView = inflater.inflate(R.layout.selection_item, parent, false);

            // inflate individual views and cache them into viewholder
            viewHolder = new ViewHolder();

            viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            viewHolder.cb.setChecked(movie.isSelected());

            viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();

                    //allMovies.get(getPosition).setSelected(buttonView.isChecked());
                    Movie selectedMovie = allMovies.get(getPosition);
                    selectedMovie.setSelected(buttonView.isChecked());
                    //movie.setSelected(buttonView.isChecked());
                    Log.d("SELECT", "onCheckChanged, " + selectedMovie.getTitle() + " is " + buttonView.isChecked());
                }
            });

            viewHolder.posterIV = (ImageView) convertView.findViewById((R.id.ivMoviePoster));
            viewHolder.playBtnIV = (ImageView) convertView.findViewById((R.id.ivPlayBtn));
            viewHolder.movieTitleTV = (TextView) convertView.findViewById(R.id.movie_title);
            viewHolder.castTV = (TextView) convertView.findViewById(R.id.topCast);
            viewHolder.ratingTV = (TextView) convertView.findViewById((R.id.rating));
            viewHolder.runtimeTV = (TextView) convertView.findViewById(R.id.movlength);
            viewHolder.showtimesNSGV = (NonScrollableGridView) convertView.findViewById(R.id.showtimes);

            // TAG - associate view memory with these views
            convertView.setTag(viewHolder);

            // TAG - associate view memory with checkbox, unnecessary
            convertView.setTag(R.id.cb, viewHolder.cb);

		}
        else {
            Log.d("SELECT_ADAPTER", "Reusing convertView, position=" + position + "and " +
                   " movie=" + movie.getTitle());
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // AND TAG HERE
        viewHolder.cb.setTag(position);

        viewHolder.cb.setChecked(allMovies.get(position).isSelected());

        // associate play button with movie so listener callback knows which trailer to launch
        viewHolder.playBtnIV.setTag(movie);

        // set values in views
        ImageLoader.getInstance().displayImage(movie.getImageUrl(), viewHolder.posterIV);
        viewHolder.movieTitleTV.setText(movie.getTitle());
        viewHolder.castTV.setText(movie.getTopCast());
        viewHolder.ratingTV.setText(movie.getRating());
        viewHolder.runtimeTV.setText(movie.getDuration());

		ArrayList<String> showtimes = movie.getShowtimes();
		showtimes = getTwelveHrShowtimes(showtimes);
        ArrayAdapter<String> gridAdapter = new ArrayAdapter<String>(getContext(), R.layout.gridview_item, showtimes);
		viewHolder.showtimesNSGV.setAdapter(gridAdapter);

		return convertView;
	}


    public ArrayList<String> getTwelveHrShowtimes(ArrayList<String> TwentyFourHrShowtimes) {
            ArrayList<String> twelveHrShowtimes = new ArrayList<String>();
            for(String twentyFourHrShowtime: TwentyFourHrShowtimes) {
			//SimpleDateFormat df24 = new SimpleDateFormat("HH:mm");
            SimpleDateFormat df24 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			SimpleDateFormat df12 = new SimpleDateFormat("h:mma");
			try {
				Date twentyFourHrDate = df24.parse(twentyFourHrShowtime);
				String twelveHrShowtime = df12.format(twentyFourHrDate).toLowerCase();
				twelveHrShowtimes.add(twelveHrShowtime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} // end iteration of showtimes in 24-hr time
		return twelveHrShowtimes;
	}
}