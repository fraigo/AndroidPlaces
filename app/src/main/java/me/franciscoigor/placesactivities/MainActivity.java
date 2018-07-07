package me.franciscoigor.placesactivities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    public final static int REQUEST_LOCATION_PERMISSION = 1001;
    float currentLikelihoodValue = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            System.out.println("REQUESTING PERMISSIONS");
        } else {
            getLocation();
        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                // Check if the permission is granted.
                System.out.println("RESULTS: "+grantResults[0]);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    System.out.println("PERMISSION OK");
                    getLocation();


                } else {
                    // Permission was denied...
                    System.out.println("PERMISSION ERROR");
                }

            }
        }
    }

    private void getLocation(){
        System.out.println("GET LOCATION");

        FusedLocationProviderClient flpClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest= LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback callback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (final Location location : locationResult.getLocations()) {
                    System.out.println("Location:"+ location);
                    getPlace();
                }

            }
        };
        try {
            flpClient.requestLocationUpdates(locationRequest, callback, Looper.myLooper());
        } catch(SecurityException ex){
            System.out.println("Location error");
            ex.printStackTrace();
        }

    }

    private void getPlace() {


        System.out.println("GET PLACE");
        final TextView message= findViewById(R.id.text_message);

        PlaceDetectionClient client = Places.getPlaceDetectionClient(this);
        try {
            Task<PlaceLikelihoodBufferResponse> placeResult = client.getCurrentPlace(null);
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        message.setText(String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                        System.out.println("PLACE: "+placeLikelihood.getPlace().getName());
                    }
                    likelyPlaces.release();
                }
            });
        }catch(Exception ex){
            System.out.println("ERROR PLACE");
            ex.printStackTrace();
        }




    }


}

