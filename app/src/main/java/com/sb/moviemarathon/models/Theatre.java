package com.sb.moviemarathon.models;

import java.io.Serializable;
import java.util.ArrayList;

import com.sb.moviemarathon.helpers.TMSclient;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by atam on 7/17/2014.
 */
public class Theatre implements Serializable {
    // used for call to get showtimes in Activity
    String id;
    // used to display in dialogFragment
    String name;
    String address;
    Double distanceFrom;
    String phoneNo;
    // used to make REST calls
    TMSclient connector;

    public Theatre(String tID, String tName, String addr, double dist, String phone) {
        id = tID;
        name = tName;
        address = addr;
        distanceFrom = dist;
        phoneNo = phone;
    }


    // API explorer shows json resp is a JSONArray
    public static ArrayList<Theatre> fromJson(JSONArray jsonArr) {
        JSONObject currentTheatreJSON;
        ArrayList<Theatre> theatres = new ArrayList<Theatre>();
        try {
            for(int i=0; i < jsonArr.length(); i++) {
                currentTheatreJSON = jsonArr.getJSONObject(i);
                // dont have a theatre obj yet, so make method static
                Theatre currentTheatre = Theatre.fromJson(currentTheatreJSON);
                theatres.add(currentTheatre);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return theatres;
    }


    private static Theatre fromJson(JSONObject currentTheatreJSON) {
        Theatre theatre = null;
        try {
            String tID = currentTheatreJSON.getString("theatreId");
            String tName = currentTheatreJSON.getString("name");

            JSONObject tLoc = currentTheatreJSON.getJSONObject("location");
            double tDist = tLoc.getDouble("distance");
            JSONObject tAddr = tLoc.getJSONObject("address");
            String tStreet = tAddr.getString("street");
            String tCity = tAddr.getString("city");
            String tAddrStr = tStreet + ", " + tCity;
            String telephone = tLoc.getString("telephone");

            theatre = new Theatre(tID, tName, tAddrStr, tDist, telephone);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return theatre;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Double getDistanceFrom() {
        return distanceFrom;
    }

    public String getPhoneNo() {
        return phoneNo;
    }
}
