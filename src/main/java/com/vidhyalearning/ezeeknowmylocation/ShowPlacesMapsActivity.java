package com.vidhyalearning.ezeeknowmylocation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.key;
import static android.R.attr.type;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE;
import static com.vidhyalearning.ezeeknowmylocation.R.id.add;
import static com.vidhyalearning.ezeeknowmylocation.R.id.imgView;
import static com.vidhyalearning.ezeeknowmylocation.R.id.recyclerView1;
import static java.lang.Thread.sleep;
import static java.security.AccessController.getContext;

public class ShowPlacesMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{

    private GoogleMap mMap;
    double gLat,gLon;
    String placeType,placeString;
    int totalCount=0;
    private Marker tempMarker=null;
    private String radiusStr;
    String mNextToken ="";
    customListener mListener;
    private String mNextSetToken="";
    private RecyclerView recyclerView;
    private ArrayList<LocalPlace> mLocalPlaceList;
    private View layoutView;
    private PlacesListAdapter mAdapter;
    Marker prevMarker;
    private Button button2;
private HashMap<Marker,String> myMarkers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener = null;
        mLocalPlaceList = new ArrayList<>();
        prevMarker = null;
        mLocalPlaceList.clear();
        placeType = getIntent().getStringExtra("PLACE_TYPE");
        placeString = getIntent().getStringExtra("PLACE_STRING");
        radiusStr = getIntent().getStringExtra("Radius");
        gLat = getIntent().getDoubleExtra("LAT",0);
        gLon = getIntent().getDoubleExtra("LON",0);
        setContentView(R.layout.activity_show_places_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

         mListener = new customListener() {
            @Override
            public void setToken(String token) {
                mNextToken =   token;
             //   mNextToken="CqQCFAEAAC0w41IktGx64GwgtS3hEh1unqHHbELMsgzMSH6_5GrOk4UWOL-jBoKaa-DHqnjWkwZVugitB3RGBW3__ViDWXHuYkyRbvFthrcBDtQ6qxKVgsF9WhbZqGa22JCbJLlGz2Y7z0CtXK6sevvAurJlu-wmx7S4jbeqDo1t_MduJkciH_F_Atom5NyUHyb4BubT8zvQ8QS-a2U_WFvAaiL8m11mZloncXggBjNB5Lf3Im4pajNTSqX7meMF0K2BS6frKSM63jgUO61sdGWx-428jI1on5HJw4ayx23OajUOAsSBgEWySNj1pbVncn3y4zxuFtYd2TLMGcfiqcwY5VnN3gUx6T8sDzAK1O-u-2wGNmQp2f74XdSka36ey0uWsr78HRIQMdUthxhazizzWzH8oJDBKhoUHHoghnXAbwfpSvCB_RCj3R_jobI";
                try {
                     sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showNextPlaces();
            }

             @Override
             public void setNextToken(String token) {
                mNextSetToken =   token;

                 try {
                     sleep(3000);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
                 showNextSetPlaces();
             }
         };
         button2 = (Button)findViewById(R.id.button2);
         layoutView = (View)findViewById(R.id.linearLayout1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        layoutView.setVisibility(View.INVISIBLE);
        myMarkers = new HashMap<> ();

        //permanentproductList.addAll(productList);




    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    RequestQueue rq;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng myPlace = new LatLng(gLat, gLon);
        mMap.addMarker(new MarkerOptions().position(myPlace).title("My Selected Place"));
        mMap.setOnMarkerClickListener(this);
        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(gLat, gLon));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(10);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
         rq= Volley.newRequestQueue(getApplicationContext());
        showPlaces();


        // showNextPlaces();
        //showNextSetPlaces();
    }
    public void showPlaces()
    {
        float radius = Float.parseFloat(radiusStr);
        radius = radius * 1000;
        String newRadius = String.valueOf(radius);
        String finalUrl="";
        if(placeString.isEmpty())
         finalUrl="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+gLat +"," +gLon+"&radius=" + newRadius +"&type="+
                placeType
                 + "&key=" + LocalPlace.gmapskey +"\n";
        else {
            String tempPlaceString = placeString.replace(' ','+');
            finalUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + tempPlaceString + "&location=" + gLat + "," + gLon + "&radius=" + newRadius
                    + "&key=" + LocalPlace.gmapskey +"\n";
        }
        Log.d("Url Response",finalUrl);
        //final RequestQueue rq1 = Volley.newRequestQueue(getApplicationContext());

        final JsonObjectRequest js = new JsonObjectRequest(JsonObjectRequest.Method.GET,finalUrl,null,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {

                        Log.d("Get Response",response.toString());

                    String nextPageToken="";
                    try {
                        nextPageToken = response.getString("next_page_token");
                        getResults(response);
                        mListener.setToken(nextPageToken);


                    }
                    catch(JSONException e){
                        e.printStackTrace();
                        getResults(response);
                        mNextToken="";
                    }


              //  showNextPlaces();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });

        rq.add(js);



    }

    public void showList(View view) {
        if(layoutView.getVisibility()==View.INVISIBLE) {
            layoutView.setVisibility(View.VISIBLE);
            button2.setText("Hide Details");
        }
        else{
            layoutView.setVisibility(View.INVISIBLE);
            button2.setText("Show Details");
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String localplaceTemp = (String) myMarkers.get(marker);
        Intent i = new Intent();
        i.setClass(getApplicationContext(), DetailsActivity.class);
        i.putExtra("PlaceId",localplaceTemp );
        startActivity(i);
        return false;
    }

    public interface customListener {
        public void setToken(String token);
        public void setNextToken(String token);
    }
    private void showNextPlaces() {


            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=" + mNextToken + "&key=" + LocalPlace.gmapskey;
            Log.d("Url Response", url);

            final JsonObjectRequest js = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.d("Get Response", response.toString());
                   String nextPageToken ="";
                    try {
                        nextPageToken = response.getString("next_page_token");
                        getResults(response);

                        mListener.setNextToken(nextPageToken);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        getResults(response);
                        mNextSetToken="";
                    }

                    //showNextSetPlaces();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                }
            });
            rq.add(js);

    }


    private void showNextSetPlaces() {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=" + mNextSetToken + "&key=" + LocalPlace.gmapskey + "\n";
        Log.d("Url Response", url);

        final JsonObjectRequest js = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("Get Response", response.toString());

                getResults(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });

        rq.add(js);
    }

    private void getResults(JSONObject response) {
        JSONArray placesArray = null;
        try {
            placesArray = response.getJSONArray("results");
            totalCount = totalCount+placesArray.length();
            TextView totalCountStr = (TextView)findViewById(R.id.placesCommentText);
            String tempPlace = (placeString.isEmpty())?placeType:placeString;
            totalCountStr.setText(String.valueOf(totalCount) + " "+ tempPlace + " places found");
            for(int i=0;i<placesArray.length();i++){
                LocalPlace place = new LocalPlace();
                JSONObject JSONPlace = placesArray.getJSONObject(i);
                JSONObject geometryObj = JSONPlace.getJSONObject("geometry");
                JSONObject locnObject = geometryObj.getJSONObject("location");
                String lat="",lng="",name="",placeId="";
                try {
                     name = JSONPlace.getString("name");
                    place.setName(name);
                    String icon = JSONPlace.getString("icon");
                     lat = locnObject.getString("lat");
                    place.setlat(lat);
                     lng = locnObject.getString("lng");
                    place.setlng(lng);
                    double endLat = Double.parseDouble(lat);
                    double endLng = Double.parseDouble(lng);
                    float[] results = new float[1];
                    Location.distanceBetween(
                            gLat,
                            gLon,
                            endLat,
                            endLng,
                            results);
                    float distance = results[0]/1000;
                    String strDistance = String.valueOf(distance) + " Km";
                    place.setDistance(strDistance);
                     placeId = JSONPlace.getString("place_id");
                    place.setPlaceId(placeId);
                    String rating = JSONPlace.getString("rating");
                    place.setRating(rating);
                    String vicinity = JSONPlace.getString("vicinity");
                    place.setVicinity(vicinity);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                double dLat = Double.parseDouble(lat);
                double dLon = Double.parseDouble(lng);
                LatLng myPlace = new LatLng(dLat, dLon);  // this is New York
                tempMarker = mMap.addMarker(new MarkerOptions().position(myPlace).title(name));
                tempMarker.setIcon(BitmapDescriptorFactory.defaultMarker(HUE_ORANGE));
                place.setMarker(tempMarker);
                mLocalPlaceList.add(place);
                myMarkers.put(tempMarker,placeId);
            }
            try{

                  String  nextPageToken = response.getString("next_page_token");

            }
            catch(JSONException e) {
                Collections.sort(mLocalPlaceList, LocalPlace.distCompare);
                mAdapter = new PlacesListAdapter(mLocalPlaceList);
                recyclerView.setAdapter(mAdapter);
                mAdapter.setCustomEventListener(new PlacesListAdapter.OnCustomEventListener(){
                    @Override
                    public void highlightMarker(Marker marker) {
                        if(prevMarker!=null)
                            prevMarker.setIcon(BitmapDescriptorFactory.defaultMarker(HUE_ORANGE));
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN));
                       LatLng latlng =  marker.getPosition();
                        CameraUpdate center= CameraUpdateFactory.newLatLng(latlng);
                        CameraUpdate zoom=CameraUpdateFactory.zoomTo(14);

                        mMap.moveCamera(center);
                        mMap.animateCamera(zoom);
                        prevMarker = marker;
                    }



                });
                e.printStackTrace();
            }
            if(mAdapter!=null)
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
