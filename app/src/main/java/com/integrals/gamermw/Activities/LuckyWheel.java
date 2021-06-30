package com.integrals.gamermw.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adefruandta.spinningwheel.SpinningWheelView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.integrals.gamermw.Helpers.Constants;
import com.integrals.gamermw.Helpers.CustomToast;
import com.integrals.gamermw.MainActivity;
import com.integrals.gamermw.R;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LuckyWheel extends AppCompatActivity {
    private ArrayList<String> commenters;
    private SpinningWheelView wheelView;
    private DatabaseReference details;
    private ProgressBar progressBar;
    private String postID;
    private DatabaseReference postReference;
    private DatabaseReference userReference;
    private MaterialButton startWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_lucky_wheel);
        wheelView =findViewById(R.id.wheel);
        progressBar=findViewById(R.id.wheelProgress);
        commenters=new ArrayList<>();
        progressBar.setVisibility(View.GONE);
        details= FirebaseDatabase.getInstance().getReference().child("GiveAway");
        postReference=FirebaseDatabase.getInstance().getReference().child("Posts");
        userReference=FirebaseDatabase.getInstance().getReference().child("user");
        startWheel=findViewById(R.id.startWheel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchCommenters();
        startWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateWheel();
            }
        });

        wheelView.setOnRotationListener(new SpinningWheelView.OnRotationListener<String>() {
            @Override
            public void onRotation() {
                Toast.makeText(getApplicationContext(),"Rotating",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStopRotation(String item) {
                Toast.makeText(getApplicationContext()," "+ item,Toast.LENGTH_LONG).show();
                userReference.child(item).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            String winnerUser=task.getResult().child("username").getValue().toString();
                            details.child("winner").setValue(winnerUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                 if(task.isSuccessful()){
                                     initiateDialog(winnerUser);
                                 }else{
                                   new CustomToast(LuckyWheel.this).showErrorToast(task.getException().getMessage());
                                 }
                                }
                            });


                        }
                    }
                });
            }
        });


    }

    private void initiateDialog(String winnerUser) {
        new AestheticDialog.Builder(this, DialogStyle.FLAT, DialogType.SUCCESS)
                .setTitle("Winner "+winnerUser)
                .setMessage("Message")
                .setCancelable(false)
                .setDarkMode(true)
                .setGravity(Gravity.CENTER)
                .setAnimation(DialogAnimation.SHRINK)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NotNull AestheticDialog.Builder builder) {
                        builder.dismiss();
                    }
                }).show();
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
                   progressBar.setVisibility(View.INVISIBLE);
                });
            }else{
                new CustomToast(LuckyWheel.this).showErrorToast(task.getException().getMessage());
            }
        });
    }

    private void initiateWheel() {
        wheelView.setEnabled(false);
        wheelView.rotate(Constants.MAX_ANGLE, Constants.WHEEL_DURATION, Constants.INTERVELS);
    }
}