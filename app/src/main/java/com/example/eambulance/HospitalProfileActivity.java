package com.example.eambulance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HospitalProfileActivity extends AppCompatActivity {

    private TextView textname,textemail,textpin,textaddress;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_profile);

        textname=findViewById(R.id.textname);
        textemail=findViewById(R.id.textemail);
        textpin=findViewById(R.id.textpin);
        textaddress=findViewById(R.id.textaddress);

        textemail.setMovementMethod(new ScrollingMovementMethod());
        textaddress.setMovementMethod(new ScrollingMovementMethod());

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("ADMIN").child("HOSPITALS").child(firebaseAuth.getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Hospital h=dataSnapshot.getValue(Hospital.class);
                textname.setText(h.getName());
                textemail.setText(h.getEmail());
                textaddress.setText(h.getAddress());
                textpin.setText(h.getPin());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HospitalProfileActivity.this,databaseError.getCode(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HospitalProfileActivity.this,AddAmbulances.class));
        finish();
    }
}
