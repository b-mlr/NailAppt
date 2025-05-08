package com.example.nailappt;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppointmentRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference appointmentsRef = db.collection("Appointments");


    public Task<DocumentReference> createAppointment(Appointment newAppointment){
        return appointmentsRef.add(newAppointment);
    }
}
