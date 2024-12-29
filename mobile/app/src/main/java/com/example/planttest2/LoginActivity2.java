package com.example.planttest2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.planttest2.model.LoginRequest;
import com.example.planttest2.model.LoginResponse;
import com.example.planttest2.model.Utilisateur;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity2 extends BaseActivity {
    private TextView singupbtn;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private Button btnlogin,btnlogingoogle;
    private TextInputEditText login;
    private TextView erreurmsg;
    private TextInputEditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("681429203052-ol8ds2qm7708fhldmlqgielk59479alb.apps.googleusercontent.com") // Remplacez par votre ID de client OAuth 2.0
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        singupbtn=findViewById(R.id.tvSignUp);
        btnlogin=findViewById(R.id.btnLogin);
        btnlogingoogle=findViewById(R.id.button3);
        login=findViewById(R.id.emailinput);
        password=findViewById(R.id.passinput);
        erreurmsg=findViewById(R.id.tvErrorMessage);
        btnlogingoogle.setOnClickListener(view -> signIn());;
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                erreurmsg.setText("");
                if (login.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    erreurmsg.setText("Username et mot de passe sont requis.");
                    return;
                }
                LoginRequest loginRequest=new LoginRequest(login.getText().toString(),password.getText().toString());
                ApiService apiService=RetrofitClient.getInstance("").create(ApiService.class);
                apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if(response.isSuccessful() && response.body()!=null){
                            String accestoken=response.body().getAccessToken();
                            getSharedPreferences("user_prefs", MODE_PRIVATE)
                                    .edit()
                                    .putString("access_token", accestoken)
                                    .apply();

                            Intent intent = new Intent(LoginActivity2.this, MainNavigation.class);
                            finish();
                            startActivity(intent);

                        }

                        else
                            erreurmsg.setText("Username ou mot de pass incorrect");

                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        erreurmsg.setText(t.getMessage());
                    }
                });


            }
        });
        singupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity2.this, SingupActivity.class);
                startActivity(intent);
            }
        });


    }

    // Démarre la connexion
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Google Sign-In",requestCode+"");
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // Récupération des informations du compte
                String idToken = account.getIdToken();
                Log.w("Google Sign-In",idToken );

                String username = account.getDisplayName(); // Nom d'utilisateur
                String email = account.getEmail(); // Email
                Utilisateur user=new Utilisateur(username,email,"");
                user.setGoogleAuth(true);
                ApiService apiService= RetrofitClient.getInstance("").create(ApiService.class);
                apiService.createUser(user).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            LoginRequest loginRequest=new LoginRequest(username,"");
                            ApiService apiService=RetrofitClient.getInstance("").create(ApiService.class);
                            apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
                                @Override
                                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                    if(response.isSuccessful() && response.body()!=null){
                                        String accestoken=response.body().getAccessToken();


                                        getSharedPreferences("user_prefs", MODE_PRIVATE)
                                                .edit()
                                                .putString("access_token", accestoken)
                                                .apply();

                                        Intent intent = new Intent(LoginActivity2.this, MainNavigation.class);
                                        finish();
                                        startActivity(intent);

                                    }



                                }

                                @Override
                                public void onFailure(Call<LoginResponse> call, Throwable t) {
                                    erreurmsg.setText(t.getMessage());
                                }
                            });


                        } else {
                            Toast.makeText(LoginActivity2.this, "Échec de la création de l'utilisateur", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(LoginActivity2.this,t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });


            } catch (ApiException e) {
                // Erreur lors de la connexion
                Log.w("Google Sign-In", "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }




}