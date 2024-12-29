package com.example.planttest2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planttest2.api.ApiService;
import com.example.planttest2.config.RetrofitClient;
import com.example.planttest2.model.Utilisateur;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingupActivity extends BaseActivity {
    private TextView btnlogin;
    private Button btnregister;
    private TextInputEditText username,email,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        btnlogin=findViewById(R.id.tvSignIn);
        username=findViewById(R.id.usernameinput);
        email=findViewById(R.id.emailinput);
        pass=findViewById(R.id.mdpinput);
        btnregister=findViewById(R.id.btnRegister);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilisateur user=new Utilisateur(username.getText().toString(),email.getText().toString(),pass.getText().toString());
                ApiService apiService= RetrofitClient.getInstance("").create(ApiService.class);
                apiService.createUser(user).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(SingupActivity.this, "Utilisateur créé avec succès", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SingupActivity.this, LoginActivity2.class);
                            finish();
                            startActivity(intent);
                        } else {
                            Toast.makeText(SingupActivity.this, "Échec de la création de l'utilisateur", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(SingupActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingupActivity.this, LoginActivity2.class);
                finish();
                startActivity(intent);
            }
        });

    }
}