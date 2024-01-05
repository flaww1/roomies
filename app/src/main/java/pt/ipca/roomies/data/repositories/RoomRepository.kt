package pt.ipca.roomies.data.repositories

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.dao.RoomDao
import pt.ipca.roomies.data.dao.UserProfileDao
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.entities.UserProfile


class RoomRepository(
    private val userProfileDao: UserProfileDao,
    private val roomDao: RoomDao,

) {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Local (Room) operations

    suspend fun insertRoomLocal(room: Room) {
        roomDao.insertRoom(room)
    }

    private suspend fun getAllRoomsLocal(): List<pt.ipca.roomies.data.entities.Room> {
        return roomDao.getAllRooms()
    }

    suspend fun getRoomByIdLocal(roomId: String): pt.ipca.roomies.data.entities.Room? {
        return roomDao.getRoomById(roomId)
    }


    suspend fun deleteRoomLocal(roomId: String) {  // Add this line
        roomDao.deleteRoomById(roomId)
    }

    suspend fun updateRoomLocal(room: Room) {
        roomDao.updateRoom(room)
    }

    // Firebase operations

    suspend fun createRoomFirebase(room: Room): DocumentReference {
        return firestore.collection("rooms")
            .add(room)
            .await()
    }

    suspend fun getRoomsForHabitationFirebase(
        roomId: String,
        habitationId: Comparable<*>,
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


    suspend fun getAllRoomsFirebase(
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

    suspend fun getRoomByIdFirebase(roomId: String): Room? {
        return try {
            val document = firestore.collection("rooms").document(roomId).get().await()
            document.toObject(Room::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteRoomFirebase(
        roomId: Long,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            firestore.collection("rooms")
                .document(roomId.toString())
                .delete()
                .await()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    suspend fun updateRoomFirebase(
        room: Room,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            room.roomId?.let {
                firestore.collection("rooms")
                    .document(it.toString())
                    .set(room)
                    .await()
            }
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    // Sync operations
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun syncRooms() {
        val localRooms = getAllRoomsLocal()
        val remoteRooms = getAllRoomsFirebase(
            onSuccess = { rooms ->
                GlobalScope.launch {
                    val mergedRooms = mergeRooms(localRooms, rooms)
                    updateLocalRoomDatabase(mergedRooms)
                }
            },
            onFailure = { exception ->
                // Handle error
            }
        )


    }

    private fun mergeRooms(
        localRooms: List<pt.ipca.roomies.data.entities.Room>,
        remoteRooms: List<pt.ipca.roomies.data.entities.Room>
    ): List<pt.ipca.roomies.data.entities.Room> {
        // Implement your logic for merging local and remote rooms
        // This could involve checking timestamps, resolving conflicts, etc.
        // For simplicity, we're assuming that the remote data is always more recent.

        return remoteRooms
    }

    private suspend fun updateLocalRoomDatabase(rooms: List<Room>) {
        // Update the local Room database with the merged list of rooms
        roomDao.deleteAllRooms()
        roomDao.insertRooms(rooms)
    }

    // User Profile operations (assuming UserProfileDao is used for Room storage)

    suspend fun getUserProfileByUserId(userId: String): LiveData<UserProfile?> {
        return userProfileDao.getUserProfileByUserId(userId)
    }

    suspend fun insertUserProfile(userProfile: UserProfile) {
        userProfileDao.insertUserProfile(userProfile)
    }

    // GET CURRENT USER ID
    suspend fun getCurrentUserId(): String? {
        return userProfileDao.getAllUserProfiles().firstOrNull()?.userId
    }

    suspend fun insertOrUpdateUserProfileLocally(it: UserProfile) {
        userProfileDao.insertUserProfile(it)


    }


}
