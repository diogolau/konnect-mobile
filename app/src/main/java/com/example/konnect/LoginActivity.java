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

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText usernameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.button_login);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter both username and password", Toast.LENGTH_LONG).show();
                    return;
                }

                String url = "http://10.0.2.2:8080/server_war_exploded/api/user/login" ;
                String body = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

                String response = makePostRequest( url, body);
                Log.i("Login response", response);

                try {
                    if (!response.contains("__error__")) {
                        JSONObject responseObject = new JSONObject(response);
                        String message = responseObject.getString("message");
                        JSONObject userObject = new JSONObject(message);

                        String userId = userObject.getString("id");
                        String loggedUsername = userObject.getString("username");

                        Intent intent = new Intent(LoginActivity.this, FeedActivity.class);
                        intent.putExtra("username", loggedUsername);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.i("LoginError", e.toString());
                }
            }
        });

        Button registerButton = findViewById(R.id.button_saiba_mais);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
