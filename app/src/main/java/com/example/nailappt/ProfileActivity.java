package com.example.nailappt;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements AppointmentAdapter.OnBookListener {

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


        mAdapter = new AppointmentAdapter(this, myAppointmentList, "profileActivity", this);

        appointmentRW = findViewById(R.id.myAppointmentsRecycler);
        appointmentRW.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        appointmentRW.setAdapter(mAdapter);

        userRepo.getUserbyID(currentUser.getUid()).addOnSuccessListener( userData -> {
            if(userData != null) {
                if (userData.get("lastName") != null) {
                    nameTV.append(" " + userData.get("lastName").toString() + " ");
                }

                if (userData.get("firstName") != null) {
                    nameTV.append(userData.get("firstName").toString());
                }

                Map<String, Object> location = (Map<String, Object>) userData.get("location");

                if (location != null) {
                    if (location.get("postcode") != null) {
                        locationTV.append(" " +location.get("postcode").toString() + ", ");
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
            }

            if(userData.get("phone") != null){
                Log.i(LOG_TAG,userData.get("phone").toString());
                phoneTV.setText(userData.get("phone").toString());
            } else {
                phoneTitleTV.setVisibility(View.GONE);
                phoneTV.setVisibility(View.GONE);
            }

            if(userData.get("otherContact") != null){
                Log.i(LOG_TAG,userData.get("otherContact").toString());
                otherContactTV.setText(userData.get("otherContact").toString());
            } else {
                otherContactTitleTV.setVisibility(View.GONE);
                otherContactTV.setVisibility(View.GONE);
            }

        });

        if( currentUser != null) {
            Log.d(LOG_TAG,"Belépett felhasználó");
        } else {
            Log.d(LOG_TAG,"Nem belépett felhasználó");
        }

        if(myAppointmentList != null){
            myAppointmentList.clear();
        }

        fillMyAppointmentsList();
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

    public void cancelEditing(View view) {
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
        phoneTitleTV.setVisibility(View.VISIBLE);
        phoneTV.setVisibility(View.VISIBLE);
        otherContactTitleTV.setVisibility(View.VISIBLE);
        otherContactTV.setVisibility(View.VISIBLE);
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

    }
}