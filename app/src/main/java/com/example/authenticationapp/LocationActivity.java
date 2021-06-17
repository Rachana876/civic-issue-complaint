package com.example.authenticationapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;


public class LocationActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseDatabase db1= FirebaseDatabase.getInstance();
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textLatLong,textAddress;
    private ProgressBar progressBar;
    private ResultReceiver resultReceiver;
    private Button getLoc;
    private Button givebutton;
    private EditText notlive;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        resultReceiver = new AddressResultReceiver(new Handler());


        textLatLong= findViewById(R.id.textLatLong);
        progressBar= findViewById(R.id.progressbar);
        textAddress= findViewById(R.id.textAddress);
        notlive = findViewById(R.id.addressop);

        givebutton= findViewById(R.id.button3);
        getLoc=findViewById(R.id.buttonGetCurrentLocation);

        givebutton.setOnClickListener(this);
         getLoc.setOnClickListener(this);

          databaseReference = db1.getReference("Address:");

          // findViewById(R.id.buttonGetCurrentLocation).setOnClickListener(new View.OnClickListener() {
         //   @Override
           // public void onClick(View view) {
             //   if(ContextCompat.checkSelfPermission(
               //         getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
               // ) != PackageManager.PERMISSION_GRANTED ){
                 //   ActivityCompat.requestPermissions(
                   //         LocationActivity .this,
                     //       new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                       //     REQUEST_CODE_LOCATION_PERMISSION
                  //  );

              //  }else{
               //     getCurrentLocation();
                //}

            //}
        //});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode== REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            } else{
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation(){
        progressBar.setVisibility(View.VISIBLE);
        LocationRequest locationRequest=new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient( LocationActivity.this)
                .requestLocationUpdates(locationRequest,new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient( LocationActivity.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size() > 0){
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude=
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            textLatLong.setText(
                                    String.format(
                                            "Latitude: %s\nLongitude: %s",
                                            latitude,
                                            longitude
                                    )
                            );
                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            fetchAddressFromLatLong(location);
                        }else{

                            progressBar.setVisibility(View.GONE);
                        }

                    }

                }, Looper.getMainLooper());
    }

    private  void fetchAddressFromLatLong(Location location ){
        Intent intent = new Intent (this , FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }



    private class  AddressResultReceiver extends ResultReceiver{


        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == Constants.SUCCESS_RESULT){
                textAddress.setText(resultData.getString((Constants.RESULT_DATA_KEY)));
                String datafieldText = resultData.getString((Constants.RESULT_DATA_KEY));
                String idAdd= databaseReference.push().getKey();

                if(!TextUtils.isEmpty(datafieldText)){
                    DataAdd info = new DataAdd(idAdd,datafieldText);
                    databaseReference.child(idAdd).setValue(info);
                    Toast.makeText(LocationActivity.this, "Data has been saved", Toast.LENGTH_SHORT).show();

                }else{

                    Toast.makeText(LocationActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }




            }else {
                Toast.makeText(LocationActivity.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        }
    }



    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.button3:


                String datafieldText = notlive.getText().toString();
                String idAdd= databaseReference.push().getKey();

                if(!TextUtils.isEmpty(datafieldText)){
                    DataAdd info = new DataAdd(idAdd,datafieldText);
                    databaseReference.child(idAdd).setValue(info);
                    Toast.makeText(LocationActivity.this, "Data has been saved", Toast.LENGTH_SHORT).show();

                }

                i = new Intent(this,ConfirmActivity.class);startActivity(i);
                break;


            case R.id.buttonGetCurrentLocation:
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ){
                    ActivityCompat.requestPermissions(
                            LocationActivity .this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                }
                else{
                    getCurrentLocation();
                }
                //  CropImage.activity().start(LocationActivity.this);


                break;
        }
    }
}
