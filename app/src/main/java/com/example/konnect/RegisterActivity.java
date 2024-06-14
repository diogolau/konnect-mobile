package com.example.konnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        registerButton = findViewById(R.id.button_register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Preencha todos os campos", Toast.LENGTH_LONG).show();
                    return;
                }

                String url = "http://10.0.2.2:8080/server_war_exploded/api/user/register";
                String body = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

                makePostRequest(url, body, new NetworkUtils.NetworkCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Log.i("RegisterResponse", response);

                        try {
                            if (!response.contains("__error__")) {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("message");
                                JSONObject userObject = new JSONObject(message);
                                String userId = userObject.getString("id");
                                String usernameResponse = userObject.getString("username");

                                Intent intent = new Intent(RegisterActivity.this, FeedActivity.class);
                                intent.putExtra("username", usernameResponse);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Erro no registro", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.i("RegisterError", e.toString());
                        Toast.makeText(RegisterActivity.this, "Erro no registro", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void makePostRequest(String url, String body, NetworkUtils.NetworkCallback callback) {
        NetworkUtils.makePostRequest(url, body, callback);
    }
}
