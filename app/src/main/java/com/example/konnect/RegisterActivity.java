package com.example.konnect;

import static com.example.konnect.NetworkUtils.makePostRequest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private Button loginButton;
    private Button registerButton;
    private EditText usernameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginButton = findViewById(R.id.button_login);
        registerButton = findViewById(R.id.button_register);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish current activity
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();

                String url = "http://10.0.2.2:8080/server_war_exploded/api/user/register" ;
                String body = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

                String response = makePostRequest(url, body);
                if (!response.contains("__error__")) {
                    Intent intent = new Intent(RegisterActivity.this, FeedActivity.class);
                    startActivity(intent);
                    finish(); // Finish current activity
                } else {
                    Toast.makeText(getBaseContext(), "Erro no registro", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
