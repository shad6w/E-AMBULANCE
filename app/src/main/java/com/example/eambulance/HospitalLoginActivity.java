package com.example.eambulance;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class HospitalLoginActivity extends AppCompatActivity {

    TextInputLayout inputemail,inputpassword;
    Button btnlogin;
    TextView tvregister,tvforgotpassword,tvdriverlogin;

    private ProgressDialog progressDialog;

    FirebaseAuth mfirebaseauth;

    private long backPressedTime;

    private static final int Request_User_Location_Code=99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_login);

        GPSLocationPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }

        inputemail=findViewById(R.id.inputemail);
        inputpassword=findViewById(R.id.inputpassword);
        btnlogin=findViewById(R.id.btnlogin);
        tvregister=findViewById(R.id.tvregister);
        tvforgotpassword=findViewById(R.id.tvforgotpassword);
        tvdriverlogin=findViewById(R.id.tvdriverlogin);

        mfirebaseauth = FirebaseAuth.getInstance();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=inputemail.getEditText().getText().toString().trim();
                String password=inputpassword.getEditText().getText().toString().trim();
                if(email.isEmpty())
                {
                    inputemail.setError("Please enter email id");
                    inputemail.requestFocus();
                }
                else if(password.isEmpty())
                {
                    inputpassword.setError("Please enter password");
                    inputpassword.requestFocus();
                }
                else if(email.isEmpty() && password.isEmpty())
                {
                    Toast.makeText(HospitalLoginActivity.this, "Login failed....please fill up the details", Toast.LENGTH_LONG).show();
                }
                else if(!(email.isEmpty() && password.isEmpty()))
                {
                    progressDialog=new ProgressDialog(HospitalLoginActivity.this);
                    progressDialog.setTitle("Hospital Login...");
                    progressDialog.setMessage("Processing your request...please wait...");
                    progressDialog.show();

                    mfirebaseauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(HospitalLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Toast.makeText(HospitalLoginActivity.this, "Login Unsuccessful....please try again later", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                startActivity(new Intent(HospitalLoginActivity.this,AddAmbulances.class));
                                finish();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(HospitalLoginActivity.this, "Error Occured....please try again later", Toast.LENGTH_LONG).show();
                }
            }
        });

        tvregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(HospitalLoginActivity.this, HospitalRegisterActivity.class);
                startActivity(i);
            }
        });

        tvforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HospitalLoginActivity.this,Forgot_password_Activity.class);
                startActivity(intent);
            }
        });

        tvdriverlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(HospitalLoginActivity.this, DriverLoginActivity.class);
                startActivity(i);
            }
        });

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
                                            .startResolutionForResult(HospitalLoginActivity.this,Request_User_Location_Code);
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

        public boolean checkUserLocationPermission()
        {
            GPSLocationPermission();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?

                if (ActivityCompat.shouldShowRequestPermissionRationale(HospitalLoginActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    new AlertDialog.Builder(this)
                            .setTitle("Required Location Permission").setMessage("You have to give this permission to access this feature")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(HospitalLoginActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            Request_User_Location_Code);
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
                    ActivityCompat.requestPermissions(HospitalLoginActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            Request_User_Location_Code);
                }
                return false;
            }
            else
            {
                return true;
            }
        }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {

        if(backPressedTime + 2000 > System.currentTimeMillis())
        {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
        else
        {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime=System.currentTimeMillis();
    }
}