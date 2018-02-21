package com.vidhyalearning.ezeeknowmylocation;

/**
 * Created by user on 09-Jan-18.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.os.Build.VERSION_CODES.M;

public class CustomPagerAdapter extends FragmentStatePagerAdapter  {

    private Context mContext;
    int tabCount;
    double gLat=0,  gLon=0;
    MapsActivity.updateLocnListener listener =null;
    public CustomPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
        listener = new MapsActivity.updateLocnListener() {
            @Override
            public void setLocns(double lat, double lon) {
                gLat=lat;
                gLon=lon;
            }
        };


    }


    @Override
    public Fragment getItem(int position) {
        NewTab nt = new NewTab();
       Bundle bundl = new Bundle();
        bundl.putInt("POSITION", position);
        bundl.putDouble("LATITUDE", gLat);
        bundl.putDouble("LONGITUDE", gLon);
        Log.d("Lat/Lon" , gLat + " " + gLon);
        nt.setArguments(bundl);
        return nt;

    }

    @Override
    public int getCount() {
            return tabCount;
        }




    public MapsActivity.updateLocnListener getListener() {
        return listener;
    }
}
