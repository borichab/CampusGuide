package com.hsm.macs.campusguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int currentMapType = GoogleMap.MAP_TYPE_NORMAL; // Default map type is normal
    private ImageButton toggleMapButton, liveLocationButton;;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private PolygonLoader polygonLoader;
    private boolean isLocationUpdatesStarted = false;

    LatLng pointOfInterest = new LatLng(50.71706063728796, 10.46422167313702);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleMapButton = findViewById(R.id.toggleMapButton);
        liveLocationButton = findViewById(R.id.liveLocationButton);
        liveLocationButton.setImageResource(R.drawable.location_109);
        polygonLoader = new PolygonLoader(this);
        // Load the drawable resource
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.location_109);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize fused location provider client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        liveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLocationUpdatesStarted) {
                    stopLocationUpdates();
                    isLocationUpdatesStarted = false;
                    // Modify the fillColor attribute (in this case, changing it to Black)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        drawable.setTint(Color.BLACK);
                    else
                        liveLocationButton.setImageResource(R.drawable.location_109);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointOfInterest, 15.0f));
                } else {
                    startLocationUpdates();
                    isLocationUpdatesStarted = true;
                    // Modify the fillColor attribute (in this case, changing it to Blue)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        drawable.setTint(Color.BLUE);
                    else
                        liveLocationButton.setImageResource(R.drawable.location_109_blue);
                }
                // Set the modified drawable as the background of the liveLocationButton
                liveLocationButton.setBackground(drawable);
            }
        });

        // Create a location request
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // Update location every 5 seconds
        locationRequest.setFastestInterval(2000); // Fastest interval for updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Initialize location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    updateLiveLocation(location);
                }
            }
        };

        toggleMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap != null) {
                    currentMapType = (currentMapType == GoogleMap.MAP_TYPE_NORMAL)
                            ? GoogleMap.MAP_TYPE_SATELLITE
                            : GoogleMap.MAP_TYPE_NORMAL;
                    mMap.setMapType(currentMapType);

                    // Load the animation
                    Animation rotation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_360);

                    // Start the animation on the toggleMapButton
                    toggleMapButton.startAnimation(rotation);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker for a point of interest and move the camera
        mMap.addMarker(new MarkerOptions().position(pointOfInterest).title("Fachhochshule Schmalkalden"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointOfInterest, 15.0f));

        // Customize map settings if needed
        mMap.getUiSettings().setZoomControlsEnabled(true); // Enable zoom controls

        // Define the coordinates for the campus area polygon
        List<LatLng> campusCoordinates = new ArrayList<>();
        campusCoordinates.add(new LatLng(50.714657, 10.467253)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.715720, 10.468533)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.716263, 10.467417)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.717034, 10.466552)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.717818, 10.465563)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.718047, 10.465066)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.717354, 10.464253)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.7174425066481, 10.464001100457407)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.717493550483965, 10.463614186722271)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.71808010566404, 10.463777005626046)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.71872012784805, 10.462734510093476)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.71795236994671, 10.461819382478103)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.71596114326812, 10.464276239946837)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.715439882447924, 10.465352622172873)); //Coordinates Campus area border
        campusCoordinates.add(new LatLng(50.715090574649, 10.466221835912478)); //Coordinates Campus area border

        // Create a polygon options
        PolygonOptions campusPolygonOptions = new PolygonOptions()
                .addAll(campusCoordinates)
                .strokeColor(Color.BLUE) // Stroke color of the polygon outline
                .fillColor(Color.argb(30, 0, 0, 255)); // Fill color of the polygon

        // Add the polygon to the map
        mMap.addPolygon(campusPolygonOptions);

        // Add the campus area polygon to the map
        Polygon campusPolygon = mMap.addPolygon(campusPolygonOptions);

        // Load building polygons and building information
        List<CustomPolygon> buildingPolygons = polygonLoader.loadBuildingPolygons();

        // Add the building polygons to the map and set click listeners
        for (CustomPolygon customPolygon : buildingPolygons) {
            PolygonOptions polygonOptions = customPolygon.getPolygonOptions();

            polygonOptions.zIndex(1.0f).clickable(true);

            // Add the polygon to the map
            Polygon polygon = mMap.addPolygon(polygonOptions);
            polygon.setTag(customPolygon);

            // Attach a click listener to the polygon
            mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                @Override
                public void onPolygonClick(@NonNull Polygon polygon) {
                    CustomPolygon customPolygon = (CustomPolygon) polygon.getTag();
                    if (customPolygon != null) {
                        Log.d("PolygonDebug", "Polygon clicked: " + customPolygon.getTitle());
                        // Show a dialog with building information when the polygon is clicked
                        showBuildingInfoDialog(customPolygon);
                    }
                }
            });
        }
    }

    private void showBuildingInfoDialog(CustomPolygon customPolygon) {
        String title = customPolygon.getTitle();
        String description = customPolygon.getDescription();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(description)
                // Add other information fields to the dialog as needed
                .setPositiveButton("Close", null)
                .show();
    }


    // Helper function to calculate the center of a polygon
    private LatLng getPolygonCenter(List<LatLng> polygonPoints) {
        double latSum = 0.0;
        double lngSum = 0.0;

        for (LatLng point : polygonPoints) {
            latSum += point.latitude;
            lngSum += point.longitude;
        }

        int pointCount = polygonPoints.size();

        if (pointCount > 0) {
            return new LatLng(latSum / pointCount, lngSum / pointCount);
        } else {
            return null;
        }
    }

   private Marker userLocationMarker; // Declaration of member variable for the user's location marker

    private void updateLiveLocation(Location location) {
        if (mMap != null) {
            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Remove the previous user location marker if it exists
            if (userLocationMarker != null) {
                userLocationMarker.remove();
            }

            // Add a new user location marker
            userLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(userLatLng)
                    .title("My Location"));

            //Animate the camera to the user's location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15.0f));
        }
    }

    private void startLocationUpdates() {
         if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }
}