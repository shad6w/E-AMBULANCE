package com.example.eambulance;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class HospitalRegisterActivity extends AppCompatActivity {

    private TextInputLayout inputname, inputemail, inputpassword;
    private TextInputEditText inputaddress,inputpin;
    private Button btnsubmit;
    Hospital hospital;
    DatabaseReference d,hcount;
    FirebaseAuth mfirebaseauth;
    private ProgressDialog progressDialog;
    Geocoder geocoder;
    List<Address> addresses;
    String loc="";
    Double lat,lon;

    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION=99;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_register);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        inputname = findViewById(R.id.inputname);
        inputemail = findViewById(R.id.inputemail);
        inputpassword = findViewById(R.id.inputpassword);
        inputpin = findViewById(R.id.inputpin);
        inputaddress = findViewById(R.id.address);
        btnsubmit = findViewById(R.id.btnsubmit);
        geocoder=new Geocoder(this,Locale.getDefault());

        mfirebaseauth = FirebaseAuth.getInstance();
        d = FirebaseDatabase.getInstance().getReference();
        hospital=new Hospital();

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        fetchLocation();

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputname.getEditText().getText().toString().trim().length() == 0)
                    inputname.setError("Hospital Name is required");
                else if (inputemail.getEditText().getText().toString().trim().length() == 0)
                    inputemail.setError("Hospital Email is required");
                else if (inputpassword.getEditText().getText().toString().trim().length() == 0)
                    inputpassword.setError("Password is required");
                else if (inputaddress.getText().toString().trim().length() == 0)
                    inputaddress.setError("Hospital Address is required");
                else if (inputpin.getText().toString().trim().length() == 0)
                    inputpin.setError("Pin Code of your area is required");
                else {
                    CreateUserAndSaveData();
                }
            }
        });
    }



        private void CreateUserAndSaveData() {

            progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Hospital Registration...");
            progressDialog.setMessage("Processing your request...please wait...");
            progressDialog.show();

            mfirebaseauth.createUserWithEmailAndPassword(inputemail.getEditText().getText().toString(),inputpassword.getEditText().getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        saveData();
                        Toast.makeText(HospitalRegisterActivity.this, "Hospital Successfully Registered...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        FirebaseAuthException e = (FirebaseAuthException)task.getException();
                        Log.i("ERROR",e.getMessage());
                        Toast.makeText(HospitalRegisterActivity.this, "Hospital Registration Failed....Please try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    private void saveData()
    {
        hospital.setName(inputname.getEditText().getText().toString().trim());
        hospital.setEmail(inputemail.getEditText().getText().toString().trim());
        hospital.setPassword(inputpassword.getEditText().getText().toString().trim());
        hospital.setAddress(inputaddress.getText().toString().trim());
        hospital.setPin(inputpin.getText().toString().trim());

        hospital.setLatitude(lat);
        hospital.setLongitude(lon);

        d= FirebaseDatabase.getInstance().getReference().child("ADMIN").child("HOSPITALS").child(mfirebaseauth.getCurrentUser().getUid());

        d.setValue(hospital);


        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(HospitalRegisterActivity.this, HospitalLoginActivity.class));
        finish();

    }






    // Runtime permission for the location on the app
    private void fetchLocation()
    {
        GPSLocationPermission();
        if (ContextCompat.checkSelfPermission(HospitalRegisterActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?

            if (ActivityCompat.shouldShowRequestPermissionRationale(HospitalRegisterActivity.this
                    ,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission").setMessage("You have to give this permission to access this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(HospitalRegisterActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_READ_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(HospitalRegisterActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                                try {
                                    addresses=geocoder.getFromLocation(lat,lon,1);
                                    String address=addresses.get(0).getAddressLine(0);
                                    String Area=addresses.get(0).getLocality();
                                    String City=addresses.get(0).getAdminArea();
                                    String post=addresses.get(0).getPostalCode();
                                    String fulladdress=address+" , "+Area+" , "+City+" , "+post;
                                    inputaddress.setText(fulladdress);
                                    inputpin.setText(post);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Double lat = location.getLatitude();
                                    Double lon = location.getLongitude();

                                    //loc="Latitude :-" + lat + "\n" + "Longitude :-" + lon;

//                                    tvlat.setText(String.valueOf(lat));
//                                    tvlon.setText(String.valueOf(lon));
                                }
                            }
                        });
            }
            else
            {
                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission").setMessage("You have to give this permission to access this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(HospitalRegisterActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_READ_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        }
    }
    // RunTime permission for Google Location Services
    private void GPSLocationPermission()
    {
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(settingsBuilder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(HospitalRegisterActivity.this,MY_PERMISSIONS_REQUEST_READ_LOCATION);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });

    }
}