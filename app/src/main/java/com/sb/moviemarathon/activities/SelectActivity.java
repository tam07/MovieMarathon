package com.sb.moviemarathon.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sb.moviemarathon.R;
import com.sb.moviemarathon.adapters.SelectionAdapter;
import com.sb.moviemarathon.helpers.TMSclient;
import com.sb.moviemarathon.fragments.TheatreListDialog;
import com.sb.moviemarathon.fragments.MyAlertDialogFragment;
import com.sb.moviemarathon.helpers.MovieNode;
import com.sb.moviemarathon.models.Movie;

import android.app.Dialog;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.sb.moviemarathon.adapters.TheatreAdapter.*;

public class SelectActivity extends FragmentActivity implements
                                    GooglePlayServicesClient.ConnectionCallbacks,
                                    GooglePlayServicesClient.OnConnectionFailedListener,
                                    TheatreListDialogListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int BUFFER_MINS = 30;

    private static final int REQUEST_CODE = 50;
    // zip code of riviera, leads to amc metreon 16, theatreId=7641
    private String currZip;
    // wait time of 20 minutes in milliseconds
    private static final long WAIT = 1200000;

    ArrayList<Movie> movies;
    ArrayList<Movie> selectedMovies = new ArrayList<Movie>();

    TextView waitMsg;
    ProgressBar spinWait;
    ListView listView;
    Button linkBtn;

    LocationClient mLocationClient;
    Location mCurrentLocation;

    private String selectedTheatreID;

    private DialogFragment theatreListDialog;

    private boolean theatreDialogShown;

    ArrayList<ArrayList<Movie>> schedules;

    // placeholder for deleted data structure declaration

    public DialogFragment getTheatreListDialog() {
        return theatreListDialog;
    }


    private String getZipCodeFromLocation(Location location) {
        Address addr = getAddressFromLocation(location);
        String zipcode = addr.getPostalCode();
        if (zipcode == null)
            return "could not get zipcode from Address object!";
        else
            return zipcode;
    }

    private Address getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this);
        Address address = new Address(Locale.getDefault());
        try {
            List<Address> addr = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addr.size() > 0) {
                address = addr.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    /* Called by Location services after the client is connected.  Now you can get the
     * current location
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        Log.d("MY_DEBUG", "In SelectActivity's onConnected.  Successfully connected to loc services");
        mCurrentLocation = mLocationClient.getLastLocation();
        //currZip = getZipCodeFromLocation(mCurrentLocation);
        // Display the connection status
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        /* first case is if user presses home before selecting a theatre.  Reopening app we want
           to show the theatre dialog then.
         */
        if (movies == null) {
            showTheatreListingDialog();
        }
    }


    // A theatre was selected, theatreId from TheatreAdapter is passed to this callback
    @Override
    public void onFinishTheatreListDialog(String theatreId) {
        Log.d("MY_DEBUG", "In SelectActivity's onFinishTheatreListDialog.  theatreId=" + theatreId);

        /*Toast.makeText(this, "In SelectActivity's onFinishTheatreListDialog.  theatreId=" + theatreId,
                Toast.LENGTH_LONG).show();*/

        selectedTheatreID = theatreId;

        spinWait.setVisibility(View.VISIBLE);
        waitMsg.setText("Getting movie showtimes and info");
        // selectedMovies.clear();
        requestShowtimes(selectedTheatreID);
    }


    // Called by Location Services if conn to the location client drops because of an error
    @Override
    public void onDisconnected() {
        Log.d("MY_DEBUG", "In SelectActivity's onDisconnected.");
        Toast.makeText(this, "Location services disconnected.  Please refresh.", Toast.LENGTH_SHORT).show();
    }


    // Called by Location Services if the attempt to Location Services fails
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /* Google Play Services can resolve some errors it detects.
           If the error has a resolution, try sending an Intent to start a Google Play Services
           activity that can resolve the error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            /* If no resolution is available, display a dialog to the user with the error
               WHHERE IS THIS METHOD DEFINED?!

            showErrorDialog(connectionResult.getErrorCode()); */
        }
    }


    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // try the request again
                        break;
                }
        }
    }


    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        }
        /* where is the connectionResult variable defined?  getErrorCode is an instance method
           and can't be called in a static context!

        else {
            int errorCode = connectionResult.getErrorCode();
            // Get the error dialog from Google Play Services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this,
                                                                       CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play sevices can provide an error dialog
            if(errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }
        } */
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MY_DEBUG", "In SelectActivity's onCreate");
        super.onCreate(savedInstanceState);

        mLocationClient = new LocationClient(this, this, this);

        setContentView(R.layout.activity_select);
        setupViews();
    }


    private void showTheatreListingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        theatreListDialog = TheatreListDialog.newInstance("Select a Theatre:", mCurrentLocation);
        theatreListDialog.show(fm, "theatresDialogTag");
    }


    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }


    @Override
    protected void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }


    private void setupViews() {
        spinWait = (ProgressBar) findViewById(R.id.progressSpinner);
        waitMsg = (TextView) findViewById(R.id.waitMessage);

        listView = (ListView) findViewById(R.id.lvChoices);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        linkBtn = (Button) findViewById(R.id.calcBtn);
        linkBtn.setVisibility(View.GONE);
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


    /* Given a movie theatre(by id), request all the movie data and display in view via
	 * the adapter */
    // theatreId should be passed back from dialogFragment
    public void requestShowtimes(String theatreId) {
        //final SelectActivity current = this;
        // 7641, Metreon
        TMSclient.getTheatreShowtimes(theatreId, new JsonHttpResponseHandler() {

            @Override
			/* a showtime JSONObject is NOT just the showtimes, look carefully
			 * at json response */
            public void onSuccess(JSONArray movieObjects) {
                // make loading icon and message disappear
                spinWait.setVisibility(View.GONE);
                waitMsg.setVisibility(View.GONE);
                linkBtn.setVisibility(View.VISIBLE);

                movies = Movie.fromJson(movieObjects);
                if (movies.size() == 0) {
                    FragmentManager fm = getSupportFragmentManager();
                    MyAlertDialogFragment noShowtimesDialog =
                            MyAlertDialogFragment.newInstance("Showings Unavailable", "The service could " +
                                    "not find any movies for this theatre!");
                    noShowtimesDialog.show(fm, "no_showtimes_tag");
                    /* put in dialog's OK button callback
                    launchRefresh(null); */
                } else {
                    /*SelectionAdapter selectAdapter = new
                            SelectionAdapter(getBaseContext(), movies);*/
                    SelectionAdapter selectAdapter = new
                            SelectionAdapter(SelectActivity.this, movies);
                    listView.setAdapter(selectAdapter);
                }
            }

            @Override
            public void onFailure(Throwable arg0, JSONArray arg1) {
                super.onFailure(arg0, arg1);
            }

            @Override
            public void onFailure(Throwable arg0, String arg1) {
                super.onFailure(arg0, arg1);
            }

            @Override
            public void onFailure(Throwable arg0, JSONObject arg1) {
                super.onFailure(arg0, arg1);
            }
        });
    }

    // SET BREAKPOINT FOR THIS METHOD TO MAKE SURE REFRESH WORKS!!!
    public void launchRefresh(MenuItem menuItem) {
        Log.d("MY_DEBUG", "In SelectActivity's launchRefresh.");
        mCurrentLocation = mLocationClient.getLastLocation();
        // Display the connection status
        //Toast.makeText(this, "Current location is " + mCurrentLocation, Toast.LENGTH_LONG).show();
        showTheatreListingDialog();
    }


    /* when this activity resumes, clear out old schedules in case movies are selected and the
       algorithm is run again.
     */
    @Override
    public void onResume() {
        super.onResume();
        // schedules is null when creating the activity for first time, > 0 on back btn press
        if(schedules != null && schedules.size() > 0) {
            Log.d("SELECT", "in onResume after BACK pressed.  selectedMovies before being " +
                  "cleared:");
            for(Movie selected: selectedMovies) {
                Log.d("SELECT", selected.getTitle() + "\n");
            }
            schedules.clear();
            selectedMovies.clear();
        }
    }


    // The types specified here are the input data type, the progress type, and the result type
    private class MyAsyncTask extends AsyncTask<ArrayList<Movie>, Void, Void> {

        protected void onPreExecute() {
            // Runs on the UI thread before doInBackground
            // Good for toggling visibility of a progress indicator
            spinWait.setVisibility(ProgressBar.VISIBLE);
            waitMsg.setText("Calculating Schedules....");

            spinWait.bringToFront();
            waitMsg.bringToFront();
        }


        @Override
        protected Void doInBackground(ArrayList<Movie>... lolMovies) {
            // Some long-running task like downloading an image.
            //ArrayList<ArrayList<Movie>> temp = null;
            try {
                calcSchedules(lolMovies[0]);
                //return someBitmap;
            }
            catch(Exception e) {
                Log.d("DEBUG", "Calculating schedules failed, " + e.getMessage());
            }
            return null;
        }

        /*protected void onProgressUpdate(P) {
            // Executes whenever publishProgress is called from doInBackground
            // Used to update the progress indicator
            progressBar.setProgress(values[0]);
        }*/


        @Override
        protected void onPostExecute(Void v) {
            // This method is executed in the UIThread
            // with access to the result of the long running task
            // launch results activity

            // Hide the progress bar
            spinWait.setVisibility(View.GONE);
            waitMsg.setVisibility(View.GONE);

            // if schedules is empty, show error dialog
            if(schedules.size() == 0) {
                FragmentManager fm = getSupportFragmentManager();
                MyAlertDialogFragment noLinksDialog =
                        MyAlertDialogFragment.newInstance("No Connections",
                                "A schedule could not be generated for the selected movies."
                        );
                noLinksDialog.show(fm, "no_links_dialog_tag");
                selectedMovies.clear();
            }
            else {
                Intent i = new Intent(getBaseContext(), ResultsActivity.class);
                i.putExtra("results", schedules);

                startActivity(i);
            }
        }
    }


    public ArrayList<Movie> getSelectedMovies() {
        return selectedMovies;
    }


    // onClick method MUST HAVE "View v" as the param list!
    public void launchResultsView(View v) throws Exception {
        // gather the movies the user checked off
        for (int i = 0; i < movies.size(); i++) {
            Movie currMovie = movies.get(i);
            if (currMovie.isSelected()) {
                selectedMovies.add(currMovie);
            }
        }

        // if selectedMovies is null, pop up a error dialog saying "please select at least 2 movies"
        if(selectedMovies.size() <= 1) {
            FragmentManager fm = getSupportFragmentManager();
            MyAlertDialogFragment selectMoviesDialog =  MyAlertDialogFragment.newInstance("Insufficient selections",
                                                     "Please select at least 2 movies");
            selectMoviesDialog.show(fm, "select_movies_dialog_tag");
        }
        else
            new MyAsyncTask().execute(selectedMovies);
    }





    private String getNextShowtime(DateTime dt, ArrayList<String> showtimes) {
        for (String showtime : showtimes) {
            // "2014-07-30T13:00"
            DateTime showtimeDT = DateTime.parse(showtime,
                    DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm"));
            // if the showtime is before the current time, keep looking
            if (showtimeDT.isBefore(dt)) {
                continue;
            } else
                return showtime;
        }
        // no more screenings of this movie today
        return null;
    }





    private boolean inCurrentSchedule(String title, ArrayList<MovieNode> currentSchedule) {
        for(MovieNode mn: currentSchedule) {
            String mTitle = mn.getTitle();
            if(title.equals(mTitle))
                return true;
        }
        return false;
    }
}