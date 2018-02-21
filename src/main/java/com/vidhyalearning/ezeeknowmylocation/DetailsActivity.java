package com.vidhyalearning.ezeeknowmylocation;

import android.appwidget.AppWidgetProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.icu.lang.UCharacter.JoiningGroup.FE;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        Button okDetailsButton = (Button) findViewById(R.id.okDetailsButton);
        okDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        String localPlaceId = getIntent().getStringExtra("PlaceId");
        String finalUrl="https://maps.googleapis.com/maps/api/place/details/json?placeid="+localPlaceId+"&key=" + LocalPlace.gmapskey ;
        Log.d("Url Response",finalUrl);
        final RequestQueue rq1 = Volley.newRequestQueue(getApplicationContext());

        final JsonObjectRequest js = new JsonObjectRequest(JsonObjectRequest.Method.GET,finalUrl,null,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {

                Log.d("Get Response",response.toString());
                try {

                    JSONObject placesObject = response.getJSONObject("result");
                    try{
                        String name = placesObject.getString("name");
                        TextView placeText = (TextView) findViewById(R.id.placeText);
                        placeText.setText(name);
                    }
                    catch(JSONException e){
                        e.printStackTrace();

                    }
                    try{
                        String rating = placesObject.getString("rating") + "/5";
                        TextView ratingText = (TextView) findViewById(R.id.ratingText);
                        ratingText.setText(rating);

                    }
                    catch(JSONException e){
                        e.printStackTrace();

                    }
                    try{
                        String vicinity = placesObject.getString("formatted_address");

                        TextView vicinityText = (TextView) findViewById(R.id.addressText);
                        vicinityText.setText(vicinity);
                        vicinityText.setMovementMethod(new ScrollingMovementMethod());
                    }
                    catch(JSONException e){
                        e.printStackTrace();

                    }

                    try{
                        String phone = placesObject.getString("international_phone_number");
                        TextView phoneText = (TextView)findViewById(R.id.phoneText);
                        phoneText.setText(phone);
                    }
                    catch(JSONException e){
                        e.printStackTrace();

                    }

                    try{
                        String website = placesObject.getString("website");
                        TextView websiteText = (TextView) findViewById(R.id.websiteText);
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
                        TextView openHours = (TextView)findViewById(R.id.openHoursText);
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
                        TextView typeText = (TextView) findViewById(R.id.categoryText);
                        typeText.setText(typesString);
                    }
                    catch(JSONException e){
                        e.printStackTrace();

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
        rq1.add(js);

    }

}

