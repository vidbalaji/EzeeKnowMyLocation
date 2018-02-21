package com.vidhyalearning.ezeeknowmylocation;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.key;
import static android.R.attr.radius;
import static android.R.attr.type;
import static com.vidhyalearning.ezeeknowmylocation.R.id.radiusText;
import static com.vidhyalearning.ezeeknowmylocation.R.id.spinner;

/**
 * Created by user on 11-Jan-18.
 */

public class NewTab extends android.support.v4.app.Fragment implements  Response.ErrorListener, Response.Listener<Bitmap>, AdapterView.OnItemSelectedListener, View.OnClickListener {
    int position;
    double gLat,gLon ;
    TextView weatherText;
    EditText customPlaceText;
    ImageView imgView;
    private String radiusStr;
    EditText radiusText;
    @Override
    public void setArguments(Bundle args) {
        position = args.getInt("POSITION");
        gLat = args.getDouble("LATITUDE");
        gLon = args.getDouble("LONGITUDE");
    }

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes

        Log.d("Get Current POsition", String.valueOf(position));
        ModelObject modelObject = ModelObject.values()[position];
        View view = inflater.inflate(modelObject.getLayoutResId(), container, false);
        if(modelObject.getLayoutResId()== R.layout.view_weather) {
            weatherText = (TextView) view.findViewById(R.id.weathertext);
            imgView = (ImageView)view.findViewById(R.id.imgView);
            String locn = "lat=" + gLat + "&lon=" + gLon;
            String url = "http://api.openweathermap.org/data/2.5/weather?units=metric&";
            String app = "&appid=1df14599236ab70357b4ac30e702949d";
            String finalUrl = url + locn + app;
            LatLng objLatLng = new LatLng(gLat, gLon);
            String cityName = getAddress(objLatLng);
            parseJsonObject(finalUrl,"Weather",cityName);
            Log.d("LAT", finalUrl);
        }
        else if(modelObject.getLayoutResId()== R.layout.view_placedetails){
             radiusText = (EditText)view.findViewById(R.id.radiusText);
            radiusStr = radiusText.getText().toString();
            Log.d("Radius",radiusStr);
            if(radiusStr.isEmpty()){
                radiusStr = "10";
            }
            // Spinner element
            customPlaceText = (EditText) view.findViewById(R.id.customPlaceText);
            Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

            // Spinner click listener
            spinner.setOnItemSelectedListener(this);
            Button okButton = (Button)view.findViewById(R.id.okButton);
            okButton.setOnClickListener(this);

        }
        Log.d("Get Current POsition",modelObject.getTitleResId() + " " + modelObject.getLayoutResId() + " " + position);
        return view;
    }
    String finalTemp="";
    public void parseJsonObject(String finalUrl, final String findType, final String cityName)
    {
        final RequestQueue rq = Volley.newRequestQueue(getContext());

        final JsonObjectRequest js = new JsonObjectRequest(JsonObjectRequest.Method.GET,finalUrl,null,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(findType.equals("Weather")) {
                        String tempCityName = cityName;
                        if(tempCityName.isEmpty())
                        tempCityName = response.getString("name");

                        JSONObject mainObject = response.getJSONObject("main");
                        String temp = mainObject.getString("temp") + "ºC";
                        String humidity = "Humidity:" + mainObject.getString("humidity") + "%";

                        JSONArray weatherArray = response.getJSONArray("weather");
                        JSONObject JSONWeather = weatherArray.getJSONObject(0);
                        String weatherCondition = JSONWeather.getString("description");
                        weatherCondition = weatherCondition.toUpperCase();
                        weatherCondition = tempCityName + "\n\n" + weatherCondition + "\n" + humidity;

                        finalTemp = weatherCondition + "\nTemp:" + temp + "\n";
                        weatherText.setText(finalTemp);

                        String imgIcon = JSONWeather.getString("icon");
                        Log.d("LAT", finalTemp);
                        //downloadIcon(imgIcon);
                    }
                    else if(findType.equals("Places")){
                        Log.d("Get Response",response.toString());
                    }

                }
                catch(JSONException e){
                    e.printStackTrace();

                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });

        rq.add(js);

    }
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),"Some Error in downloading Image",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(Bitmap response) {
        imgView.setImageBitmap(response);
    }
    private String getAddress( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( getContext() ); // 1
        String addressText = "";
        List<Address> addresses = null;
        Address address = null;
        try {
            addresses = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 ); // 2
            if (null != addresses && !addresses.isEmpty()) { // 3
                address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressText += (i == 0)?address.getAddressLine(i):("\n" + address.getAddressLine(i));
                }
            }
        } catch (IOException e ) {
        }
        return addressText;
    }
    private void downloadIcon(String imgIcon) {
        final String imgUrl = "http://openweathermap.org/img/w/" + imgIcon+".png";
        final RequestQueue rq1 = Volley.newRequestQueue(getContext());
        ImageRequest ir= new ImageRequest(imgUrl,this,imgView.getWidth(),imgView.getHeight(), ImageView.ScaleType.CENTER,null,this);
        rq1.add(ir);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    String myPlace="food";
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        myPlace =  parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent();
        radiusStr = radiusText.getText().toString();
        if(radiusStr.isEmpty()){
            radiusStr = "10";
        }
        String customPlaceStr = customPlaceText.getText().toString();
        i.setClass(getContext(),ShowPlacesMapsActivity.class);
        i.putExtra("PLACE_STRING",customPlaceStr);
        i.putExtra("PLACE_TYPE", myPlace);
        i.putExtra("LAT", gLat);
        i.putExtra("LON", gLon);
        i.putExtra("Radius",radiusStr);
        startActivity(i);
    }
}
