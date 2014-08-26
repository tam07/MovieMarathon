package com.sb.moviemarathon.helpers;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.loopj.android.http.*;

public class TMSclient {
  private static final String BASE_URL = "http://data.tmsapi.com/";
  private static final String TOKEN = "";
  private static final String NUM_THEATRES = "17";
  private static final String POSTER_SIZE = "Md";
  private static final String TRAILER_FORMAT_ID = "449";
  private static final String TRAILERS_ONLY_FLAG = "1";
  private static final String TRAILERS_ENDPT = "v1/screenplayTrailers";
  private static AsyncHttpClient client = new AsyncHttpClient();
  
  private static final String IMG_SIZE="Sm";

  public static void findTheatres(Double lat, Double lon, AsyncHttpResponseHandler handler) {
	  RequestParams params = new RequestParams();
	  String latStr = lat.toString();
      String lonStr = lon.toString();
      params.put("lat", latStr);
      params.put("lng", lonStr);
      params.put("numTheatres", NUM_THEATRES);
      params.put("api_key", TOKEN);
      
      String findTheatreEndpoint = "v1/theatres";
      get(findTheatreEndpoint, params, handler);

  }
  
  // AMC Metreon 16 is theatreId 7641
  public static void getTheatreShowtimes(String theatreId, AsyncHttpResponseHandler handler) {
	  RequestParams parameters = new RequestParams();
	  
	  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	  String today = dateFormat.format(new Date());

	  parameters.put("startDate", today);
	  parameters.put("api_key", TOKEN);
	  parameters.put("imageSize", POSTER_SIZE);
	  String getShowtimesEndpoint = "v1/theatres/" + theatreId + "/showings";
	  get(getShowtimesEndpoint, parameters, handler);
  }

  public static void getTrailerUrl(String rootId, AsyncHttpResponseHandler handler) {
      RequestParams parameters = new RequestParams();
      parameters.put("rootids", rootId);
      parameters.put("bitrateids", TRAILER_FORMAT_ID);
      parameters.put("trailersonly", TRAILERS_ONLY_FLAG);
      parameters.put("api_key", TOKEN);

      get(TRAILERS_ENDPT, parameters, handler);
  }

  
  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	  String absoluteUrl = getAbsoluteUrl(url);
	  String fullRequestStr = client.getUrlWithQueryString(absoluteUrl, params);
	  boolean debugPt = false;
	  if(fullRequestStr.contains("screenplayTrailers")) {
		  debugPt = true;
	  }
	  else {
		  debugPt = false;
	  }
      
	  client.get(absoluteUrl, params, responseHandler);
  }

  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
      client.post(getAbsoluteUrl(url), params, responseHandler);
  }

  /*public static void post(AsyncHttpResponseHandler handler) {
      RequestParams parameters = new RequestParams();
      parameters.put("Authorization", "Basic V0tENE43WU1BMXVpTThWOkR0ZFR0ek1MUWxBMGhrMkMxWWk1cEx5VklsQVE2OA==");
      parameters.put("AppGlu-Environment", "staging");
      parameters.put("stopname", "Mauro Ramos");
      client.post("https://dashboard.appglu.com/v1/queries/findRoutesByStopName/run",  )
  }*/

  private static String getAbsoluteUrl(String relativeUrl) {
      return BASE_URL + relativeUrl;
  }
}