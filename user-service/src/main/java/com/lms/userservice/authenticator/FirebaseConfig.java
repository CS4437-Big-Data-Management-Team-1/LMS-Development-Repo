package com.lms.userservice.authenticator;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
/**
 * Configuration class for initialising Firebase in our application.
 *
 * This class sets up Firebase using the Firebase Admin SDK. It reads the service account key
 * from a JSON file and configures the Firebase application with the provided credentials.
 *
 * @see <a href="https://firebase.google.com/docs/admin/setup">Firebase Admin SDK Setup Guide</a>
 * @author Olan Healy
 */


@Configuration
public class FirebaseConfig {

    /**
     *  This method is executed after construction to set up the FirebaseApp instance.
     *  It loads the service account credentials from a firebase-setup-json (in discord)
     *
     *  The initialisation only occurs if there are no existing FirebaseApp instances
     *  to ensure that the app is not initialised multiple times.
     *
     *
     *  @throws IOException if there is an error reading the service account key file
     *
     */
    @PostConstruct
    public void initialise() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("user-service/src/main/resources/firebase-setup.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}