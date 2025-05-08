package com.example.nailappt;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class AddAdvertActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();

    TextInputEditText dateET;
    TextInputEditText timeET;
    TextInputEditText postCodeET;
    TextInputEditText cityET;
    TextInputEditText addressET;
    TextInputEditText phoneET;
    TextInputEditText otherContactET;
    TextInputLayout dateETLO;
    TextInputLayout timeETLO;
    TextInputLayout postCodeETLO;
    TextInputLayout cityETLO;
    TextInputLayout addressETLO;
    TextInputLayout phoneETLO;
    TextInputLayout otherContactETLO;
    FirebaseUser currentUser;

    private final UserRepository userRepo = new UserRepository();
    private final AppointmentRepository appointmentRepo = new AppointmentRepository();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_advert);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentUser = mAuth.getCurrentUser();

        dateET = findViewById(R.id.editTextDate);
        timeET = findViewById(R.id.editTextTime);
        postCodeET = findViewById(R.id.editTextPostCode);
        cityET = findViewById(R.id.editTextCity);
        addressET = findViewById(R.id.editTextAddress);
        phoneET = findViewById(R.id.editTextPhoneNum);
        otherContactET = findViewById(R.id.editTextOtherContact);


        dateETLO = findViewById(R.id.editTextDateLayout);
        timeETLO = findViewById(R.id.editTextTimeLayout);
        postCodeETLO = findViewById(R.id.editTextPostCodeLayout);
        cityETLO = findViewById(R.id.editTextCityLayout);
        addressETLO = findViewById(R.id.editTextAddressLayout);
        phoneETLO = findViewById(R.id.editTextPhoneNumLayout);
        otherContactETLO = findViewById(R.id.editTextOtherContactLayout);

        if (currentUser != null) {
            userRepo.getUserbyID(currentUser.getUid()).addOnSuccessListener(userData ->{
                if(userData != null){
                    Map<String, Object> location = (Map<String, Object>) userData.get("location");
                    if (location != null) {
                        if (location.get("postcode") != null) {
                            Log.i(LOG_TAG, location.get("postcode").toString());
                            postCodeET.setText(location.get("postcode").toString());
                        }
                        if (location.get("city") != null) {
                            Log.i(LOG_TAG, location.get("city").toString());
                            cityET.setText(location.get("city").toString());
                        }
                        if (location.get("address") != null) {
                            Log.i(LOG_TAG, location.get("address").toString());
                            addressET.setText(location.get("address").toString());
                        }
                    }
                    if(userData.get("phoneNumber") != null){
                        Log.i(LOG_TAG,userData.get("phoneNumber").toString());
                        phoneET.setText(userData.get("phoneNumber").toString());
                    }
                } else {
                    Log.e(LOG_TAG, "Nem található felhazsnálói adat");
                }
            }).addOnFailureListener(e -> {
                Log.e(LOG_TAG, "Hiba a lekérdezésben", e);
            });
        }

    }

    public void back(View view){
        finish();
    }

    public void addAdvert(View view){
        String advertiserID = currentUser.getUid();
        String date = dateET.getText().toString().trim();
        String time = timeET.getText().toString().trim();
        String postCode = postCodeET.getText().toString().trim();
        String city = cityET.getText().toString().trim();
        String address = addressET.getText().toString().trim();
        String phone = phoneET.getText().toString().trim();
        String otherContact = otherContactET.getText().toString().trim();
        Map<String, String> location  = new HashMap<String, String>() {{
            put("postcode",postCode);
            put("city", city);
            put("address", address);
        }};

        Appointment newApp = new Appointment(
                advertiserID,
                date,
                time,
                postCode,
                location,
                phone,
                otherContact
        );

        appointmentRepo.createAppointment(newApp).addOnCompleteListener( appointmentCreation -> {
           if(appointmentCreation.isSuccessful()){
               Log.i(LOG_TAG,"Időpont létrehozása sikeres!");
           } else {
               Log.e(LOG_TAG,"Időpont létrehozása sikertelen!");
           }
        });



    }


}