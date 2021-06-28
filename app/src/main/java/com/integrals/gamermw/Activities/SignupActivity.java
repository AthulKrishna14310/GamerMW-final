package com.integrals.gamermw.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.integrals.gamermw.Helpers.Constants;
import com.integrals.gamermw.Helpers.CustomToast;
import com.integrals.gamermw.R;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private EditText userName;
    private RadioGroup radioGroupLastShelter;
    private EditText stateNumber;
    private FirebaseDatabase userInfo;
    private String password;
    private String email;
    private String stateNumberI ="no player";
    private String userNameS;
    private String shelterPlayer;
    private String profilePic= Constants.PROFILE_PIC;
    private boolean checked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        auth = FirebaseAuth.getInstance();
        userInfo=FirebaseDatabase.getInstance();
        btnSignIn =     findViewById(R.id.sign_in_button);
        btnSignUp =     findViewById(R.id.sign_up_button);
        inputEmail =    findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar =   findViewById(R.id.progressBar);
        userName=       findViewById(R.id.username);
        radioGroupLastShelter=findViewById(R.id.radiogroup);
        stateNumber=findViewById(R.id.stateNumber);
    }
    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.GONE);
        btnSignIn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(SignupActivity.this,LoginActivity.class));
        });
        radioGroupLastShelter.setOnCheckedChangeListener((group, checkedId) -> {
            if(group.getCheckedRadioButtonId()==R.id.yesRadio){
                checked=true;
                findViewById(R.id.stateNumberLayout).setVisibility(View.VISIBLE);
                stateNumber.setVisibility(View.VISIBLE);
            }else if(group.getCheckedRadioButtonId()==R.id.noRadio){
                checked=true;
                findViewById(R.id.stateNumberLayout).setVisibility(View.GONE);
                stateNumber.setVisibility(View.GONE);
            }
        });
        btnSignUp.setOnClickListener(v -> {
            email = inputEmail.getText().toString().trim();
            password = inputPassword.getText().toString().trim();
            userNameS=userName.getText().toString().trim();

            if(!checked){
                new CustomToast(getApplicationContext()).showErrorToast("Please check the field");
                return;
            }
            if(stateNumber.getVisibility()== View.VISIBLE){
                stateNumberI =stateNumber.getText().toString().trim();
            }

            if (TextUtils.isEmpty(email)) {
                new CustomToast(getApplicationContext()).showErrorToast("Enter Email Address");
                return;
            }

            if (TextUtils.isEmpty(userNameS)) {
                new CustomToast(getApplicationContext()).showErrorToast("Enter username");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                new CustomToast(getApplicationContext()).showErrorToast("Enter Password");
                return;
            }

            if (password.length() <=6) {
                new CustomToast(getApplicationContext()).showErrorToast("Password too short, enter minimum 6 characters");
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            if(auth.getCurrentUser()==null) {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    new CustomToast(getApplicationContext()).showErrorToast("Authentication Failed. Please try again");
                                } else {
                                    sentVerificationEmail();
                                    uploadData();
                                }
                            }
                        });
                 }
        });
    }

    private void sentVerificationEmail() {
        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                    } else {
                        new CustomToast(getApplicationContext()).showErrorToast("Registration Failed. Please try again later.");
                    }
                }
            });
    }
    private void uploadData() {
        userInfo.getReference().child("user").child(auth.getCurrentUser().getUid())
                .child("email").setValue(email)
                .addOnSuccessListener(aVoid -> userInfo.getReference().child("user").child(auth.getCurrentUser().getUid())
                        .child("username").setValue(userNameS+"#"+ stateNumberI +"")
                        .addOnSuccessListener(aVoid1 -> userInfo.getReference().child("user").child(auth.getCurrentUser().getUid())
                                .child("statenumber").setValue(stateNumberI)
                                .addOnSuccessListener(aVoid11 -> userInfo.getReference().child("user").child(auth.getCurrentUser().getUid())
                                        .child("profilepic").setValue(profilePic)
                                        .addOnCompleteListener(task -> {
                                            if(task.isSuccessful()){
                                                progressBar.setVisibility(View.GONE);
                                                new CustomToast(getApplicationContext()).showSuccessToast("Please verify your mail and login again");
                                                finish();
                                            }else{
                                                new CustomToast(getApplicationContext()).showErrorToast(task.getException().getMessage());
                                            }
                                        }))));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}