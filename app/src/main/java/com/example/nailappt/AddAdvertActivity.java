package com.example.nailappt;

import static java.lang.Integer.parseInt;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
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

        dateET.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private int prevLength;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dateETLO.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;

                String input = s.toString().replaceAll("[^\\d]", ""); // csak számokat tartunk meg
                StringBuilder formatted = formatDate(input);

                dateET.setText(formatted.toString());
                dateET.setSelection(dateET.getText().length()); // kurzor a végére

                isFormatting = false;

            }
        });

        timeET.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;
            private int prevLength = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timeETLO.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;

                isEditing = true;

                String text = s.toString().replace(":", "");
                if (text.length() > 4) {
                    text = text.substring(0, 4);
                }

                if (text.length() >= 3) {
                    // pl. 930 vagy 0930
                    String hour = text.substring(0, text.length() - 2);
                    String minute = text.substring(text.length() - 2);
                    String formatted = hour + ":" + minute;
                    timeET.setText(formatted);
                    timeET.setSelection(formatted.length());
                } else {
                    timeET.setText(text);
                    timeET.setSelection(text.length());
                }

                isEditing = false;

            }
        });

        postCodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                postCodeETLO.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(postCodeET.getText().toString().isEmpty()){
                    postCodeETLO.setError("A mező kitöltése kötelező!");
                }
            }
        });

        cityET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cityETLO.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(cityET.getText().toString().isEmpty()){
                    cityETLO.setError("A mező kitöltése kötelező!");
                }
            }
        });

        addressET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addressETLO.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(addressET.getText().toString().isEmpty()){
                    addressETLO.setError("A mező kitöltése kötelező!");
                }
            }
        });


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
                    Log.e(LOG_TAG, "Nem található felhasználói adat");
                }
            }).addOnFailureListener(e -> {
                Log.e(LOG_TAG, "Hiba a lekérdezésben", e);
            });
        }

    }

    @NonNull
    private static StringBuilder formatDate(String input) {
        StringBuilder formatted = new StringBuilder();

        if (input.length() > 4) {
            formatted.append(input.substring(0, 4)).append("-");
            if (input.length() > 6) {
                formatted.append(input.substring(4, 6)).append("-");
                formatted.append(input.substring(6, Math.min(8, input.length())));
            } else {
                formatted.append(input.substring(4));
            }
        } else {
            formatted.append(input);
        }
        return formatted;
    }

    private boolean isValidTime(String time){
        if(!time.isEmpty()) {
            String[] timeSplit = time.split(":");
            int hour = parseInt(timeSplit[0]);
            int minute = parseInt(timeSplit[1]);

            return hour <= 23 && hour >= 0 && minute >= 0 && minute <= 59;
        }
        return false;
    }

    private boolean isValidDate(String date){
        if(!date.isEmpty()) {
            String[] dateSplit = date.split("-");
            int year = parseInt(dateSplit[0]);
            int month = parseInt(dateSplit[1]);
            int day = parseInt(dateSplit[2]);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            return year >= currentYear && month >= 1 && month <= 12 && day >= 1 && day <= 31;
        } return false;


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
        Map<String, String> location  = new HashMap<>() {{
            put("postcode",postCode);
            put("city", city);
            put("address", address);
        }};

        if(isValidDate(date) && isValidTime(time) && ((postCodeETLO.getError() == null || postCodeETLO.getError().toString().isEmpty())) &&
                ( cityETLO.getError() == null || cityETLO.getError().toString().isEmpty()) &&
                (addressETLO.getError() == null || addressETLO.getError().toString().isEmpty()))  {
                Appointment newApp = new Appointment(
                        advertiserID,
                        "null",
                        date,
                        time,
                        location,
                        phone,
                        otherContact
                );

                Log.i(LOG_TAG, newApp.toString());

                appointmentRepo.createAppointment(newApp).addOnCompleteListener(appointmentCreation -> {
                    if (appointmentCreation.isSuccessful()) {
                        Log.i(LOG_TAG, "Időpont létrehozása sikeres!");
                        Toast successToast = Toast.makeText(this, "Sikeres létrehozás!", Toast.LENGTH_SHORT);
                        successToast.show();
                        finish();
                    } else {
                        Log.e(LOG_TAG, "Időpont létrehozása sikertelen!");
                    }
                });
        } else {
            if(!isValidDate(date)) {
                dateETLO.setError("Kérem adjon meg egy érvényes dátumot! (pl: 2025-05-28");
            }
            if(!isValidTime(time)){
                timeETLO.setError("Kérem adjon meg egy érvényes időpontot! (pl: 9:30, 17:45test7");
            }
        }

    }


}