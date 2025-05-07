package com.example.nailappt;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = db.collection("Users");


    public Task<Void> createUser(User newUser){
        return usersRef.document(newUser.getUid()).set(newUser);
    }

}
