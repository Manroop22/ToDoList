package com.example.todolist;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class GetOrigin extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityGetOriginBinding binding;
    AutocompleteSupportFragment autocompleteFragment;
    String selectedOriginName;
    LatLng selectedOriginLatLng;
    String addedLocationName;
    LatLng addedLocationLatLng;
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
                selectedOriginName=place.getName();
                selectedOriginLatLng=place.getLatLng();
                // change needed in names
                Log.i(TAG, "Place: " + selectedOriginName + selectedOriginLatLng);
//                addedLocationName=selectedPlaceName;
//                addedLocationLatLng=selectedPlaceLatLng;
                getDirections(selectedOriginName,selectedOriginLatLng);
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

        LatLng location = new LatLng(taskLocationLat, taskLocationLong);
        mMap.addMarker(new MarkerOptions().position(location).title("Destination: "+taskLocationName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
    public void getDirections(String name, LatLng latLng){
        mMap.addMarker(new MarkerOptions().position(latLng).title("Origin: "+name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
    public void useCurrentLocation(View view){

    }
    public void goBack(View view){
        finish();
    }
}