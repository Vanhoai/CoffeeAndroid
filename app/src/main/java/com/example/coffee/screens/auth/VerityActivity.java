package com.example.coffee.screens.auth;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.coffee.R;
import com.example.coffee.callbacks.AuthCallback;
import com.example.coffee.models.User.User;
import com.example.coffee.models.User.UserResponse;
import com.example.coffee.services.UserService;
import com.example.coffee.utils.LayoutLoading;
import com.example.coffee.utils.Logger;
import com.example.coffee.utils.Storage;
import com.example.coffee.utils.UserInformation;
import com.example.coffee.utils.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerityActivity extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private String verificationId;
    EditText edtPhone;
    ImageView checkPhone, backNavigation;
    AppCompatButton btnVerify;
    UserService userService;
    ConstraintLayout constraintLayout;
    LayoutLoading layoutLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verity);

        // inti view
        initView();

        // init service
        userService = new UserService();

        // handle onclick
        handleOnclick();
    }

    private void initView() {
        edtPhone = findViewById(R.id.edtPhone);
        checkPhone = findViewById(R.id.checkPhone);
        btnVerify = findViewById(R.id.btnVerify);
        backNavigation = findViewById(R.id.backNavigation);
        constraintLayout = findViewById(R.id.loading);
        layoutLoading = new LayoutLoading(constraintLayout, VerityActivity.this);
        layoutLoading.setGone();
    }

    private void handleOnclick() {
        btnVerify.setOnClickListener(this);
        backNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerityActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        String phone = edtPhone.getText().toString().trim();
        if (Validation.verifyPhoneNumber(phone)) {
            verify(phone);
        } else {
            Toast.makeText(this, "PHONE NUMBER INVALID", Toast.LENGTH_SHORT).show();
        }
    }

    public void verify(String phone) {
        layoutLoading.setLoading();
        String value = String.format("+84%s", phone.substring(1));
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(value)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this).setCallbacks(onVerificationStateChangedCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Logger.log("PHONE VERIFY", "ON VERIFICATION COMPLETED");
            verifyCode(phoneAuthCredential.getSmsCode());
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(getApplicationContext(), "REQUEST FAILED", Toast.LENGTH_SHORT).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(getApplicationContext(), "QUOTA NOT ENOUGH", Toast.LENGTH_SHORT).show();
            }
            Logger.log("PHONE VERIFY", "ON VERIFICATION FAILED");
            layoutLoading.setGone();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            forceResendingToken = token;
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    assert user != null;
                    updateUser(user.getPhoneNumber());
                } else {
                    Logger.log("ERROR", Objects.requireNonNull(task.getException()));
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Logger.log("ERROR", Objects.requireNonNull(task.getException()));
                    }
                    layoutLoading.setGone();
                }
            }
        });
    }

    private void updateUser (String phone) {
        try {
            String value = String.format("+84%s", phone.substring(1));
            User user = UserInformation.getUser(VerityActivity.this);
            userService.updatePhoneNumber(user.getId(), value, new AuthCallback() {
                @Override
                public void onSuccess(Boolean value, UserResponse userResponse) {
                    Logger.log("RESPONSE", userResponse);
                    layoutLoading.setGone();
                    Intent intent = new Intent(VerityActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailed(Boolean value) {
                    layoutLoading.setGone();
                    Logger.log("RESPONSE", "ERROR");
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}