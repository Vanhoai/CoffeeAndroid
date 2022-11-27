package com.example.coffee.screens.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.coffee.R;
import com.example.coffee.callbacks.AuthCallback;
import com.example.coffee.models.User.UserResponse;
import com.example.coffee.services.AuthService;
import com.example.coffee.utils.LayoutLoading;
import com.example.coffee.utils.Logger;
import com.example.coffee.utils.Validation;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;
    AppCompatButton btnCreateAccount;
    AuthService authService;
    ImageView backNavigation, checkEmail, checkPassword, checkUsername, checkConfirmPassword;
    ConstraintLayout constraintLayout;
    LayoutLoading layoutLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // init view
        initView();

        // init Service
        authService = new AuthService();

        // handle onclick
        handleOnclick();

        // handle onchange
        handleOnchange();
    }

    private void handleOnchange() {
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = editable.toString();
                validate(value, "EMAIL");
            }
        });

        edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = editable.toString();
                validate(value, "USERNAME");
            }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = editable.toString();
                validate(value, "PASSWORD");
            }
        });

        edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = editable.toString();
                validate(value, "CONFIRM");
            }
        });
    }

    private void handleOnclick() {
        backNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCreateAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // validate
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (Validation.verifyRegister(username, email, password, confirmPassword)) {
            register(username, email, password);
            Logger.log("USERNAME", username);
            Logger.log("EMAIL", email);
            Logger.log("PASSWORD", password);
        } else {
            Toast.makeText(this, "DATA INSIDE IS INVALID", Toast.LENGTH_SHORT).show();
        }
    }

    private void validate(String value, String type) {
        Logger.log("VALUE", value);
        switch (type) {
            case "EMAIL": {
                if (Validation.verifyEmail(value)) {
                    checkEmail.setImageResource(R.drawable.check_active);
                } else {
                    checkEmail.setImageResource(R.drawable.check);
                }
                break;
            }
            case "PASSWORD": {
                if (value.length() > 0) {
                    checkPassword.setImageResource(R.drawable.check_active);
                } else {
                    checkPassword.setImageResource(R.drawable.check);
                }
                break;
            }
            case "USERNAME": {
                if (value.length() > 6) {
                    checkUsername.setImageResource(R.drawable.check_active);
                } else {
                    checkUsername.setImageResource(R.drawable.check);
                }
                break;
            }
            case "CONFIRM": {
                if (edtPassword.getText().toString().trim().equals(value)) {
                    checkConfirmPassword.setImageResource(R.drawable.check_active);
                } else {
                    checkConfirmPassword.setImageResource(R.drawable.check);
                }
            }
            default:
                break;
        }
    }

    public void initView() {
        backNavigation = findViewById(R.id.backNavigation);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        checkEmail = findViewById(R.id.checkEmail);
        checkUsername = findViewById(R.id.checkUsername);
        checkPassword = findViewById(R.id.checkPassword);
        checkConfirmPassword = findViewById(R.id.checkConfirmPassword);
        constraintLayout = findViewById(R.id.loading);
        layoutLoading = new LayoutLoading(constraintLayout, RegisterActivity.this);
        layoutLoading.setGone();
    }


    public void register(String username, String email, String password){
        try {
            layoutLoading.setLoading();
            authService.register(username, email, password, new AuthCallback() {
                @Override
                public void onSuccess(Boolean value, UserResponse userResponse) {
                    layoutLoading.setGone();
                    Logger.log("RESPONSE", userResponse);
                    Intent intent = new Intent(RegisterActivity.this, VerityActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailed(Boolean value) {
                    Toast.makeText(RegisterActivity.this, "REGISTER FAILED", Toast.LENGTH_SHORT).show();
                    layoutLoading.setGone();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            layoutLoading.setGone();
        }
    }
}