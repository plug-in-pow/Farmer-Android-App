package com.example.farmerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button loginButton,signupButton;
    private EditText mEmail,mPassword;
    private FirebaseAuth fAuth;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginInButtonLogin);
        signupButton = findViewById(R.id.signUpButtonLogin);
        loadingDialog = new LoadingDialog(MainActivity.this);
        mEmail = findViewById(R.id.editTextEmailLogin);
        mPassword = findViewById(R.id.editTextPasswordLogin);
        fAuth = FirebaseAuth.getInstance();

        // To direct the user directly to home screen if logged in
        if(fAuth.getCurrentUser() != null){
            openActivity(HomePage.class,true);
        }

        // Go to Sign up page
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(SignUpActivity.class,false);
            }
        });

        // Log in
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password must be >= 6 characters");
                    return;
                }

                loadingDialog.startLoadingAnimation();

                // authenticate the user
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingDialog.dismissDialog();
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Logged In.", Toast.LENGTH_SHORT).show();
                            openActivity(HomePage.class,true);
                            // startActivity(new Intent(getApplicationContext(), HomePage.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Error !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }

    public void openActivity(Class classname,boolean flag) {
        Intent intent = new Intent(this,classname);
        startActivity(intent);
        if (flag) {
            finish();
        }
    }
}