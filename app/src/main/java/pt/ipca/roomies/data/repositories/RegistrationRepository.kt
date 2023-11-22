package pt.ipca.roomies.data.repositories

import User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel


class RegistrationRepository (private val viewModel: RegistrationViewModel) {

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


            // Store user's role in Firestore
            val userDocumentReference = firestore.collection("users").document(user.userId.toString())
            userDocumentReference.set(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // The operation was successful
                    auth.currentUser
                } else {
                    // The operation failed

                    viewModel._errorMessage.value = "Failed to save user information"
                }
            }



            // No need to set user.userId manually, as it will be automatically assigned by Firestore

            return true
        } catch (e: Exception) {
            // Handle registration failure (e.g., duplicate email, weak password, etc.)
            viewModel._errorMessage.value = e.message
            return false

        }
    }



    // Add other repository methods as needed
}
