package com.integrals.gamermw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.integrals.gamermw.Activities.LoginActivity;
import com.integrals.gamermw.Activities.SettingsActivity;
import com.squareup.picasso.Picasso;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private CircleImageView settingButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);
        settingButton=findViewById(R.id.settingbutton);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("user");
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseUser==null) {
            finishAffinity();
            startActivity(new Intent(this, LoginActivity.class));
        }
        try {
            Objects.requireNonNull(firebaseAuth.getCurrentUser()).reload().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    if(!firebaseAuth.getCurrentUser().isEmailVerified()){
                        finish();
                        Toast.makeText(getApplicationContext(),"Please verify your mail and open again",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        settingButton.setImageDrawable(getDrawable(R.drawable.ic_launcher_round));
        settingButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
        if(firebaseUser!=null) {
            databaseReference.child(firebaseUser.getUid())
                    .child(getString(R.string.dbprofilepic))
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String url = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getValue()).toString();
                    Picasso.get().load(url)
                            .placeholder(R.drawable.ic_account_circle_black_24dp)
                            .into(settingButton);
                }
            });
        }

    }

}