package pt.ipca.roomies.data.repositories

import pt.ipca.roomies.ui.authentication.registration.RegistrationViewModel
import User
import UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar


class RegistrationRepository(private val viewModel: RegistrationViewModel) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun registerUser(user: User, userProfile: UserProfile): Boolean {
        try {
            // Create user with email and password
            val authResult = auth.createUserWithEmailAndPassword(user.email, user.password).await()

            // Update user profile with additional information (e.g., display name)
            val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                .setDisplayName("${user.firstName} ${user.lastName}")
                .build()
            authResult.user?.updateProfile(userProfileChangeRequest)?.await()

            // Update user entity with registration date and userRating
            val updatedUser = user.copy(
                registrationDate = Calendar.getInstance().timeInMillis,
                userRating = 0
                // Add other fields as needed
            )

            // Store user's role in Firestore
            saveUserAndProfileToFirestore(updatedUser, userProfile)

            return true
        } catch (e: Exception) {
            viewModel._errorMessage.value = e.message ?: "User registration failed"
            return false
        }
    }

    private fun saveUserAndProfileToFirestore(user: User, userProfile: UserProfile) {
        // Store user information in Firestore
        val userId = user.userId ?: return
        firestore.collection("users").document(userId).set(user)
            .addOnCompleteListener { userTask ->
                if (!userTask.isSuccessful) {
                    viewModel._errorMessage.value = userTask.exception?.message ?: "Failed to save user information"
                }
            }

        // Store user profile information in Firestore
        firestore.collection("userProfiles").document(userId).set(userProfile)
            .addOnCompleteListener { profileTask ->
                if (!profileTask.isSuccessful) {
                    viewModel._errorMessage.value = profileTask.exception?.message ?: "Failed to save user profile information"
                }
            }
    }



}

