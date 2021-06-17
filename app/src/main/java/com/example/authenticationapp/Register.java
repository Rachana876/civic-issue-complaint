package com.example.authenticationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    public static final String TAG = "Tag";
    EditText mFullName, mPassword , mEmail, mConfirmPassword ;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fstore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName    =  findViewById(R.id.FullName);
        mPassword    =  findViewById(R.id.Password);
        mEmail       =  findViewById(R.id.Email);
        mRegisterBtn =  findViewById(R.id.loginBtn);
        mLoginBtn    =  findViewById(R.id.createText);
        mConfirmPassword= findViewById(R.id.ConfirmPassword);

        fAuth = FirebaseAuth.getInstance();
        fstore= FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() !=null ){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }


        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 final String email = mEmail.getText().toString().trim();
                 String password = mPassword.getText().toString().trim();
                 final String fullname = mFullName.getText().toString().trim();
                 String confirmpassword = mConfirmPassword.getText().toString().trim();

                 if(TextUtils.isEmpty(fullname)){
                     mFullName.setError("Name is Required.");
                     return;
                 }

                  if(TextUtils.isEmpty(email)){
                      mEmail.setError("Email is Required.");
                      return;
                  }

                  if(TextUtils.isEmpty(password)){
                      mPassword.setError("Password is Required");
                      return;
                  }

                  if(password.length() < 6){
                      mPassword.setError("Password Must be >= 6 characters");
                      return;
                  }

                 if(TextUtils.isEmpty(confirmpassword)){
                    mConfirmPassword.setError("Please Confirm the Password");
                    return;
                 }

                 if(!confirmpassword.equals(password)){
                      Toast.makeText(Register.this,"your password does not match with the confirm password",Toast.LENGTH_SHORT).show();
                      mConfirmPassword.setError("Password does not match");
                      return;
                 }
                  progressBar.setVisibility(View.VISIBLE);

                  // register the user in firebase

                  fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // send verification email

                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());

                                }
                            });




                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID= fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fname", fullname );
                            user.put("email", email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for"+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),Login.class));

                        }else{
                           Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        } }
                  });
            }
        });


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }

}
