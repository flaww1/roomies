package pt.ipca.roomies.data.repositories

import User
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


class RegistrationRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun updateUserInFirestore(updatedUser: User) {
        Log.d("RegistrationRepository", "Updating user in Firestore: $updatedUser")

        val userId = updatedUser.userId
        if (userId.isNotEmpty()) {
            val userDocument = firestore.collection("users").document(userId)

            userDocument.set(updatedUser)
                .addOnSuccessListener {
                    // Update successful
                    Log.d("RegistrationRepository", "User role updated successfully: $updatedUser")
                }
                .addOnFailureListener { e ->
                    // Handle the error
                    Log.e("RegistrationRepository", "Error updating user role", e)
                }
        } else {
            Log.e("RegistrationRepository", "User ID is empty, cannot update user role")
        }
    }

    fun storeUserInFirestore(userId: String, email: String, password: String, firstName: String, lastName: String) {
        // Create a user object with basic information
        val user = User(
            userId = userId,
            firstName = firstName, // You may add these fields later in the profile editor
            lastName = lastName,  // You may add these fields later in the profile editor
            email = email,
            userRole = "",
            password = password,
            registrationDate = Calendar.getInstance().timeInMillis,
            userRating = 0
            // Add other fields as needed
        )

        // Store user information in Firestore
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(userId).set(user)
            .addOnCompleteListener { userTask ->
                if (!userTask.isSuccessful) {



            }
    }
    }
}

