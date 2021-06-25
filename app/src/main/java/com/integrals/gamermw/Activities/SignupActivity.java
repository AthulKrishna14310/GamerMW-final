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
   // private ProgressDialog progressDialog;


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
                Log.d("ADHIN","yes");
                findViewById(R.id.stateNumberLayout).setVisibility(View.VISIBLE);
                stateNumber.setVisibility(View.VISIBLE);
            }else if(group.getCheckedRadioButtonId()==R.id.noRadio){
                checked=true;
                findViewById(R.id.stateNumberLayout).setVisibility(View.GONE);
                stateNumber.setVisibility(View.GONE);
                 Log.d("ADHIN","no");

            }
        });



        btnSignUp.setOnClickListener(v -> {
            email = inputEmail.getText().toString().trim();
            password = inputPassword.getText().toString().trim();
            userNameS=userName.getText().toString().trim();

            if(!checked){
                Toast.makeText(getApplicationContext(),"Please check the field ",Toast.LENGTH_LONG).show();
                return;
            }
            if(stateNumber.getVisibility()== View.VISIBLE){
                stateNumberI =stateNumber.getText().toString().trim();
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(userNameS)) {
                Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() <=6) {
                Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Registration failed. " +
                                "Please try again", Toast.LENGTH_LONG).show();
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
                                                Toast.makeText(getApplicationContext(),"Please verify your mail and login again.",Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        }))));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}