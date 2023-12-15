package pt.ipca.roomies.data.repositories

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.Room

class RoomRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // In RoomRepository
    suspend fun createRoom(room: Room): DocumentReference {
        return firestore.collection("rooms")
            .add(room)
            .await()
    }


    suspend fun getRoomsForHabitation(
        habitationId: String,
        onSuccess: (List<Room>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val rooms = firestore.collection("rooms")
                .whereEqualTo("habitationId", habitationId)
                .get()
                .await()
                .toObjects(Room::class.java)
            onSuccess(rooms)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

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
        roomId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            firestore.collection("rooms")
                .document(roomId)
                .delete()
                .await()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    suspend fun updateRoom(
        room: Room,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            room.roomId?.let {
                firestore.collection("rooms")
                    .document(it)
                    .set(room)
                    .await()
            }
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}
