package pt.ipca.roomies.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import pt.ipca.roomies.data.local.AppDatabase
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.entities.User

class HomeRepository(private val AppDatabase: AppDatabase) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Local Room-related functions
    suspend fun insertRoomToLocalDatabase(room: pt.ipca.roomies.data.entities.Room) {
        AppDatabase.roomDao().insertRoom(room)
    }

    suspend fun getRoomsFromLocalDatabase(): List<pt.ipca.roomies.data.entities.Room> {
        return AppDatabase.roomDao().getAllRooms()
    }



    suspend fun syncLocalDataWithFirestore(localRooms: List<Room>) {
        withContext(Dispatchers.IO) {
            try {
                // Iterate through local rooms and update Firestore
                for (room in localRooms) {
                    // Use your logic to update or add the room to Firestore
                    updateOrAddRoomToFirestore(room)
                }
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    private suspend fun updateOrAddRoomToFirestore(room: Room) {
        val roomDocument = room.roomId.let { db.collection("rooms").document(it.toString()) }

        try {
            roomDocument.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Room exists in Firestore, update it
                    roomDocument.set(room).addOnSuccessListener {
                        // Handle success if needed
                    }.addOnFailureListener {
                        // Handle failure if needed
                    }
                } else {
                    // Room does not exist in Firestore, add it
                    roomDocument.set(room).addOnSuccessListener {
                        // Handle success if needed
                    }.addOnFailureListener {
                        // Handle failure if needed
                    }
                }
            }?.addOnFailureListener {
                // Handle failure if needed
            }
        } catch (e: Exception) {
            // Handle exceptions
        }
    }


    suspend fun getCurrentUserRole(): String? {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        return if (userId != null) {
            try {
                val snapshot = db.collection("users").document(userId).get().await()
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
        return try {
            db.collection("rooms")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Room::class.java) }
        } catch (e: Exception) {
            emptyList() // Handle exceptions, e.g., network issues
        }
    }

    suspend fun getUsersForLandlord(): List<User> {
        return try {
            db.collection("users")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(User::class.java) }
        } catch (e: Exception) {
            emptyList() // Handle exceptions, e.g., network issues
        }
    }

    // Add additional functions for like, dislike, match, etc., as needed
}
