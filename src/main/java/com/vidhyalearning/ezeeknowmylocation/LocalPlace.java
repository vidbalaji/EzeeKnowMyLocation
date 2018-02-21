package com.vidhyalearning.ezeeknowmylocation;

import com.google.android.gms.maps.model.Marker;

import java.util.Comparator;

/**
 * Created by user on 29-Jan-18.
 */

class LocalPlace {
    private String name,lng,lat,placeId,vicinity,rating;
    Marker marker;

    public static String gmapskey = "xxxx";
    private String distance;

    public void setName(String name) {
        this.name = name;
    }

    public void setlng(String lng) {
        this.lng = lng;
    }

    public void setlat(String lat) {
        this.lat = lat;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getName() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public String getRating() {
        return rating;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }

    public Marker getMarker() {
        return marker;
    }

    public String getPlaceId() {
        return placeId;
    }

    public static Comparator<LocalPlace> distCompare = new Comparator<LocalPlace>() {

        public int compare(LocalPlace s1, LocalPlace s2) {
            String tempS1 = s1.getDistance().replace("Km","").trim();
            String tempS2 = s2.getDistance().replace("Km","").trim();
            float availDist1 = Float.parseFloat(tempS1);
            float availDist2 = Float.parseFloat(tempS2);
            float distDiff = (availDist1-availDist2);
	   /*For ascending order*/
            return (int)distDiff;
        }};
}
