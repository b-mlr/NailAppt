package com.example.nailappt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    TextInputEditText emailET;
    TextInputEditText passwordET;
    TextInputEditText passwordAgainET;
    TextInputEditText surnameET;
    TextInputEditText firstNameET;
    TextInputEditText phoneET;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if(secret_key != 12){
            finish();
        }

        emailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);
        passwordAgainET = findViewById(R.id.editTextPasswordAgain);
        surnameET = findViewById(R.id.editTextSurname);
        firstNameET = findViewById(R.id.editTextFirstName);
        phoneET = findViewById(R.id.editTextPhoneNum);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = preferences.getString("email","");
        String password = preferences.getString("password","");

        emailET.setText(email);
        passwordET.setText(password);

        mAuth = FirebaseAuth.getInstance();
    }

    public void back(View view) {
        finish();
    }

    private void goToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void register(View view){
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordAgain = passwordAgainET.getText().toString();


        if(!password.equals(passwordAgain)){
            Log.e(LOG_TAG, "Nem egyezik a jelszó megerősítése.");
            return;
        }

        String surname = surnameET.getText().toString();
        String firstName = firstNameET.getText().toString();
        String phone = phoneET.getText().toString();


        Log.i(LOG_TAG, "Regisztrált: " + email + ", jelszó: " + password);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(LOG_TAG,"User created successfully!");
                    goToHome();
                } else {
                    Log.d(LOG_TAG,"There was an issue while creating the user!");
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}

