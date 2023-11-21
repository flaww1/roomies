package pt.ipca.roomies.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.User

class UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun getUserById(userId: String): User? {
        try {
            val userDocumentSnapshot = firestore.collection("users").document(userId).get().await()
            return userDocumentSnapshot.toObject(User::class.java)
        } catch (e: Exception) {
            // Handle retrieval failure (e.g., user not found, network error, etc.)
            return null
        }
    }

    // Add other repository methods as needed
}
