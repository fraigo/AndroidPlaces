package me.franciscoigor.placesactivities;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

public class MainActivity extends AppCompatActivity {

    public final static int REQUEST_LOCATION_PERMISSION = 1001;
    float currentLikelihoodValue = -1;
    GoogleMapApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button getLocation = findViewById(R.id.get_location);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLocation.getText().toString().equals("Start Location")){
                    api.startLocationLooper();
                    getLocation.setText("Stop Location");
                }else{
                    api.stopLocationLooper();
                    getLocation.setText("Start Location");
                }

            }
        });

        final Button getPlace = findViewById(R.id.get_place);
        getPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                api.getCurrentPlace();
            }
        });

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
            }

            @Override
            public void onCheckedPermissions() {
                getLocation.setEnabled(true);
                getPlace.setEnabled(true);
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

