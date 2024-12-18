package com.shiftsl.shiftslbackend.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.shiftsl.shiftslbackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FirestoreRepository {

    @Autowired
    private Firestore firestore;

    private static final String COLLECTION_NAME = "users"; // Firestore collection

    public ApiFuture<WriteResult> saveUser(User user) {
        CollectionReference users = firestore.collection(COLLECTION_NAME);
        return users.document(user.getId()).set(user);
    }
}
