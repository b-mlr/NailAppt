package com.example.nailappt;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class UserRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = db.collection("Users");


    public Task<Void> createUser(User newUser){
        return usersRef.document(newUser.getUid()).set(newUser);
    }

    public Task<Boolean> doesUserExist(String uid){
        DocumentReference docRef = usersRef.document(uid);

        return docRef.get().continueWith(task -> {
            if(task.isSuccessful()){
                return task.getResult().exists();
            } else {
                throw Objects.requireNonNull(task.getException());
            }
        });

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

    public Task<Void> updateUser(User updatedUser) {
        return usersRef.document(updatedUser.getUid()).set(updatedUser);
    }
}
