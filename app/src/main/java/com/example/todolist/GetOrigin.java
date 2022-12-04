package com.example.todolist;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.todolist.databinding.ActivityGetOriginBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetOrigin extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityGetOriginBinding binding;
    AutocompleteSupportFragment autocompleteFragment;
    String OriginName;
    LatLng OriginLatLng;
    String currentLocationName;
    LatLng currentLocationLatLng;
    String taskLocationName;
    double taskLocationLat;
    double taskLocationLong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        taskLocationName=intent.getStringExtra("TaskLocationName");
        taskLocationLat=Double.parseDouble(intent.getStringExtra("TaskLatitude"));
        taskLocationLong=Double.parseDouble(intent.getStringExtra("TaskLongitude"));
        currentLocationName=intent.getStringExtra("CurrentPlaceName");
        currentLocationLatLng=new LatLng(intent.getDoubleExtra("CurrentLatitude",0),intent.getDoubleExtra("CurrentLongitude",0));
        binding = ActivityGetOriginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Initialize the SDK
        Places.initialize(getApplicationContext(),Config.API_KEY);
        // Create a new Places client instance
        PlacesClient placesClient=Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager () .findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

       /*autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596)));

        */
        autocompleteFragment.setCountries("CA","US","IN");
        //autocompleteFragment.setText(currentPlace.getPlace().getName());
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected (Place place) {
                // TODO: Get info about the selected place.
                String TAG="ANSWERAGYA";
                OriginName=place.getName();
                OriginLatLng=place.getLatLng();
                // change needed in names
                Log.i(TAG, "Place: " + OriginName + OriginLatLng);
//                addedLocationName=selectedPlaceName;
//                addedLocationLatLng=selectedPlaceLatLng;
                getDirections(OriginName,OriginLatLng);
            }
            @Override
            public void onError (Status status) {
                // TODO: Handle the error.
                Log.i("Error Hogi", "An error occurred: " + status) ;
            }
        });
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng location =new LatLng(taskLocationLat,taskLocationLong);
        mMap.addMarker(new MarkerOptions().position(location).title("Origin: "+taskLocationName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
    public void getDirections(String name, LatLng latLng){
        LatLng origin = latLng;
        mMap.addMarker(new MarkerOptions().position(origin).title("Origin: "+name));

        LatLng dest = new LatLng(taskLocationLat,taskLocationLong);
        mMap.addMarker(new MarkerOptions().position(dest).title("Destination: "+taskLocationName));

        LatLng zaragoza = new LatLng(41.648823,-0.889085);

        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList();


        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCXNeBRQ_evQLmpHZtWFMSlHj5tPYNsAlg")
                .build();
        String originString=String.format("%s,%s",origin.latitude,origin.longitude);
        String destString=String.format("%s,%s",dest.latitude,dest.longitude);
        DirectionsApiRequest req = DirectionsApi.getDirections(context, originString, destString);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];
                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }}
                }
            }
        } catch(Exception ex) {
            String TAG="ERROR AGI";
            Log.e(TAG, ex.getLocalizedMessage());
        }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            mMap.addPolyline(opts);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6));
    }
    public void useCurrentLocationAsOrigin(View view){
        OriginName=currentLocationName;
        OriginLatLng=currentLocationLatLng;
        getDirections(OriginName,OriginLatLng);
    }
    public void goBack(View view){
        finish();
    }
}