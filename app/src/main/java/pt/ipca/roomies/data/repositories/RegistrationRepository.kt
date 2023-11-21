package pt.ipca.roomies.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.User

class RegistrationRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun registerUser(user: User): Boolean {
        try {
            // Create user with email and password
            val authResult = auth.createUserWithEmailAndPassword(user.email, user.password).await()

            // Update user profile with additional information (e.g., display name)
            val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                .setDisplayName("${user.firstName} ${user.lastName}")
                .build()
            authResult.user?.updateProfile(userProfileChangeRequest)?.await()

            // Store additional user information in Firestore
            val userDocumentReference = firestore.collection("users").add(user).await()

            // No need to set user.userId manually, as it will be automatically assigned by Firestore

            return true
        } catch (e: Exception) {
            // Handle registration failure (e.g., duplicate email, weak password, etc.)
            return false
        }
    }

    // Add other repository methods as needed
}
