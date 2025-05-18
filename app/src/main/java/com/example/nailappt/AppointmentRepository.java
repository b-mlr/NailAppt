package com.example.nailappt;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class AppointmentRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference appointmentsRef = db.collection("Appointments");

    public Task<Void> createAppointment(Appointment newAppointment){
        DocumentReference docRef = appointmentsRef.document(); // generál automatikus ID-t
        newAppointment.setAppointmentID(docRef.getId()); // ID mentése mezőként is
        return docRef.set(newAppointment);
    }

    public Task<Void> updateAppointment(Appointment appointment ){
        return appointmentsRef.document(appointment.getAppointmentID()).set(appointment);
    }

    public Task<Void> deleteAppointment(String appointmentID){
        return appointmentsRef.document(appointmentID).delete();
    }

    public Task<Void> bookAppointment(String appointmentID, String bookedByID){
        DocumentReference docRef = appointmentsRef.document(appointmentID);

        return docRef.update("bookedByID", bookedByID);

    }

    public Task<Void> cancelAppointment(String appointmentID) {
        DocumentReference docRef = appointmentsRef.document(appointmentID);
        return docRef.update("bookedByID", "null");
    }
    public Task<DocumentSnapshot> getAppointmentByID(String appointmentID){
        return appointmentsRef.document(appointmentID).get();
    }

    public Task<QuerySnapshot> getPostedAppointments(String uid){
        return appointmentsRef.whereEqualTo("advertiserID", uid).orderBy("date", Query.Direction.ASCENDING).get();
    }

    public Task<QuerySnapshot> freeAppointmentsByDate(String selectedDate, String uid){
        return appointmentsRef.whereEqualTo("date",selectedDate).whereNotEqualTo("advertiserID",uid).whereEqualTo("bookedByID","null").get();
    }

    public Task<QuerySnapshot> getAllAvailableAppointments(String uid){
        return appointmentsRef.whereEqualTo("bookedByID","null").whereNotEqualTo("advertiserID",uid).get();
    }

    public Task<QuerySnapshot> getMyAppointments(String uid) {
        return appointmentsRef.whereEqualTo("bookedByID", uid).orderBy("date", Query.Direction.ASCENDING).orderBy("time", Query.Direction.ASCENDING).get();
    }


}
