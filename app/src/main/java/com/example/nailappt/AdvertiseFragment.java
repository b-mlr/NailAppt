package com.example.nailappt;

import android.content.Intent;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdvertiseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdvertiseFragment extends Fragment implements AppointmentAdapter.OnBookListener{
    private static final String LOG_TAG = AdvertiseFragment.class.getName();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser =  mAuth.getCurrentUser();
    private final AppointmentRepository appointmentRepo = new AppointmentRepository();

    private RecyclerView adRW;
    private ArrayList<Appointment> mAppointmentList = new ArrayList<>();
    private AppointmentAdapter mAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdvertiseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdvertiseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdvertiseFragment newInstance(String param1, String param2) {
        AdvertiseFragment fragment = new AdvertiseFragment();
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
        View view = inflater.inflate(R.layout.fragment_advertise, container, false);

        MaterialButton addBtn = view.findViewById(R.id.addAd);
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ManageAdvertActivity.class);
            startActivity(intent);
        });


        mAdapter = new AppointmentAdapter(requireContext(), mAppointmentList, "advertiseFragment",this);

        adRW = view.findViewById(R.id.adRecycler);
        adRW.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adRW.setAdapter(mAdapter);


        Log.d(LOG_TAG, "Appointments list size: " + mAppointmentList.size() +"\n GetItemCount:" + mAdapter.getItemCount());

        return view;
    }

    @Override
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
        mAppointmentList.clear();
        appointmentRepo.getPostedAppointments(currentUser.getUid())
                .addOnSuccessListener(querySnapshot -> {

                    for (DocumentSnapshot document : querySnapshot){
                        Appointment appointment = document.toObject(Appointment.class);
                        Log.i(LOG_TAG,appointment.toString());
                        mAppointmentList.add(appointment);
                    }
                    mAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba a lekérdezésben: ", e));
    }

    @Override
    public void onDestroy() {
        mAppointmentList.clear();
        super.onDestroy();
    }

    @Override
    public void onBookRequested(Appointment appointment) {
    }
}