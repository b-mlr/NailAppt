package com.example.nailappt;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class AppointmentRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference appointmentsRef = db.collection("Appointments");

    public Task<DocumentReference> createAppointment(Appointment newAppointment){
        return appointmentsRef.add(newAppointment);
    }

    public Task<QuerySnapshot> getPostedAppointments(String uid){
        return appointmentsRef.whereEqualTo("advertiserID", uid).orderBy("date", Query.Direction.ASCENDING).get();
    }
}
