package com.example.coffee.screens.auth;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coffee.R;
import com.example.coffee.callbacks.AuthCallback;
import com.example.coffee.models.User.UserResponse;
import com.example.coffee.screens.bottom.MainActivity;
import com.example.coffee.services.AuthService;
import com.example.coffee.utils.LayoutLoading;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail;
    EditText edtPassword;
    AppCompatButton btnLogin;
    AppCompatButton btnCreateAccount;
    AuthService authService;
    LayoutLoading layoutLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // mapping
        ConstraintLayout constraintLayout = findViewById(R.id.loading);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        // init
        layoutLoading = new LayoutLoading(constraintLayout,LoginActivity.this);
        authService = new AuthService();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get data
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                LayoutLoading.setLoading();
                authService.login(email, password, authCallback);
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private final AuthCallback authCallback = new AuthCallback() {
        @Override
        public void onSuccess(Boolean value, UserResponse userResponse) {
            LayoutLoading.setGone();

            // handle save user
            User user = userResponse.getUser();
            if (saveUserToShareReference(user)) {
                Toast.makeText(LoginActivity.this, "SAVE USER SUCCESSFULLY", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "SAVE USER FAILED", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onFailed(Boolean value) {
            LayoutLoading.setGone();
            Toast.makeText(LoginActivity.this, "LOGIN FAILED", Toast.LENGTH_SHORT).show();
        }
    };
    
}