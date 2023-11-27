package pt.ipca.roomies.data.repositories

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.Room

class RoomRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getRooms(habitationId: String): List<Room> {
        // Assuming you have a "rooms" collection in Firestore under each habitation
        return firestore.collection("habitations")
            .document(habitationId)
            .collection("rooms")
            .get()
            .await()
            .toObjects(Room::class.java)
    }

    suspend fun createRoom(habitationId: String, room: Room): DocumentReference {
        // Assuming you have a "rooms" collection in Firestore under each habitation
        return firestore.collection("habitations")
            .document(habitationId)
            .collection("rooms")
            .add(room)
            .await()
    }

    // Add more methods as needed for room-related operations

}