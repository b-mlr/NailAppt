package com.example.nailappt;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    private static final String LOG_TAG = SignInActivity.class.getName();
    private static final String PREF_KEY = SignInActivity.class.getPackage().toString();
    private static final int RC_SIGN_IN = 123;
    private static final int SECRET_KEY = 12;
    TextInputEditText emailET;
    TextInputEditText passwordET;
    TextInputLayout emailETLO;
    TextInputLayout passwordETLO;

    Button loginBtn;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);
        passwordETLO = findViewById(R.id.editTextPasswordLayout);
        emailETLO = findViewById(R.id.editTextEmailLayout);
        loginBtn =findViewById(R.id.loginButton);

        emailET.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(emailETLO.getError() != null){
                    emailETLO.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Pattern pattern;
                Matcher matcher;
                String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                pattern = Pattern.compile(EMAIL_PATTERN);
                matcher = pattern.matcher(s);
                if (!(matcher.matches()) && !s.toString().isEmpty()) {
                    emailETLO.setError("Az email-cím formátuma helytelen!");
                }
            }
        });

        passwordET.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passwordETLO.getError() != null){
                    passwordETLO.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }


    private void goToMain(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void login(View view){

        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        if(!email.trim().isEmpty() && !password.trim().isEmpty() ){
            if(emailETLO.getError() != "Az email-cím formátuma helytelen!"){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.i(LOG_TAG, "Successful login: " + email);
                    goToMain();
                } else {
                    Log.d(LOG_TAG, "Login unsuccessful!");
                    emailETLO.setError(" ");
                    passwordETLO.setError("Hibás email-cím vagy jelszó! Próbálja újra!");
                }
            });
            }
        } else {
            if(email.trim().isEmpty()) {
                emailET.setText("");
                emailETLO.setError("A mező kitöltése kötelező!");

            }
            if(password.trim().isEmpty()) {
                passwordET.setText("");
                passwordETLO.setError("A mező kitöltése kötelező!");
            }
        }
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i(LOG_TAG, "firebaseAuthWithGoogle: " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e){
                Log.w(LOG_TAG, "Google sign in failed!", e);
            }
        }
    }



    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.i(LOG_TAG, "Successful Google login: " + idToken);
                goToMain();
            } else {
                Log.d(LOG_TAG, "Google sign in failed!");
                Toast.makeText(SignInActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }

    public void loginAsGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.i(LOG_TAG, "Successful login: guest user");
                goToMain();
            } else {
                Log.d(LOG_TAG,"Login unsuccessful!");
                Toast.makeText(SignInActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loginWithGoogle(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void register(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY",12);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", emailET.getText().toString());
        editor.putString("password", passwordET.getText().toString());
        editor.apply();

        Log.i(LOG_TAG, "onPause");

    }


}