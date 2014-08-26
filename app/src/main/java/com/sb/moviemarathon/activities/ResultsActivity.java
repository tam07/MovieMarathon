package com.sb.moviemarathon.activities;

import java.util.ArrayList;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sb.moviemarathon.R;
import com.sb.moviemarathon.adapters.ResultsAdapter;
import com.sb.moviemarathon.helpers.TMSclient;
import com.sb.moviemarathon.fragments.MyAlertDialogFragment;
import com.sb.moviemarathon.models.Movie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResultsActivity extends FragmentActivity {
    ListView lvSchedules;

    ArrayList<ArrayList<Movie>> itineraries;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		//itineraries = (ArrayList<ArrayList<MovieNode>>) getIntent().getSerializableExtra("results");
		lvSchedules = (ListView)findViewById(R.id.lvResults);
        //itineraries = (ArrayList<ArrayList<MovieNode>>) getIntent().getSerializableExtra("results");
        itineraries = (ArrayList<ArrayList<Movie>>) getIntent().getSerializableExtra("results");
        ResultsAdapter resultsAdapter = new ResultsAdapter(this, itineraries);
        lvSchedules.setAdapter(resultsAdapter);
	}

    @Override
    protected void onResume() {
        super.onResume();
        boolean debug_flag = true;
        /*itineraries = (ArrayList<ArrayList<MovieNode>>) getIntent().getSerializableExtra("results");
        ResultsAdapter resultsAdapter = new ResultsAdapter(this, itineraries);
        lvSchedules.setAdapter(resultsAdapter);*/
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    public void playTrailer(View v) {
        final Movie selectedMovie = (Movie) v.getTag();
        String selectedID = selectedMovie.getRootID();

        TMSclient.getTrailerUrl(selectedID, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject trailerObj) {
                        // keeping movie json processing with movie class as good OOP practice
                        String trailerUrl = Movie.returnTrailerUrl(trailerObj);
                        if (trailerUrl.equals("")) {
                            FragmentManager fm = getSupportFragmentManager();
                            MyAlertDialogFragment noTrailerDialog =
                                    MyAlertDialogFragment.newInstance("Trailer Unavailable",
                                            "Trailer not found for " +
                                                    selectedMovie.getTitle()
                                    );
                            noTrailerDialog.show(fm, "no_trailer_dialog_tag");
                        } else {
                            /*Intent implicitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                            startActivity(implicitIntent);*/

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setDataAndType(Uri.parse(trailerUrl), "video/mp4");
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onFailure(Throwable arg0, JSONArray arg1) {
                        super.onFailure(arg0, arg1);
                        Toast.makeText(getApplicationContext(),
                                "Failed with JSONArray 2nd arg!", Toast.LENGTH_LONG).show();
                    }

                    // If it fails it fails here where arg1 is the error message(dev inactive)
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        Toast.makeText(getApplicationContext(),
                                "getTrailerUrl failed with String 2nd arg!",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);
                        Toast.makeText(getApplicationContext(),
                                "Failed with JSONObject 2nd arg!", Toast.LENGTH_LONG).show();
                    }

                }
        );
    }
}
