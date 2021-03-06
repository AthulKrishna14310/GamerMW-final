package com.integrals.gamermw.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.integrals.gamermw.Helpers.CustomToast;
import com.integrals.gamermw.MainActivity;
import com.integrals.gamermw.R;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        inputEmail =    findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar =   findViewById(R.id.progressBar);
        btnSignup =     findViewById(R.id.btn_signup);
        btnLogin =      findViewById(R.id.btn_login);
        btnReset =      findViewById(R.id.btn_reset_password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.GONE);
        btnSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
        btnReset.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class)));
        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString();
            final String password = inputPassword.getText().toString();
            if (TextUtils.isEmpty(email)) {
                new CustomToast(getApplicationContext()).showErrorToast("Enter email address");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                new CustomToast(getApplicationContext()).showErrorToast("Enter Password");
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            new CustomToast(getApplicationContext()).showErrorToast(task.getException().getMessage());
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                               progressBar.setVisibility(View.GONE);
                            }
                        }else if (!auth.getCurrentUser().isEmailVerified()){
                            progressBar.setVisibility(View.GONE);
                            new CustomToast(getApplicationContext()).showErrorToast("Email not verified");
                        }else{
                            new CustomToast(getApplicationContext()).showSuccessToast("Successfully logged in ");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        });
    }
}
