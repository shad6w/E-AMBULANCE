package com.example.eambulance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot_password_Activity extends AppCompatActivity {
private TextInputLayout inputforgotemail;
private Button btnsendemail;

private ProgressDialog progressDialog;

FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT>=21)
        {
            Window window=this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        setContentView(R.layout.activity_forgot_password_);
        inputforgotemail=findViewById(R.id.inputforgotemail);
        btnsendemail=findViewById(R.id.btnsendemail);
        firebaseAuth=FirebaseAuth.getInstance();
        btnsendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog=new ProgressDialog(Forgot_password_Activity.this);
                progressDialog.setMessage("Processing your request...please wait...");
                progressDialog.show();

                String useremail=inputforgotemail.getEditText().getText().toString().trim();
                if(useremail.equals(""))
                {
                    progressDialog.dismiss();
                    Toast.makeText(Forgot_password_Activity.this, "Enter your registered Email-id", Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Toast.makeText(Forgot_password_Activity.this, "Password Reset link is send to Email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Forgot_password_Activity.this, HospitalLoginActivity.class));
                                finish();
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(Forgot_password_Activity.this, "Sorry! Password Reset failed...Email is not registered!!! ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });
    }
}
