package com.sb.moviemarathon.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sb.moviemarathon.helpers.TMSclient;

public class Movie implements Serializable {
    String title;
    ArrayList<String> showtimes;
    // running time of one movie in string format, "01:30"(1 hr, 30 min)
    String duration;
    // running time of one movie in minutes
    int runningTime;
    boolean selected;
    String synopsis;
    String selectedShowtime;
    String imageUrl;
    // useless, trailerUrls are generated dynamically through a separate GET
    String trailerUrl;
    String rating;
    String topCast;
    String rootID;
    
    TMSclient apiClient;
    private static final String IMG_BASE = "http://developer.tmsimg.com/";
    private static final String STATIC_PARAM="?api_key=kxzkmj2vnxa9fghxfc6uenpf";
    
    public Movie(String imgUrl, String trailerUrl, String time, String duration, String rootID) {
        imageUrl = imgUrl;
        this.trailerUrl = trailerUrl;
        // this is bad, refactor later
        rating = time;
        this.duration = duration;
        this.rootID = rootID;
    }


    public Movie(String movieName, ArrayList<String> showings, String duration,
    		     int numMins, boolean checked, String longDescription, 
    		     String selectedTime, String rating, String topCast, String imgUrl, String rID) {
        title = movieName;
        showtimes = showings;
        this.duration = duration;
        runningTime = numMins;
        selected = checked;
        synopsis = longDescription;
        selectedShowtime = selectedTime;
        // no access to api endpt for images or trailers, so hardcode
        // VERIFY SPELLING AND CAPITALIZATION OF THESE TITLES!!!!!!!!!!!!
        
        this.rating = rating;
        this.topCast = topCast;
        this.imageUrl = imgUrl;
        this.apiClient = new TMSclient();

        rootID = rID;
    }
    
    public void setSelectedShowtime(long selectedShowtimeMs) {
    	Date time = new Date(selectedShowtimeMs);
    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    	
    	selectedShowtime = dateFormat.format(time);
    }

	public String getTitle() {
		boolean breakPt = false;
		if(title == null)
			breakPt = true;
    	return title;
    }
	
	public ArrayList<String> getShowtimes() {  
	    return showtimes;
	}
	
	public String getDuration() {
		return duration;
	}
    
	public int getRunningTime() {
		return runningTime;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public String getSynopsis() {
		return synopsis;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public String getTrailerUrl() {
		return trailerUrl;
	}
	
	public String getSelected() {
		return selectedShowtime;
	}
	
	public String getTopCast() {
		return topCast;
	}
	
	public String getRating() {
		return rating;
	}

    public String getRootID() { return rootID; }
	
	public void setSelected(boolean isSelected) {
		selected = isSelected;
	}
	
	public static String returnTheatreId(JSONArray jsonArr) {
		JSONObject theatreJSON;
		String theatreId = null;
		try {
			theatreJSON = (JSONObject)jsonArr.get(0);
		    theatreId = theatreJSON.getString("theatreId");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return theatreId;
	}

    public static String returnTrailerUrl(JSONObject trailerObj) {
        String trailerUrl = "";
        try {
            JSONObject resp = trailerObj.getJSONObject("response");
            JSONArray trailers = resp.getJSONArray("trailers");
            JSONObject tObj = (JSONObject)trailers.get(0);
            trailerUrl = tObj.getString("Url");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailerUrl;
    }
	
	public static Movie fromJson(JSONObject jsonObj) {
		Movie movie = null;
		try {
		    String movieTitle = jsonObj.getString("title");

		    String longDescription = "";
		    String rating="";
		    String imgUrl="";
            String rootid = "";
		    try {
                /* skip one, do the others? */
		        longDescription = jsonObj.getString("longDescription");
		        JSONArray ratingsJSON = jsonObj.getJSONArray("ratings");
		        JSONObject ratingsJsonObj = ratingsJSON.getJSONObject(0);
			    rating = ratingsJsonObj.getString("code");
			    
			    JSONObject imgObj = jsonObj.getJSONObject("preferredImage");
			    imgUrl = IMG_BASE + imgObj.getString("uri") + STATIC_PARAM;
			    rootid = jsonObj.getString("rootId");
		    }
		    catch(Exception e) {
                String errMsg = e.getMessage();
                if(errMsg.contains("longDescription")) {
                    longDescription = "Description Unavailable";

                    JSONArray ratingsJSON = jsonObj.getJSONArray("ratings");
                    JSONObject ratingsJsonObj = ratingsJSON.getJSONObject(0);
                    rating = ratingsJsonObj.getString("code");

                    JSONObject imgObj = jsonObj.getJSONObject("preferredImage");
                    imgUrl = IMG_BASE + imgObj.getString("uri") + STATIC_PARAM;
                    rootid = jsonObj.getString("rootId");
                }
                else if(errMsg.contains("ratings")) {
                    rating = "Rating Unavailable";

                    JSONObject imgObj = jsonObj.getJSONObject("preferredImage");
                    imgUrl = IMG_BASE + imgObj.getString("uri") + STATIC_PARAM;
                    rootid = jsonObj.getString("rootId");
                }
                else if(errMsg.contains("preferredImage")) {
                    imgUrl = IMG_BASE + "movies/generic/generic_movies_v5.png" + STATIC_PARAM;
                }

		    }
		    // JSONArray doesnt have to carry just json objects
		    JSONArray topCastArr = jsonObj.getJSONArray("topCast");
		    String topCastStr="";
		    for(int i=0; i < topCastArr.length(); i++) {
		    	String castMember = topCastArr.getString(i);
		    	topCastStr+=castMember;
		    	if(i != topCastArr.length()-1)
		    	    topCastStr+=", ";
		    }
		    
    	    String movieDurationStr = jsonObj.getString("runTime");
    	    movieDurationStr = formatDuration(movieDurationStr);
    	    int movieLength = convertRuntime(movieDurationStr);
    	    JSONArray showtimesJSON = jsonObj.getJSONArray("showtimes");
    	
    	    ArrayList<String> showtimes = new ArrayList<String>();
    	    for(int j=0; j < showtimesJSON.length(); j++) {
    		    JSONObject currentShowtimeJSON = showtimesJSON.getJSONObject(j);
    		
    		    String showtimeWithDate = currentShowtimeJSON.getString("dateTime");
    		    //String showtime = showtimeWithDate.substring(11);
    		    showtimes.add(showtimeWithDate);
    	    }
    	    movie = new Movie(movieTitle, showtimes, movieDurationStr, 
    	    		          movieLength, false, longDescription, null, 
    	    		          rating, topCastStr, imgUrl, rootid);
    	    
    	}
		catch (Exception e) {
			e.printStackTrace();
		}
		return movie;
	}
	
	
	// return list of movies with json data filled in
	public static ArrayList<Movie> fromJson(JSONArray jsonArr) {
		JSONObject currentMovieJSON;
		ArrayList<Movie> movies = new ArrayList<Movie>();
		try {
            for(int i=0; i < jsonArr.length(); i++) {
            	// get json and fill the current Movie object
            	currentMovieJSON = jsonArr.getJSONObject(i);
            	// got json obj for current movie, deserialize into Movie obj
          	    Movie currentMovie= Movie.fromJson(currentMovieJSON);
          	    // build list of movies
          	    if(currentMovie != null)
            	    movies.add(currentMovie);
            }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return movies;	
	}
	
	
    private static int convertRuntime(String runtimeStr) {
    	String numHrs = runtimeStr.substring(0,2);
    	int numHrsint = Integer.parseInt(numHrs);
		
    	String numMins = runtimeStr.substring(3,5);
    	int numMinsint = Integer.parseInt(numMins);
    	
		return numHrsint*60 + numMinsint;
    }
    
    // format runTime from json into hh:mm string
	private static String formatDuration(String duration) {
		// TODO Auto-generated method stub
		String numHrs = duration.substring(2, 4);
		String numMins = duration.substring(5,7);

		String formattedDuration = numHrs + ":" + numMins;
		return formattedDuration;
	}
}
