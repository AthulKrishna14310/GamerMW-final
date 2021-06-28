package com.integrals.gamermw.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.integrals.gamermw.Helpers.CustomToast;
import com.integrals.gamermw.MainActivity;
import com.integrals.gamermw.Models.ChatModel;
import com.integrals.gamermw.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Button logoutButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userId;
    private String username;
    private String state;
    private String url;
    private EditText usernameInput;
    private EditText stateInput;
    private CircleImageView imageView;
    private MaterialButton updateButton;
    private LinearProgressIndicator progressIndicator;
    private ExtendedFloatingActionButton updateUserImage;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_settings);
        logoutButton=findViewById(R.id.signoutbtn);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child("messages");
        userId=firebaseAuth.getCurrentUser().getUid();
        usernameInput=findViewById(R.id.user_name_activity_edittext);
        stateInput=findViewById(R.id.user_state_activity_edittext);
        imageView=findViewById(R.id.profilepic_update);
        updateButton =findViewById(R.id.update);
        updateUserImage =findViewById(R.id.user_image_select);
        progressIndicator=findViewById(R.id.progress);
        progressIndicator.setVisibility(View.INVISIBLE);
        updateButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.child("user").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadData(snapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new CustomToast(getApplicationContext()).showErrorToast(error.getMessage());
            }
        });

        logoutButton.setOnClickListener(v -> {
            logout();
        });

        updateButton.setOnClickListener(v -> updateData());
        updateUserImage.setOnClickListener(v -> {
            CropImage.activity().start(SettingsActivity.this);
        });
    }

    private void loadData(@NonNull DataSnapshot snapshot) {
        progressIndicator.setVisibility(View.VISIBLE);
        username = snapshot.child(getString(R.string.dbusername)).getValue().toString().trim();
        state = snapshot.child(getString(R.string.dbstatenumber)).getValue().toString().trim();
        url = snapshot.child(getString(R.string.dbprofilepic)).getValue().toString();
        int length = username.indexOf("#");
        usernameInput.setText(username.substring(0, length));
        stateInput.setText(state);
        Picasso.get().load(url).placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressIndicator.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressIndicator.setVisibility(View.INVISIBLE);
                        new CustomToast(SettingsActivity.this).showErrorToast(e.getMessage());
                    }
                });

        implementListners();
    }

    private void logout() {
        firebaseAuth.signOut();
        finishAffinity();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }

    private void updateData() {
        databaseReference.child("user").child(userId).child(getString(R.string.dbusername))
                .setValue(usernameInput.getText().toString()+"#"+stateInput.getText().toString().trim());
        databaseReference.child("user").child(userId).child(getString(R.string.dbstatenumber))
                .setValue(stateInput.getText().toString().trim() + "");
        updateButton.setText("Updated");
    }

    private void implementListners() {
        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateButton.setVisibility(View.VISIBLE);

            }
        });

        stateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                updateButton.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                StorageReference riversRef = storageReference.child("images/" + resultUri.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(resultUri);
                progressIndicator.setVisibility(View.VISIBLE);
                uploadTask.addOnFailureListener(exception -> {
                    progressIndicator.setVisibility(View.INVISIBLE);
                    new CustomToast(getApplicationContext()).showErrorToast(exception.getMessage());
                }).addOnSuccessListener(taskSnapshot -> {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            databaseReference.child("user")
                                    .child(userId)
                                    .child(getString(R.string.dbprofilepic))
                                    .setValue(uri.toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressIndicator.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        progressIndicator.setVisibility(View.VISIBLE);
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                new CustomToast(getApplicationContext()).showErrorToast(error.getMessage());
                progressIndicator.setVisibility(View.INVISIBLE);
            }
        }
    }
}