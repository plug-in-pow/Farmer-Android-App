package com.example.farmerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button signUpButton;
    private EditText mFullname, mEmail, mPassword, mPhone;
    private LoadingDialog loadingDialog;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;
    private static final String TAG = SignUpActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        backButton = findViewById(R.id.backNavigationButton);
        mFullname = findViewById(R.id.editTextPersonNameSignUp);
        mEmail = findViewById(R.id.editTextEmailSignUp);
        mPassword = findViewById(R.id.editTextPasswordSignUp);
        mPhone = findViewById(R.id.editTextPhoneSignUp);
        signUpButton = findViewById(R.id.signUpButtonSignUp);
        loadingDialog = new LoadingDialog(SignUpActivity.this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null){
            openActivity(HomePage.class,true);
        }

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String name = mFullname.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();

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

                if (name.length() < 3){
                    mFullname.setError("Name must be >= 2 characters");
                    return;
                }

                if (phone.length() != 10){
                    mPhone.setError("Phone number must be of 10 digits");
                    return;
                }

                loadingDialog.startLoadingAnimation();

               // register the user
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "User Created.", Toast.LENGTH_SHORT).show();

                            userId = fAuth.getCurrentUser().getUid();

                            DocumentReference documentReference = fStore.collection("user").document(userId);

                            Map<String,Object> user = new HashMap<>();
                            user.put("fullName",name);
                            user.put("email",email);
                            user.put("phone",phone);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   Log.d(TAG,"User created successfully" + userId);
                                }
                            });

                            loadingDialog.dismissDialog();

                            openActivity(HomePage.class,true);
                        } else {
                            loadingDialog.dismissDialog();
                            Toast.makeText(SignUpActivity.this, "Error !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}