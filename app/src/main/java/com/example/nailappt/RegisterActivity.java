package com.example.nailappt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    TextInputEditText emailET;
    TextInputEditText passwordET;
    TextInputEditText passwordAgainET;
    TextInputEditText surnameET;
    TextInputEditText firstNameET;
    TextInputEditText phoneET;
    TextInputLayout emailETLO;
    TextInputLayout passwordETLO;
    TextInputLayout passwordAgainETLO;
    TextInputLayout surnameETLO;
    TextInputLayout firstNameETLO;
    TextInputLayout phoneETLO;
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

        passwordETLO = findViewById(R.id.editTextPasswordLayout);
        emailETLO = findViewById(R.id.editTextEmailLayout);
        passwordAgainETLO = findViewById(R.id.editTextPasswordAgainLayout);
        firstNameETLO = findViewById(R.id.editTextFirstNameLayout);
        surnameETLO = findViewById(R.id.editTextSurnameLayout);
        phoneETLO = findViewById(R.id.editTextPhoneNumLayout);


        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = preferences.getString("email","");
        String password = preferences.getString("password","");

        emailET.setText(email);
        passwordET.setText(password);

        if(!password.isEmpty() && passwordET.length() < 6){
            passwordETLO.setError("A jelszónak min. 6 karakter hosszúnak kell lennie!");
        }

        mAuth = FirebaseAuth.getInstance();

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
                    emailETLO.setError("Érvénytelen email-cím!");
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
                if(passwordET.length() < 6){
                    passwordETLO.setError("A jelszónak min. 6 karakter hosszúnak kell lennie!");
                }
                if(!passwordET.getText().toString().trim().equals(passwordAgainET.getText().toString().trim())){
                    passwordAgainETLO.setError("Nem egyezik a jelszó megerősítése!");
                } else {
                    passwordAgainETLO.setError(null);
                }
            }
        });

        passwordAgainET.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passwordAgainETLO.getError() != null){
                    passwordAgainETLO.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!passwordET.getText().toString().trim().equals(passwordAgainET.getText().toString().trim())){
                    passwordAgainETLO.setError("Nem egyezik a jelszó megerősítése!");
                }
            }
        });

        surnameET.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(surnameETLO.getError() != null){
                    surnameETLO.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        firstNameET.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(firstNameETLO.getError() != null){
                    firstNameETLO.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        phoneET.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(phoneETLO.getError() != null){
                    phoneETLO.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void back(View view) {
        finish();
    }

    private void goToBase(){
        Intent intent = new Intent(this, BaseActivity.class);
        intent.putExtra("fragmentToOpen", "booking");
        startActivity(intent);
    }

    public void register(View view){
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String passwordAgain = passwordAgainET.getText().toString().trim();

        String surname = surnameET.getText().toString().trim();
        String firstName = firstNameET.getText().toString().trim();
        String phone = phoneET.getText().toString().trim();


        Log.i(LOG_TAG, "Regisztrált: " + email + ", jelszó: " + password);

        if(!email.isEmpty() && !password.isEmpty() && !passwordAgain.isEmpty() && !surname.isEmpty() && !firstName.isEmpty() && phoneETLO.getError() == null) {
            if(emailETLO.getError() == null && passwordETLO.getError() == null && passwordAgainETLO.getError() == null) {

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();

                        User newUser = new User(
                                uid,
                                email,
                                firstName,
                                surname
                        );

                        if(!phone.isEmpty() ){
                            newUser.setPhone(phone);
                        }

                        UserRepository userRepo = new UserRepository();

                        userRepo.createUser(newUser).addOnCompleteListener( userCreation -> {
                            if(userCreation.isSuccessful()){
                                Log.i(LOG_TAG, "Firebase user létrehozás sikeres!");
                            } else {
                                Log.i(LOG_TAG, "Firebase user létrehozás sikertelen!");
                            }
                        });

                        Log.i(LOG_TAG, "User created successfully!");
                        Toast.makeText(getApplicationContext(), "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
                        goToBase();
                    } else {
                        Log.d(LOG_TAG, "There was an issue while creating the user!");
                        phoneETLO.setError(task.getException().getMessage());
                        emailETLO.setError(" ");
                        passwordETLO.setError(" ");
                        passwordAgainETLO.setError(" ");
                        surnameETLO.setError(" ");
                        firstNameETLO.setError(" ");
                    }
                });
            }
        } else {
            if(email.isEmpty()) {
                emailET.setText("");
                emailETLO.setError("A mező kitöltése kötelező!");

            }
            if(password.isEmpty()) {
                passwordET.setText("");
                passwordETLO.setError("A mező kitöltése kötelező!");
            }
            if(passwordAgain.isEmpty()) {
                passwordAgainET.setText("");
                passwordAgainETLO.setError("A mező kitöltése kötelező!");
            }
            if(surname.isEmpty()) {
                surnameET.setText("");
                surnameETLO.setError("A mező kitöltése kötelező!");
            }
            if(firstName.isEmpty()) {
                firstNameET.setText("");
                firstNameETLO.setError("A mező kitöltése kötelező!");
            }
        }

    }
}

