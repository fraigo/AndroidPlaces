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
    GoogleMapApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = new GoogleMapApi(this) {
            @Override
            public void onPlaceFound(Place place, float likeliHood) {
                if (likeliHood>0.1){
                    message("PLACE FOUND "+place.getName()+ "(" + Math.round(likeliHood*100) + "%)");
                }

            }

            @Override
            public void onPlaceError(Exception ex) {
                message("ERROR PLACE");
                ex.printStackTrace();
            }

            @Override
            public void onLocationFound(Location location) {
                message("LOCATION FOUND: " + api.formatLocation(location)) ;
                api.getPlace();
            }

            @Override
            public void onCheckedPermissions() {
                api.getLocation();

            }
        };
        api.checkPermissions();

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        api.onRequestedPermissions(requestCode,permissions,grantResults);
    }



    private void message(String message){
        TextView txtMessage= findViewById(R.id.text_message);
        txtMessage.setText(txtMessage.getText()+"\n"+message);
        System.out.println(message);
    }




}

