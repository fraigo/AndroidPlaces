package me.franciscoigor.placesactivities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public abstract class GoogleMapApi {

    public static final int REQUEST_LOCATION_PERMISSION = 1001;
    private Activity activity;

    public GoogleMapApi(Activity activity){
        this.activity = activity;
    }

    public void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            System.out.println("REQUESTING PERMISSIONS");
        } else {
            onCheckedPermissions();
        }
    }



    public void onRequestedPermissions(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            // Check if the permission is granted.
            System.out.println("RESULTS: "+grantResults[0]);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                System.out.println("PERMISSION OK");
                onCheckedPermissions();
            } else {
                System.out.println("PERMISSION ERROR");
            }

        }
    }

    public void getLocation(){
        System.out.println("GET LOCATION");

        FusedLocationProviderClient flpClient = LocationServices.getFusedLocationProviderClient(activity);

        LocationRequest locationRequest= LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback callback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (final Location location : locationResult.getLocations()) {
                    onLocationFound(location);
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


    public void getPlace() {


        System.out.println("GET PLACE");
        PlaceDetectionClient client = Places.getPlaceDetectionClient(activity);
        try {
            Task<PlaceLikelihoodBufferResponse> placeResult = client.getCurrentPlace(null);
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {

                    if (task.isSuccessful()) {
                        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            onPlaceFound(placeLikelihood.getPlace(),placeLikelihood.getLikelihood());
                        }
                        likelyPlaces.release();
                    }else{
                        onPlaceError(task.getException());

                    }
                }
            });
        }catch(Exception ex){
            onPlaceError(ex);
        }

    }

    public String formatLocation(Location location){
        return String.format("%f,%f (%3.2f)",location.getLatitude(), location.getLongitude(), location.getAccuracy());
    }

    public abstract void onPlaceFound(Place place, float likeliHood);

    public abstract void  onPlaceError(Exception ex);

    public abstract void  onLocationFound(Location location);

    public abstract void onCheckedPermissions();
}
