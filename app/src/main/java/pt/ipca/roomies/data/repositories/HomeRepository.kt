package pt.ipca.roomies.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.entities.User

class HomeRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getCurrentUserRole(): String? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        return if (userId != null) {
            try {
                val snapshot = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
                val userRole = snapshot.getString("userRole")
                userRole
            } catch (e: Exception) {
                null // Handle exceptions, e.g., document not found or network issues
            }
        } else {
            null // User is not signed in
        }
    }
    suspend fun getRoomsForUser(): List<Room> {
        // Implement logic to fetch rooms for users from Firestore
        // For example, you might have a "rooms" collection in Firestore
        // and you can fetch documents from it
        return db.collection("rooms")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(Room::class.java) }
    }

    suspend fun getUsersForLandlord(): List<User> {
        // Implement logic to fetch users for landlords from Firestore
        // For example, you might have a "users" collection in Firestore
        // and you can fetch documents from it
        return db.collection("users")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(User::class.java) }
    }

    // Add additional functions for like, dislike, match, etc., as needed

}
