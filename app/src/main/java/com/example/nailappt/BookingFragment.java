package com.example.nailappt;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.sql.Time;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingFragment extends Fragment {
    private static final String LOG_TAG = AdvertiseFragment.class.getName();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser =  mAuth.getCurrentUser();
    private final AppointmentRepository appointmentRepo = new AppointmentRepository();

    private RecyclerView adRW;
    private ArrayList<Appointment> mAppointmentList = new ArrayList<>();
    private List<String> daysWithAppointments = new ArrayList<>();
    private AppointmentAdapter mAdapter;
    private CalendarView calendarView;
    private String selectedDate;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingFragment newInstance(String param1, String param2) {
        BookingFragment fragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if( currentUser != null) {
            Log.d(LOG_TAG,"Belépett felhasználó");
        } else {
            Log.d(LOG_TAG,"Nem belépett felhasználó");
        }

        if(mAppointmentList != null){
            mAppointmentList.clear();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        mAdapter = new AppointmentAdapter(requireContext(), mAppointmentList);

        adRW = view.findViewById(R.id.bookingRecycler);
        adRW.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adRW.setAdapter(mAdapter);

        calendarView = view.findViewById(R.id.calendar);

        calendarView.setMinDate((new Date().getTime()));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Log.i(LOG_TAG, "Kiválasztott dátum: " + year + " " + month + " " + dayOfMonth);
                if((month+1) < 10){
                    selectedDate = year + "-0" + (month+1) + "-" + dayOfMonth;
                } else {
                    selectedDate = year + "-" + (month+1) + "-" + dayOfMonth;
                }

                if(mAppointmentList != null){
                    mAppointmentList.clear();
                }

                fillAppointmentList();

            }
        });

        return view;
    }

    public void fillAppointmentList(){
        appointmentRepo.freeAppointmentsByDate(selectedDate,currentUser.getUid())
                .addOnSuccessListener(querySnapshot -> {

                    for (DocumentSnapshot document : querySnapshot){
                        Appointment appointment = document.toObject(Appointment.class);
                        Log.i(LOG_TAG,appointment.toString());
                        mAppointmentList.add(appointment);
                        mAdapter.notifyItemInserted(mAppointmentList.size()-1);
                    }
                    Log.d(LOG_TAG, "Appointments list size: " + mAppointmentList.size() +"\n GetItemCount:" + mAdapter.getItemCount());
                    mAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba a lekérdezésben: ", e));
    }

}