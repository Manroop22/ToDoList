package com.example.todolist;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.todolist.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.SphericalUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final String TAG="PANGA";
    AutocompleteSupportFragment autocompleteFragment;
    PlaceLikelihood currentPlace;
    String selectedPlaceName;
    LatLng selectedPlaceLatLng;
    String addedLocationName;
    LatLng addedLocationLatLng;
    LatLng currentPlaceLatlng;
    String currentPlaceName;
    Button currentLocationBtn;
    Button saveLocationBtn;
    Button cancelLocationBtn;
    // These will be used if the location has already been set. Otherwise, we will use these to the latitude and longitude values of UBC Okanagan.
    double defaultLatitude;
    double defaultLongitude;
    String defaultLocationName;
    private ArrayList<ToDo> toDoList;
    private int toDoIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentLocationBtn=findViewById(R.id.currentLocationBtn);
        saveLocationBtn=findViewById(R.id.saveLocationBtn);
        cancelLocationBtn=findViewById(R.id.cancelLocationBtn);
        Intent intent=getIntent();
        toDoList=(ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
        toDoIndex= intent.getIntExtra("Index", 0);
        defaultLatitude=Double.parseDouble(intent.getStringExtra("Latitude"));
        defaultLongitude=Double.parseDouble(intent.getStringExtra("Longitude"));
        defaultLocationName=intent.getStringExtra("LocationName");
        if(defaultLocationName.equals(null))
            defaultLocationName="Kelowna";
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
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
                selectedPlaceName=place.getName();
                selectedPlaceLatLng=place.getLatLng();
                // change needed in names
                Log.i(TAG, "Place: " + selectedPlaceName + selectedPlaceLatLng);
                addedLocationName=selectedPlaceName;
                addedLocationLatLng=selectedPlaceLatLng;
                updateMap(selectedPlaceName,selectedPlaceLatLng);
            }
            @Override
            public void onError (Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status) ;
            }
        });

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG);

// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            @SuppressLint("MissingPermission") Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);

            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();
                    currentPlace=response.getPlaceLikelihoods().get(0);
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        System.out.println(placeLikelihood);
                        if(placeLikelihood.getLikelihood()> currentPlace.getLikelihood()) {
                            currentPlace = placeLikelihood; // This will be the most likely place.
                            System.out.println(placeLikelihood); // This is just for testing.
                            Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                    placeLikelihood.getPlace().getName(),
                                    placeLikelihood.getLikelihood()));
                            break;
                        }
                    }
                     currentPlaceLatlng=currentPlace.getPlace().getLatLng();
                    currentPlaceName=currentPlace.getPlace().getName();
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            getLocationPermission();
        }
    }

    private void getLocationPermission() {
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                    }
                    Boolean coarseLocationGranted = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        coarseLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_COARSE_LOCATION,false);
                    }
                    if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );

// ...

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }
    public void updateMap(String name, LatLng latLng){
        mMap.addMarker(new MarkerOptions().position(latLng).title(name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Kelowna and move the camera by default
        LatLng location = new LatLng(defaultLatitude, defaultLongitude);
        mMap.addMarker(new MarkerOptions().position(location).title(defaultLocationName));
        // Used for Testing.
        mMap.addMarker(new MarkerOptions().position(new LatLng(49.8872315,-119.3866862)).title(defaultLocationName));
        Log.i("Distance",""+SphericalUtil.computeDistanceBetween(location, new LatLng(49.8872315,-119.3866862)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
    public void useCurrentLocation(View view){
        if(currentPlace!=null) {
            Toast.makeText(this, "Your Current Location", Toast.LENGTH_SHORT).show();
            autocompleteFragment.setText(currentPlaceName);// sets the search bar to the the name of the current Place.
            addedLocationName=currentPlaceName;
            addedLocationLatLng=currentPlaceLatlng;
            updateMap(currentPlaceName, currentPlaceLatlng);
        }
        else{
            Toast.makeText(this, "Processing, Click Again.", Toast.LENGTH_SHORT).show();
        }
    }
    public void onSave(View view){
        Log.i("Saved Values",addedLocationName+", "+addedLocationLatLng);
        if(addedLocationName==null || addedLocationLatLng==null){
            Toast.makeText(this, "No Location Selected", Toast.LENGTH_SHORT).show(); // Error Message that pops up in the form of a Toast
            return;
        }
        ToDo todo=toDoList.get(toDoIndex); // This gets the todo to be changed.
        todo.setLocationName(addedLocationName);
        todo.setLatitude(Double.toString(addedLocationLatLng.latitude));
        todo.setLongitude(Double.toString(addedLocationLatLng.longitude));
        Log.i("Milgi", toDoList.toString());
        setResult(RESULT_OK, new Intent().putExtra("ToDoList",toDoList));
        try {
            finish();
        }catch (Exception e){
            Log.e("Syapa", e.toString());
        }
    }
    public void onCancel(View view){
        // If the user has not entered any location and wants to cancel, then the Location Tag that had been added to the the todo will be removed.
        if(addedLocationName==null || addedLocationLatLng==null){
            ToDo todo=toDoList.get(toDoIndex);
            todo.removeTag("Location");
            setResult(RESULT_OK, new Intent().putExtra("ToDoList",toDoList));
        }
        finish(); // Simply goes back to previous activity.
    }
    public void getDirections(View view){
        ToDo todo=toDoList.get(toDoIndex);
        String latitude=todo.getLatitude();
        String longitude=todo.getLongitude();
        if( latitude != null && longitude != null) {
            Intent intent = new Intent(this, GetOrigin.class);
            intent.putExtra("TaskLocationName",todo.getLocationName());
            intent.putExtra("TaskLatitude",latitude);
            intent.putExtra("TaskLongitude",longitude);
            if(currentPlaceLatlng!=null) {
                intent.putExtra("CurrentPlaceName",currentPlaceName);
                intent.putExtra("CurrentLatitude", currentPlaceLatlng.latitude);
                intent.putExtra("CurrentLatitude", currentPlaceLatlng.longitude);
                startActivity(intent);
            } else Toast.makeText(this, "Please wait. Try Again", Toast.LENGTH_SHORT).show(); // This message is shown so that the startActivity happens only when we have the current LocationLatLng.
        }
        else{
            Toast.makeText(this, "Set Task Location First", Toast.LENGTH_SHORT).show();
        }
    }

}