package com.example.eambulance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterAmbulance extends AppCompatActivity {

    private TextInputLayout inputdname,inputdphone,inputdemail,inputdpassword,inputaid,inputadesc;
    private Button btnsubmit;

    FirebaseAuth firebaseAuth;
    DatabaseReference ref,hospital_ref;

    Ambulances ambulances;

    private ProgressDialog progressDialog;
    private String hospitalID,h_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_ambulance);

        inputdname=findViewById(R.id.inputdname);
        inputdphone=findViewById(R.id.inputdphn);
        inputdemail=findViewById(R.id.inputdemail);
        inputdpassword=findViewById(R.id.inputdpassword);
        inputaid=findViewById(R.id.inputaid);
        inputadesc=findViewById(R.id.inputadesc);

        btnsubmit=findViewById(R.id.btnsubmit);

        firebaseAuth=FirebaseAuth.getInstance();
        ambulances=new Ambulances();
        hospitalID=firebaseAuth.getCurrentUser().getUid();

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dname=inputdname.getEditText().getText().toString().trim();
                String dphone=inputdphone.getEditText().getText().toString().trim();
                String demail=inputdemail.getEditText().getText().toString().trim();
                String dpassword=inputdpassword.getEditText().getText().toString().trim();
                String aid=inputaid.getEditText().getText().toString().trim();
                String adesc=inputadesc.getEditText().getText().toString().trim();

                if (dname.length() == 0)
                    inputdname.setError("Driver Name is required");
                else if (dphone.length() == 0)
                    inputdphone.setError("Driver phone no. is required");
                else if (demail.length() == 0)
                    inputdphone.setError("Driver Email-ID is required");
                else if (dpassword.length() == 0)
                    inputdphone.setError("Driver password is required");
                else if (aid.length() == 0)
                    inputaid.setError("Ambulance no. is required");
                else if (adesc.length() == 0)
                    inputadesc.setError("Ambulance Description is required");
                else
                {
                    progressDialog=new ProgressDialog(RegisterAmbulance.this);
                    progressDialog.setTitle("Ambulance Registration...");
                    progressDialog.setMessage("Adding driver details...please wait...");
                    progressDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(demail, dpassword).addOnCompleteListener(RegisterAmbulance.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SaveDriverDetails(dname,dphone,demail,dpassword,aid,adesc);
                                Toast.makeText(RegisterAmbulance.this, "Driver Successfully Registered...", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                Log.i("ERROR", e.getMessage());
                                Toast.makeText(RegisterAmbulance.this, "Driver Registration Failed....Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void SaveDriverDetails(String dname, String dphone,String demail, String dpassword, String aid, String adesc)
    {
        ambulances.setInputdname(dname);
        ambulances.setInputdphone(dphone);
        ambulances.setInputdemail(demail);
        ambulances.setInputdpassword(dpassword);
        ambulances.setInputaid(aid);
        ambulances.setInputadesc(adesc);

        if(firebaseAuth.getCurrentUser() != null)
        {
            progressDialog.dismiss();
            ref= FirebaseDatabase.getInstance().getReference().child("ADMIN").child("HOSPITALS").child(hospitalID).child("AMBULANCES").child(firebaseAuth.getCurrentUser().getUid());

            ref.setValue(ambulances);

            h_key=FirebaseDatabase.getInstance().getReference().child("ADMIN").child("HOSPITALS").child(hospitalID).getKey();


            hospital_ref=FirebaseDatabase.getInstance().getReference().child("DRIVER-HOSPITAL").child(firebaseAuth.getCurrentUser().getUid());
            hospital_ref.setValue(h_key);

            Toast.makeText(this, "Driver added Successfully...", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(RegisterAmbulance.this,AddAmbulances.class);
            startActivity(intent);
            finish();
        }
        else
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Hospital not signed in...please sign in..", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterAmbulance.this, HospitalLoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterAmbulance.this,AddAmbulances.class));
        finish();
    }
}
