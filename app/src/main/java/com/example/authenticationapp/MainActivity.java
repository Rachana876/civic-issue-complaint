package com.example.authenticationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{


  private CardView mcomplaint, mwardinfo, mprogress, mfeedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //defining cards
       mcomplaint= (CardView) findViewById(R.id.Complaint);
       mwardinfo = (CardView) findViewById(R.id.WardInfo);
       mprogress= (CardView) findViewById(R.id.Progress);
       mfeedback= (CardView) findViewById(R.id.Feedback);
       //Add click listner to the cards
        mcomplaint.setOnClickListener(this);
        mwardinfo.setOnClickListener(this);
        mprogress.setOnClickListener(this);
        mfeedback.setOnClickListener(this);
    }

    public void Logout(View view){
        FirebaseAuth.getInstance().signOut(); //Logout
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logoutMenu:{
                FirebaseAuth.getInstance().signOut(); //Logout
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent i;

         switch (v.getId())   {
             case R.id.Complaint:i = new Intent(this,CameraAndGallery.class);startActivity(i); break;
             case R.id.Feedback : i =new Intent(this,Feedback.class);startActivity(i); break;
             default:break;
         }

    }
}
