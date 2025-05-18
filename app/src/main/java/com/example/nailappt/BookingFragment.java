package com.example.nailappt;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingFragment extends Fragment implements AppointmentAdapter.OnBookListener{
    private static final String LOG_TAG = AdvertiseFragment.class.getName();
    private static final int REQUEST_CALENDAR_PERMISSION = 100;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final FirebaseUser currentUser =  mAuth.getCurrentUser();
    private final AppointmentRepository appointmentRepo = new AppointmentRepository();

    private RecyclerView adRW;
    private ArrayList<Appointment> mAppointmentList = new ArrayList<>();
    private HashSet<CalendarDay> daysWithAppointments = new HashSet<>();
    private AppointmentAdapter mAdapter;
    private MaterialCalendarView calendarView;
    private String selectedDate;
    private Appointment pendingAppointment;


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

        mAdapter = new AppointmentAdapter(requireContext(), mAppointmentList, "bookingFragment", this);

        adRW = view.findViewById(R.id.bookingRecycler);
        adRW.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adRW.setAdapter(mAdapter);

        calendarView = view.findViewById(R.id.calendarView);

        calendarView.state().edit().setMinimumDate(CalendarDay.today().getDate());
        Log.i(LOG_TAG,CalendarDay.today().getDate().toString());

        fillDaysWithAppointments();

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDate = calendarDayToString(date);
                Log.i(LOG_TAG, "Formatted date: " + selectedDate);


                if (mAppointmentList != null) {
                    mAppointmentList.clear();
                }

                fillAppointmentList();
            }
        });

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                int year = date.getYear();
                int month = date.getMonth();
                Log.i(LOG_TAG, "Hónap változott: " + year + "-" + month);
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

    public void fillDaysWithAppointments(){
        appointmentRepo.getAllAvailableAppointments(currentUser.getUid()).addOnSuccessListener( querySnapshots -> {
           for ( DocumentSnapshot document : querySnapshots){
               Appointment appointment = document.toObject(Appointment.class);
               CalendarDay date = stringToCalendarDay(appointment.getDate());
               daysWithAppointments.add(date);
           }
           AppointmentDecorator appointmentDecorator = new AppointmentDecorator(daysWithAppointments);

           calendarView.addDecorator(appointmentDecorator);
           Log.i(LOG_TAG, "daysWithAppointments: " + daysWithAppointments);
        });
    }

    private String calendarDayToString(CalendarDay date){
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonth(),date.getDay());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return dateFormat.format(calendar.getTime());
    }

    private CalendarDay stringToCalendarDay(String date){
        String[] parts = date.split("-");
        if(parts.length == 3){
            int year = Integer.parseInt(parts[0]);
            if(parts[1].startsWith("0")){
                parts[1] = parts[1].substring(1);
            }
            int month = Integer.parseInt(parts[1]);
            if(parts[2].startsWith("0")){
                parts[2] = parts[2].substring(1);
            }
            int day = Integer.parseInt(parts[2]);
            Log.i(LOG_TAG, "stringtocalendarday: " + CalendarDay.from(year,month,day));
            return CalendarDay.from(year,month-1,day);
        } else {
            return null;
        }
    }

    private void requestCalendarPermission() throws UnsupportedEncodingException {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Időpont mentése naptárba")
                .setMessage("Szeretné hozzáadni az időpontot a naptárához?")
                .setNegativeButton("Nem", ((dialog2, which2) -> {
                    dialog2.dismiss();
                }))
                .setPositiveButton("OK", ((dialog2, which2) -> {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR}, REQUEST_CALENDAR_PERMISSION);
                    } else {
                        try {
                            addEventToCalendar(pendingAppointment);
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }))
                .show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALENDAR_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission megadva - itt indítsd el a calendar hozzáadást
                try {
                    addEventToCalendar(pendingAppointment);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Permission megtagadva - jelezd a usernek, vagy kezeld le
                Toast.makeText(requireContext(), "Naptár írási engedély szükséges a foglaláshoz", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addEventToCalendar(Appointment appointment) throws UnsupportedEncodingException {
        Calendar beginTime = Calendar.getInstance();
        // Feltételezem, hogy az Appointment tartalmazza a dátumot és időpontot
        // Itt például parse-oljuk a dátumot és az időt, és beállítjuk beginTime-ot

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            beginTime.setTime(sdf.parse(appointment.getDate() + " " + appointment.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
            beginTime = Calendar.getInstance(); // fallback most
        }

        long startMillis = beginTime.getTimeInMillis();
        long endMillis = startMillis + 60 * 60 * 1000; // 1 óra hosszú esemény például

        Log.i(LOG_TAG,startMillis + " " + endMillis);

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.Events.TITLE, "Műkörmös időpont")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Lefoglalt műkörmös időpont")
                .putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            requireActivity().startActivity(intent);
        } else {
            String[] splitdate = appointment.getDate().split("-");
            String[] splittime = appointment.getTime().split(":");
            String datetime = splitdate[0] + splitdate[1] + splitdate[2] + "T" + splittime[0] + splittime[1] + "00Z";
            String title = "Időpont: műköröm";
            String details = "Lefoglalt műkörmös időpont";
            String beginTimeUtc = URLEncoder.encode(datetime, "UTF-8"); // ISO formátumban
            //String endTimeUtc = URLEncoder.encode(datetime, "UTF-8");

            String url = "https://www.google.com/calendar/render?action=TEMPLATE"
                    + "&text=" + URLEncoder.encode(title, "UTF-8")
                    + "&details=" + URLEncoder.encode(details, "UTF-8")
                    + "&dates=" + beginTimeUtc ;

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    @Override
    public void onBookRequested(Appointment currentAppointment) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Időpont foglalása")
                .setMessage("Biztosan le szeretné foglalni ezt az időpontot?")
                .setNegativeButton("Mégse", ((dialog, which) -> {
                    dialog.dismiss();
                }))
                .setPositiveButton("Foglalás", ((dialog, which) -> {
                    String appointmentId = currentAppointment.getAppointmentID();

                    appointmentRepo.bookAppointment(appointmentId, currentUser.getUid()).addOnSuccessListener(task -> {
                        Toast successToast = Toast.makeText(requireContext(), "Sikeres foglalás!", Toast.LENGTH_SHORT);
                        successToast.show();

                        this.pendingAppointment = currentAppointment;
                        try {
                            requestCalendarPermission();
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }

                    }).addOnFailureListener(e -> {
                        Log.e(LOG_TAG, "Foglalási hiba: " + e);
                        Toast.makeText(requireContext(), "Hiba történt a foglaláskor", Toast.LENGTH_SHORT).show();
                    });
                }))
                .show();
    }

    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(requireContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        daysWithAppointments.clear();
        fillDaysWithAppointments();
    }
}