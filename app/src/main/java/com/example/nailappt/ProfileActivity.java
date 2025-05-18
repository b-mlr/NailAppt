package com.example.nailappt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements AppointmentAdapter.OnBookListener, AppointmentAdapter.OnCallListener {

    TextView titleTV;
    TextView myDataTV;
    TextView nameTitleTV;
    TextView nameTV;
    TextView locationTitleTV;
    TextView locationTV;
    TextView phoneTitleTV;
    TextView phoneTV;
    TextView otherContactTitleTV;
    TextView otherContactTV;
    TextView myAppointmentsTV;

    TextInputLayout lastNameETLO;
    TextInputLayout firstNameETLO;
    TextInputLayout postCodeETLO;
    TextInputLayout cityETLO;
    TextInputLayout addressETLO;
    TextInputLayout phoneETLO;
    TextInputLayout otherContactETLO;

    EditText lastNameET;
    EditText firstNameET;
    EditText postCodeET;
    EditText cityET;
    EditText addressET;
    EditText phoneET;
    EditText otherContactET;

    Button saveBtn;
    Button cancelBtn;
    Button editBtn;
    Button logoutBtn;

    private RecyclerView appointmentRW;
    private ArrayList<Appointment> myAppointmentList = new ArrayList<>();
    private AppointmentAdapter mAdapter;
    private final AppointmentRepository appointmentRepo = new AppointmentRepository();
    private final UserRepository userRepo = new UserRepository();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser =  mAuth.getCurrentUser();
    private static final String LOG_TAG = AdvertiseFragment.class.getName();
    private static final int REQUEST_CALL_PERMISSION = 1;
    private String pendingPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleTV = findViewById(R.id.titleTextView);
        myDataTV = findViewById(R.id.myData);
        nameTitleTV = findViewById(R.id.nameTitleTextview);
        nameTV = findViewById(R.id.nameTextview);
        locationTitleTV = findViewById(R.id.locationTitleTextview);
        locationTV = findViewById(R.id.locationTextview);
        phoneTitleTV = findViewById(R.id.phoneTitleTextview);
        phoneTV = findViewById(R.id.phoneTextview);
        otherContactTitleTV = findViewById(R.id.otherContactTitleTextview);
        otherContactTV = findViewById(R.id.otherContactTextview);
        myAppointmentsTV = findViewById(R.id.myAppointments);
        editBtn = findViewById(R.id.editBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        lastNameETLO = findViewById(R.id.lastNameETLO);
        lastNameET = findViewById(R.id.lastNameET);
        firstNameETLO = findViewById(R.id.firstNameETLO);
        firstNameET = findViewById(R.id.firstNameET);
        postCodeETLO = findViewById(R.id.postCodeETLO);
        postCodeET = findViewById(R.id.postCodeET);
        cityETLO = findViewById(R.id.cityETLO);
        cityET = findViewById(R.id.cityET);
        addressETLO = findViewById(R.id.addressETLO);
        addressET = findViewById(R.id.addressET);
        phoneETLO = findViewById(R.id.phoneETLO);
        phoneET = findViewById(R.id.phoneET);
        otherContactETLO = findViewById(R.id.otherContactETLO);
        otherContactET = findViewById(R.id.otherContactET);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetProfileView();
            }
        });


        mAdapter = new AppointmentAdapter(this, myAppointmentList, "profileActivity", this, this);

        appointmentRW = findViewById(R.id.myAppointmentsRecycler);
        appointmentRW.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        appointmentRW.setAdapter(mAdapter);

        if( currentUser != null) {
            Log.d(LOG_TAG,"Belépett felhasználó");
            loadUserData();
        } else {
            Log.d(LOG_TAG,"Nem belépett felhasználó");
        }

        if(myAppointmentList != null){
            myAppointmentList.clear();
        }

        fillMyAppointmentsList();

        lastNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastNameETLO.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(lastNameET.getText().toString().trim().isEmpty()){
                    lastNameETLO.setError("A mező kitöltése kötelező!");
                }
            }
        });

        firstNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firstNameETLO.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(firstNameET.getText().toString().trim().isEmpty()){
                    firstNameETLO.setError("A mező kitöltése kötelező!");
                }
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
                if(postCodeET.getText().toString().trim().isEmpty()){
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
                if(cityET.getText().toString().trim().isEmpty()){
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
                if(addressET.getText().toString().trim().isEmpty()){
                    addressETLO.setError("A mező kitöltése kötelező!");
                }
            }
        });


    }



    public void back(View view) {
        finish();
    }

    public void logout(View view) {
        mAuth.signOut();

        Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    public void loadUserData(){
        userRepo.getUserbyID(currentUser.getUid()).addOnSuccessListener( userData -> {
            if(userData != null) {
                if (userData.get("lastName") != null) {
                    nameTV.setText(" " + userData.get("lastName").toString() + " ");
                }

                if (userData.get("firstName") != null) {
                    nameTV.append(userData.get("firstName").toString());
                }

                Map<String, Object> location = (Map<String, Object>) userData.get("location");

                if (location != null) {
                    if (location.get("postcode") != null) {
                        locationTV.setText(" " +location.get("postcode").toString() + ", ");
                    }
                    if (location.get("city") != null) {
                        Log.i(LOG_TAG, location.get("city").toString());
                        locationTV.append(location.get("city").toString() + " ");
                    }
                    if (location.get("address") != null) {
                        Log.i(LOG_TAG, location.get("address").toString());
                        locationTV.append(location.get("address").toString());
                    }
                }

                if(userData.get("phone") != null && !userData.get("phone").toString().isEmpty()){
                    Log.i(LOG_TAG,userData.get("phone").toString());
                    phoneTitleTV.setVisibility(View.VISIBLE);
                    phoneTV.setVisibility(View.VISIBLE);
                    phoneTV.setText(userData.get("phone").toString());
                } else {
                    phoneTitleTV.setVisibility(View.GONE);
                    phoneTV.setVisibility(View.GONE);
                }

                if(userData.get("otherContact") != null && !userData.get("otherContact").toString().isEmpty()){
                    Log.i(LOG_TAG,userData.get("otherContact").toString());
                    otherContactTitleTV.setVisibility(View.VISIBLE);
                    otherContactTV.setVisibility(View.VISIBLE);
                    otherContactTV.setText(userData.get("otherContact").toString());
                } else {
                    otherContactTitleTV.setVisibility(View.GONE);
                    otherContactTV.setVisibility(View.GONE);
                }
            }
        });
    }

    public void fillMyAppointmentsList(){
        appointmentRepo.getMyAppointments(currentUser.getUid())
                .addOnSuccessListener(querySnapshot -> {

                    for (DocumentSnapshot document : querySnapshot){
                        Appointment appointment = document.toObject(Appointment.class);
                        Log.i(LOG_TAG,appointment.toString());
                        myAppointmentList.add(appointment);
                        mAdapter.notifyItemInserted(myAppointmentList.size()-1);
                    }
                    Log.d(LOG_TAG, "Appointments list size: " + myAppointmentList.size() +"\n GetItemCount:" + mAdapter.getItemCount());
                    mAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba a lekérdezésben: ", e));
    }

    @Override
    public void onBookRequested(Appointment appointment) {

    }

    public void resetProfileView() {
        loadUserData();

        lastNameETLO.setVisibility(View.GONE);
        lastNameET.setVisibility(View.GONE);
        firstNameETLO.setVisibility(View.GONE);
        firstNameET.setVisibility(View.GONE);
        postCodeETLO.setVisibility(View.GONE);
        postCodeET.setVisibility(View.GONE);
        cityETLO.setVisibility(View.GONE);
        cityET.setVisibility(View.GONE);
        addressETLO.setVisibility(View.GONE);
        addressET.setVisibility(View.GONE);
        phoneETLO.setVisibility(View.GONE);
        phoneET.setVisibility(View.GONE);
        otherContactETLO.setVisibility(View.GONE);
        otherContactET.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);

        titleTV.setText("Profil");
        myDataTV.setVisibility(View.VISIBLE);
        myAppointmentsTV.setVisibility(View.VISIBLE);
        appointmentRW.setVisibility(View.VISIBLE);
        nameTitleTV.setVisibility(View.VISIBLE);
        nameTV.setVisibility(View.VISIBLE);
        locationTitleTV.setVisibility(View.VISIBLE);
        locationTV.setVisibility(View.VISIBLE);
        editBtn.setVisibility(View.VISIBLE);
        logoutBtn.setVisibility(View.VISIBLE);
    }

    public void editProfile(View view) {
        titleTV.append(" szerkesztése");
        myDataTV.setVisibility(View.GONE);
        myAppointmentsTV.setVisibility(View.GONE);
        appointmentRW.setVisibility(View.GONE);
        nameTitleTV.setVisibility(View.GONE);
        nameTV.setVisibility(View.GONE);
        locationTitleTV.setVisibility(View.GONE);
        locationTV.setVisibility(View.GONE);
        phoneTitleTV.setVisibility(View.GONE);
        phoneTV.setVisibility(View.GONE);
        otherContactTitleTV.setVisibility(View.GONE);
        otherContactTV.setVisibility(View.GONE);
        editBtn.setVisibility(View.GONE);
        logoutBtn.setVisibility(View.GONE);

        lastNameETLO.setVisibility(View.VISIBLE);
        lastNameET.setVisibility(View.VISIBLE);
        firstNameETLO.setVisibility(View.VISIBLE);
        firstNameET.setVisibility(View.VISIBLE);
        postCodeETLO.setVisibility(View.VISIBLE);
        postCodeET.setVisibility(View.VISIBLE);
        cityETLO.setVisibility(View.VISIBLE);
        cityET.setVisibility(View.VISIBLE);
        addressETLO.setVisibility(View.VISIBLE);
        addressET.setVisibility(View.VISIBLE);
        phoneETLO.setVisibility(View.VISIBLE);
        phoneET.setVisibility(View.VISIBLE);
        otherContactETLO.setVisibility(View.VISIBLE);
        otherContactET.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);

        userRepo.getUserbyID(currentUser.getUid()).addOnSuccessListener( userData -> {
            if(userData != null) {
                if (userData.get("lastName") != null) {
                    lastNameET.setText(userData.get("lastName").toString());
                }

                if (userData.get("firstName") != null) {
                    firstNameET.setText(userData.get("firstName").toString());
                }

                Map<String, Object> location = (Map<String, Object>) userData.get("location");

                if (location != null) {
                    if (location.get("postcode") != null) {
                        postCodeET.setText(location.get("postcode").toString());
                    }
                    if (location.get("city") != null) {
                        cityET.setText(location.get("city").toString());
                    }
                    if (location.get("address") != null) {
                        addressET.setText(location.get("address").toString());
                    }
                }

                if(userData.get("phone") != null){
                    phoneET.setText(userData.get("phone").toString());

                }

                if(userData.get("otherContact") != null){
                    otherContactET.setText(userData.get("otherContact").toString());
                }
            }

        });
    }


    public void saveProfileChanges(View view) {
        if(postCodeET.getText().toString().trim().isEmpty()){
            postCodeETLO.setError("A mező kitöltése kötelező!");
        }
        if(cityET.getText().toString().trim().isEmpty()){
            cityETLO.setError("A mező kitöltése kötelező!");
        }
        if(addressET.getText().toString().trim().isEmpty()){
            addressETLO.setError("A mező kitöltése kötelező!");
        }
        if(((lastNameETLO.getError() == null || lastNameETLO.getError().toString().isEmpty()) &&
                (firstNameETLO.getError() == null || firstNameETLO.getError().toString().isEmpty()) &&
                (postCodeETLO.getError() == null || postCodeETLO.getError().toString().isEmpty())) &&
                (cityETLO.getError() == null || cityETLO.getError().toString().isEmpty()) &&
                (addressETLO.getError() == null || addressETLO.getError().toString().isEmpty()))  {
            String postCode = postCodeET.getText().toString();
            String city = cityET.getText().toString();
            String address = addressET.getText().toString();
            Map<String, String> location = new HashMap<String, String>(){{
                put("postcode",postCode);
                put("city",city);
                put("address",address);
            }};

            User updatedUser = new User(
                    currentUser.getUid(),
                    currentUser.getEmail(),
                    firstNameET.getText().toString(),
                    lastNameET.getText().toString(),
                    location,
                    phoneET.getText().toString().equals("") ? "" : phoneET.getText().toString(),
                    otherContactET.getText().toString().equals("") ? "" : otherContactET.getText().toString()
            );

            Log.i(LOG_TAG,updatedUser.toString());

            userRepo.updateUser(updatedUser).addOnCompleteListener( task -> {
                if(task.isSuccessful()){
                    Toast successToast = Toast.makeText(this,"Sikeres frissítés!",Toast.LENGTH_SHORT);
                    successToast.show();

                    resetProfileView();
                    loadUserData();
                } else {
                    Toast failToast = Toast.makeText(this,"Sikertelen frissítés!",Toast.LENGTH_SHORT);
                    failToast.show();
                }
            });
        }

    }

    private void makePhoneCall(String phoneNumber){
        this.pendingPhoneNumber = phoneNumber;
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL_PERMISSION);
        } else {
            startCallIntent(phoneNumber);
        }
    }

    private void startCallIntent(String phoneNumber){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCallIntent(pendingPhoneNumber);
            } else {
                Toast.makeText(this, "Telefonhívás indításához engedély szükséges", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCallButtonClicked(String phoneNumber) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Hívás indítása")
                .setMessage("Biztosan fel szeretné venni a kapcsolatot az időpont meghirdetőjével?")
                .setNegativeButton("Mégse", ((dialog, which) -> {
                    dialog.dismiss();
                }))
                .setPositiveButton("Hívás", ((dialog, which) -> {
                    makePhoneCall(phoneNumber);
                }))
                .show();
    }
}