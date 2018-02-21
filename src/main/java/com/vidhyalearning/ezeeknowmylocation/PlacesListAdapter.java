package com.vidhyalearning.ezeeknowmylocation;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.attr.key;
import static android.R.attr.name;
import static android.content.Context.AUDIO_SERVICE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE;
import static com.vidhyalearning.ezeeknowmylocation.R.id.map;
import static com.vidhyalearning.ezeeknowmylocation.R.id.websiteText;

/**
 * Created by user on 29-Jan-18.
 */

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.MyViewHolder>{
    private List<LocalPlace> localPlaceList;
   
    private List<LocalPlace> dictionaryWords ;
    private List<LocalPlace> filteredList ;
    OnCustomEventListener mapListener;
    public interface OnCustomEventListener {
        void highlightMarker(Marker marker);
    }
    public void setCustomEventListener(OnCustomEventListener eventListener) {
        mapListener = eventListener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView placeTitle, typetext,ratingText,categoryText,deliveryText;
        public RelativeLayout adaptorLayout1;
        public View view1;
        public final Button detailsButton,photoButton,reviewButton;
        public LocalPlace localPlace;
        public MyViewHolder(View view) {
            super(view);
            view1 = view;
            placeTitle = (TextView) view.findViewById(R.id.placeTitle);
            typetext = (TextView) view.findViewById(R.id.typetext);

            detailsButton = (Button) view.findViewById(R.id.detailsButton);
            ratingText = (TextView) view.findViewById(R.id.ratingText);
            categoryText = (TextView) view.findViewById(R.id.categoryText);


            reviewButton = (Button)view.findViewById(R.id.reviewButton);
            photoButton = (Button) view.findViewById(R.id.photoButton);
            adaptorLayout1 = (RelativeLayout)view.findViewById(R.id.adaptorLayout1);
        }
    }


    public PlacesListAdapter(List<LocalPlace> localPlaceList) {
        this.localPlaceList = localPlaceList;
        dictionaryWords = new ArrayList<LocalPlace>();
        //filteredList = localPlaceList;
        dictionaryWords.addAll(localPlaceList);

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_places, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        LocalPlace localPlace = localPlaceList.get(position);
        final LocalPlace finalProduct = localPlace;
        if(dictionaryWords.size()<=0){
            dictionaryWords.addAll(localPlaceList);
        }
        holder.localPlace=localPlace;
        holder.placeTitle.setText(localPlace.getName());
        holder.typetext.setText(localPlace.getVicinity());

       // holder.deliveryText.setText(localPlace.getDeliveryTime());
        holder.categoryText.setText("Ariel Distance from your place:"+  localPlace.getDistance());
        holder.ratingText.setText("Rating:" + localPlace.getRating());

        holder.photoButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                //compareScreen(holder);
            }


        });
        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                //displayScreen(holder);
                detailScreen(holder);
            }


        });

        holder.reviewButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                //reviewScreen(holder);
            }


        });
        holder.view1.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

               mapListener.highlightMarker(holder.localPlace.getMarker());
            }


        });
        holder.adaptorLayout1.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                mapListener.highlightMarker(holder.localPlace.getMarker());
            }


        });

    }

    private void detailScreen(MyViewHolder holder) {
        Intent i = new Intent();
        i.setClass(holder.view1.getContext(), DetailsActivity.class);
        i.putExtra("PlaceId",holder.localPlace.getPlaceId() );
        holder.view1.getContext().startActivity(i);
    }
    public void showPlaces(LocalPlace localPlace, Context context, final Dialog dialog1)
    {

        String finalUrl="https://maps.googleapis.com/maps/api/place/details/json?placeid="+localPlace.getPlaceId()+"&key=AIzaSyCEPDGKK6QzEs4HRQR8AzLYCTy17uVjcSg";
        Log.d("Url Response",finalUrl);
        final RequestQueue rq1 = Volley.newRequestQueue(context);

        final JsonObjectRequest js = new JsonObjectRequest(JsonObjectRequest.Method.GET,finalUrl,null,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {

                Log.d("Get Response",response.toString());
                try {

                    JSONObject placesObject = response.getJSONObject("result");
                    try{
                    String name = placesObject.getString("name");
                    TextView placeText = (TextView) dialog1.findViewById(R.id.placeText);
                    placeText.setText(name);
                    }
                    catch(JSONException e){
                        e.printStackTrace();

                    }
                    try{
                    String rating = placesObject.getString("rating");
                    TextView ratingText = (TextView) dialog1.findViewById(R.id.ratingText);
                    ratingText.setText(rating);
                    }
                    catch(JSONException e){
                        e.printStackTrace();

                    }
                    try{
                    String vicinity = placesObject.getString("formatted_address");
                    TextView vicinityText = (TextView) dialog1.findViewById(R.id.addressText);
                    vicinityText.setText(vicinity);
                    }
                    catch(JSONException e){
                        e.printStackTrace();

                    }

                    try{
                    String phone = placesObject.getString("formatted_phone_number");
                    TextView phoneText = (TextView) dialog1.findViewById(R.id.phoneText);
                    phoneText.setText(phone);
                    }
                    catch(JSONException e){
                        e.printStackTrace();

                    }
                    try{
                        String phone = placesObject.getString("formatted_phone_number");
                        TextView phoneText = (TextView) dialog1.findViewById(R.id.phoneText);
                        phoneText.setText(phone);
                    }
                    catch(JSONException e){
                        e.printStackTrace();

                    }
                    try{
                        String website = placesObject.getString("website");
                        TextView websiteText = (TextView) dialog1.findViewById(R.id.websiteText);
                        websiteText.setText(website);
                    }
                    catch(JSONException e){
                        e.printStackTrace();

                    }

                    try{
                        JSONObject openHoursObject = placesObject.getJSONObject("opening_hours");
                        JSONArray openHoursArray = openHoursObject.getJSONArray("weekday_text");
                        String openHoursString="";
                        for (int i=0; i <openHoursArray.length();i++){
                            String tempStr = openHoursArray.getString(i);
                            openHoursString = openHoursString + "\n"+ tempStr;
                        }
                        TextView openHours = (TextView) dialog1.findViewById(R.id.openHoursText);
                        openHours.setText(openHoursString);
                    }
                    catch(JSONException e){
                        e.printStackTrace();

                    }
                    try{
                        JSONArray typesObject = placesObject.getJSONArray("types");

                        String typesString="";
                        for (int i=0; i <typesObject.length();i++){
                            String tempStr = typesObject.getString(i);
                            typesString = typesString + "\n"+tempStr;
                        }
                        TextView typeText = (TextView) dialog1.findViewById(R.id.categoryText);
                        typeText.setText(typesString);
                    }
                    catch(JSONException e){
                        e.printStackTrace();

                    }



                }
                catch(JSONException e){
                    e.printStackTrace();

                }

                dialog1.show();

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });

        rq1.add(js);

    }

    @Override
    public int getItemCount() {
         return localPlaceList.size();
    }

}

