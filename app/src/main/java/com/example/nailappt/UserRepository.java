package com.example.nailappt;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;
import java.util.Objects;

public class UserRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = db.collection("Users");

    private static final String LOG_TAG = SignInActivity.class.getName();

    public Task<Void> createUser(User newUser){
        return usersRef.document(newUser.getUid()).set(newUser);
    }

    public Task<Map<String, Object>> getUserbyID(String uid){
        DocumentReference docRef = usersRef.document(uid);

        return docRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return document.getData();
                } else {
                    return null;
                }
            } else {
                throw Objects.requireNonNull(task.getException());
            }
        });
    }

}
