package com.integrals.gamermw.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adefruandta.spinningwheel.SpinningWheelView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.integrals.gamermw.Helpers.Constants;
import com.integrals.gamermw.R;

import java.util.ArrayList;
import java.util.List;

public class LuckyWheel extends AppCompatActivity {
    private ArrayList<String> commenters;
    private SpinningWheelView wheelView;
    private DatabaseReference details;
    private ProgressBar progressBar;
    private String postID;
    private DatabaseReference postReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_wheel);
        wheelView =findViewById(R.id.wheel);
        progressBar=findViewById(R.id.wheelProgress);
        commenters=new ArrayList<>();
        progressBar.setVisibility(View.GONE);
        details= FirebaseDatabase.getInstance().getReference().child("GiveAway");
        postReference=FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchCommenters();
        wheelView.setOnRotationListener(new SpinningWheelView.OnRotationListener<String>() {
            @Override
            public void onRotation() {
                Toast.makeText(getApplicationContext(),"Rotating",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStopRotation(String item) {
                Toast.makeText(getApplicationContext()," "+ item,Toast.LENGTH_LONG).show();
            }
        });

    }

    private void fetchCommenters() {
        progressBar.setVisibility(View.VISIBLE);
        details.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                postID=task.getResult().child("post").getValue().toString();
                Constants.showLog("wheel post =>"+postID);
                postReference.child(postID).child("Comments").get()
                        .addOnCompleteListener(task1 -> {
                   for(DataSnapshot dataSnapshot:task1.getResult().getChildren()){
                       String commenter=dataSnapshot.child("user").getValue().toString();
                       if(commenters.contains(commenter)){
                       }else{
                           commenters.add(commenter);
                       }
                   }
                   wheelView.setItems(commenters);
                   initiateWheel();
                });
            }
        });
    }

    private void initiateWheel() {
        wheelView.setEnabled(false);
        wheelView.rotate(Constants.MAX_ANGLE, Constants.WHEEL_DURATION, Constants.INTERVELS);
    }
}