package com.sb.moviemarathon.helpers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by atam on 7/29/2014.
 */
public class MovieNode implements Parcelable {


    //private static final long serialVersionUID = 5177222050535318633L;
    private String title;
    private String showtime;
    private String runtime;
    private String imageUrl;
    private String trailerUrl;
    private String rootId;
    ArrayList<MovieNode> children;

    public MovieNode(String title, String showtime, String runtime, String imageUrl,
                     String trailerUrl, String rootId) {
        this.title = title;
        this.showtime = showtime;
        this.runtime = runtime;
        this.imageUrl = imageUrl;
        this.children = new ArrayList<MovieNode>();
        this.trailerUrl = trailerUrl;
        this.rootId = rootId;
    }

    public String getTitle() {
        return title;
    }

    public String getShowtime() {
        return showtime;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<MovieNode> getChildren() {
        return children;
    }

    public String getTrailerUrl() { return trailerUrl; }

    public String getRootId() { return rootId; }


    public boolean equals(MovieNode anotherNode) {
        if((!title.equals(anotherNode.getTitle()) &&
                showtime.equals(anotherNode.getShowtime()) &&
                runtime.equals(anotherNode.getRuntime()) &&
                imageUrl.equals(anotherNode.getImageUrl()) &&
                trailerUrl.equals(anotherNode.getTrailerUrl()))) {
            return false;
        }
        if(children.size() != anotherNode.getChildren().size()) {
            return false;
        }
        for(int i=0; i < children.size(); i++) {
            MovieNode currChild = children.get(i);
            MovieNode anotherCurrChild = anotherNode.getChildren().get(i);
            if(currChild == null || anotherCurrChild == null) {
                if(currChild != anotherCurrChild)
                    return false;
            }
            else {
                if(!currChild.equals(anotherCurrChild))
                    return false;
            }

        }
        // passed all individual equality tests, return true
        return true;
    }


    protected MovieNode(Parcel in) {
        title = in.readString();
        showtime = in.readString();
        runtime = in.readString();
        imageUrl = in.readString();
        trailerUrl = in.readString();
        rootId = in.readString();
        if (in.readByte() == 0x01) {
            children = new ArrayList<MovieNode>();
            in.readList(children, MovieNode.class.getClassLoader());
        } else {
            children = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(showtime);
        dest.writeString(runtime);
        dest.writeString(imageUrl);
        dest.writeString(trailerUrl);
        dest.writeString(rootId);
        if (children == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(children);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieNode> CREATOR = new Parcelable.Creator<MovieNode>() {
        @Override
        public MovieNode createFromParcel(Parcel in) {
            return new MovieNode(in);
        }

        @Override
        public MovieNode[] newArray(int size) {
            return new MovieNode[size];
        }
    };
}