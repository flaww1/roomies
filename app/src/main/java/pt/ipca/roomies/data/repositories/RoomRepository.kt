package pt.ipca.roomies.data.repositories

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.Room

class RoomRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createRoom(
        habitationId: String,
        room: Room,
        onSuccess: (DocumentReference) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val documentReference = firestore.collection("habitations")
                .document(habitationId)
                .collection("rooms")
                .add(room)
                .await()
            onSuccess(documentReference)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    // Retrieve rooms associated with a specific habitation
    suspend fun getRoomsForHabitation(
        habitationId: String,
        onSuccess: (List<Room>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val rooms = firestore.collection("habitations")
                .document(habitationId)
                .collection("rooms")
                .get()
                .await()
                .toObjects(Room::class.java)
            onSuccess(rooms)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    // Retrieve all rooms (not associated with a specific habitation)
    suspend fun getAllRooms(
        onSuccess: (List<Room>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val rooms = firestore.collection("rooms")
                .get()
                .await()
                .toObjects(Room::class.java)
            onSuccess(rooms)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    suspend fun deleteRoom(
        habitationId: String,
        roomId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            firestore.collection("habitations")
                .document(habitationId)
                .collection("rooms")
                .document(roomId)
                .delete()
                .await()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    suspend fun updateRoom(
        habitationId: String,
        roomId: String,
        updatedRoom: Room,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            firestore.collection("habitations")
                .document(habitationId)
                .collection("rooms")
                .document(roomId)
                .set(updatedRoom)
                .await()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}
