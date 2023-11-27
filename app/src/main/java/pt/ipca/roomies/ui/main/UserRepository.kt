package pt.ipca.roomies.ui.main

import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    fun updateUserProfile(user: User) {
        // Implement logic to update user profile in Firestore
        db.collection("users")
            .document(user.userId)
            .set(user)
            .addOnSuccessListener {
                // Handle success, you can log or perform additional actions
            }
            .addOnFailureListener { e ->
                // Handle failure, you can log or display an error message
            }
    }

    // Implement other methods for user-related operations
}