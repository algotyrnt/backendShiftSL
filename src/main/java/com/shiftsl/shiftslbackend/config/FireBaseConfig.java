package com.shiftsl.shiftslbackend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FireBaseConfig {

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @Value("${firebase.database.url}")
    private String firebaseDatabaseUrl;

    private FirebaseOptions getFirebaseOptions() throws IOException {
        // Load service account from resources
        InputStream serviceAccount = getClass().getClassLoader()
                .getResourceAsStream(firebaseConfigPath.replace("classpath:", ""));

        if (serviceAccount == null) {
            throw new IllegalArgumentException("Firebase service account file not found at: " + firebaseConfigPath);
        }

        return FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(firebaseDatabaseUrl)
                .build();
    }

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(getFirebaseOptions());
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public Firestore initializeFirestore() throws IOException {
        // Ensure FirebaseApp is initialized
        initializeFirebase();
        return FirestoreClient.getFirestore();
    }
}
