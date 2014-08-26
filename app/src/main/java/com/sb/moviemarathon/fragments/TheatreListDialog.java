package com.sb.moviemarathon.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sb.moviemarathon.R;
import com.sb.moviemarathon.helpers.TMSclient;
import com.sb.moviemarathon.adapters.TheatreAdapter;
import com.sb.moviemarathon.models.Theatre;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by atam on 7/18/2014.
 */
public class TheatreListDialog extends DialogFragment {
    private ListView theatreLV;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public TheatreListDialog() {
        // empty ctor required for DialogFragment
    }


    public static TheatreListDialog newInstance(String title, Location currLoc) {
        Log.d("MY_DEBUG", "In TheatreListDialog's newInstance");
        Double latitude = currLoc.getLatitude();
        Double longitude = currLoc.getLongitude();

        TheatreListDialog frag = new TheatreListDialog();
        Bundle args = new Bundle();
        args.putString("title", title);

        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);

        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                          Bundle savedInstanceState) {
        Log.d("MY_DEBUG", "In TheatreListDialog's onCreateView");
        View view = inflater.inflate(R.layout.fragment_theatre_list, container);
        String dfTitle = getArguments().getString("title", "Default value");
        getDialog().setTitle(dfTitle);
        theatreLV = (ListView) view.findViewById(R.id.theatreLV);

        Double lat = getArguments().getDouble("latitude");
        Double lon = getArguments().getDouble("longitude");
        TMSclient.findTheatres(lat, lon, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONArray theatres) {
                        // keeping movie json processing with movie class as good OOP practice
                        ArrayList<Theatre> adapterSrc = Theatre.fromJson(theatres);

                        TheatreAdapter adapter = new TheatreAdapter(getActivity(),
                                adapterSrc);
                        theatreLV.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Throwable arg0, JSONArray arg1) {
                        // TODO Auto-generated method stub
                        super.onFailure(arg0, arg1);
                        Toast.makeText(getActivity(),
                                "TheatreListDialog's findTheatres failed with JSONArray 2nd arg!", Toast.LENGTH_LONG).show();
                    }

                    // If it fails it fails here where arg1 is the error message(dev inactive)
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        // TODO Auto-generated method stub
                        super.onFailure(arg0, arg1);
                        Toast.makeText(getActivity(),
                                "TheatreListDialog's findTheatres failed with String 2nd arg!," +
                                        "Error message: " + arg1, Toast.LENGTH_LONG).show();
                        Log.d("MY_DEBUG", "TheatreListDialog's findTheatres failed because " + arg1);
                    }

                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        // TODO Auto-generated method stub
                        super.onFailure(arg0, arg1);
                        Toast.makeText(getActivity(),
                                "TheatreListDialog's findTheatres failed with JSONObject 2nd arg!", Toast.LENGTH_LONG).show();
                    }
                }
        );

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }
}
