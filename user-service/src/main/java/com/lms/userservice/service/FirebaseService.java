package com.lms.userservice.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

/**
 * Service class for handling Firebase authentication operations.
 * This class interacts with Firebase to verify tokens and manage user-related authentication.
 */
@Service
public class FirebaseService {

    /**
     * Verifies the Firebase ID token to ensure it is valid.
     *
     * @param idToken The Firebase ID token received from the client.
     * @return The decoded FirebaseToken object if the token is valid.
     * @throws Exception If the token is invalid or an error occurs during verification.
     */
    public FirebaseToken verifyToken(String idToken) throws Exception {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }
}
