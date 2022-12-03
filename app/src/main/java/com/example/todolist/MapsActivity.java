package com.example.todolist;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final String TAG="PANGA";
    AutocompleteSupportFragment autocompleteFragment;
    PlaceLikelihood currentPlace;
    LatLng currentPlaceLatlng;
    String currentPlaceName;
    Button currentLocationBtn;
    Button saveLocationBtn;
    Button cancelLocationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentLocationBtn=findViewById(R.id.currentLocationBtn);
        saveLocationBtn=findViewById(R.id.saveLocationBtn);
        cancelLocationBtn=findViewById(R.id.cancelLocationBtn);
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
                String selectedPlaceName=place.getName();
                LatLng selectedPlaceLatLng=place.getLatLng();
                // change needed in names
                Log.i(TAG, "Place: " + selectedPlaceName + selectedPlaceLatLng);
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
        LatLng kelowna = new LatLng(49.940147, -119.396516);
        mMap.addMarker(new MarkerOptions().position(kelowna).title("Kelowna"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(kelowna));
    }
    public void useCurrentLocation(View view){
        if(currentPlace.getPlace()!=null) {
            autocompleteFragment.setText(currentPlaceName); // sets the search bar to the the name of the current Place.
            updateMap(currentPlaceName, currentPlaceLatlng);
        }
        else return;
    }
    public void onSave(View view){

    }
    public void onCancel(View view){
        finish(); // Simply goes back to previous activity.
    }
}