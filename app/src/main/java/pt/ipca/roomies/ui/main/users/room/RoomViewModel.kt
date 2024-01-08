package pt.ipca.roomies.ui.main.users.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.repositories.RoomRepository

class RoomViewModel(private val roomRepository: RoomRepository) : ViewModel() {

    // LiveData for the list of rooms
    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms

    // LiveData for the currently selected room
    private val _selectedRoom = MutableLiveData<Room>()
    val selectedRoom: LiveData<Room> get() = _selectedRoom

    // LiveData for room deletion success
    private val _roomDeletionSuccess = MutableLiveData<Boolean>()
    val roomDeletionSuccess: LiveData<Boolean> get() = _roomDeletionSuccess

    // LiveData for room update success
    private val _roomUpdateSuccess = MutableLiveData<Boolean>()
    val roomUpdateSuccess: LiveData<Boolean> get() = _roomUpdateSuccess

    private val _roomCreationSuccess = MutableLiveData<String?>()
    val roomCreationSuccess: LiveData<String?> get() = _roomCreationSuccess

    init {
        // Fetch all rooms initially
        refreshRooms("", "")
        // Call syncRooms to synchronize local and remote rooms
        syncRooms()
    }

    // Function to create a room
    fun createRoom(room: Room) {
        viewModelScope.launch {
            try {
                val documentReference = roomRepository.createRoomFirebase(room)
                // Notify observers about successful creation with document ID
                _roomCreationSuccess.value = documentReference.id
                // Refresh the list of rooms
                refreshRooms(room.habitationId, room.roomId ?: "")
            } catch (e: Exception) {
                // Handle failure
                Log.e("RoomViewModel", "Failed to create room: $e")
                _roomCreationSuccess.value = null // Set value to null to indicate failure
            }
        }
    }

    // Function to update a room
    fun updateRoom(room: Room) {
        viewModelScope.launch {
            roomRepository.updateRoomFirebase(
                room,
                onSuccess = {
                    // Notify observers about successful update
                    _roomUpdateSuccess.value = true
                    // Refresh the list of rooms
                    refreshRooms(room.habitationId, room.roomId ?: "")
                },
                onFailure = { e ->
                    // Handle failure
                    Log.e("RoomViewModel", "Failed to update room: $e")
                }
            )
        }
    }

    // Function to delete a room
    fun deleteRoom(habitationId: String, roomId: String) {
        viewModelScope.launch {
            roomRepository.deleteRoomFirebase(
                roomId,
                onSuccess = {
                    // Notify observers about successful deletion
                    _roomDeletionSuccess.value = true
                    // Refresh the list of rooms
                    refreshRooms(habitationId, roomId)
                }
            ) { e ->
                // Handle failure
                Log.e("RoomViewModel", "Failed to delete room: $e")
            }
        }
    }

    // Function to refresh the list of rooms
    fun refreshRooms(roomId: String, habitationId: Comparable<*>) {
        viewModelScope.launch {
            roomRepository.getRoomsForHabitationFirebase(
                roomId = roomId,
                habitationId = habitationId,
                onSuccess = { rooms ->
                    _rooms.value = rooms
                }
            ) { e ->
                // Handle failure
                Log.e("RoomViewModel", "Failed to retrieve rooms: $e")
            }
        }
    }


    // Function to select a room
    fun selectRoom(room: Room) {
        _selectedRoom.value = room
    }

    // Function to synchronize local and remote rooms
    private fun syncRooms() {
        viewModelScope.launch {
            roomRepository.syncRooms()
        }
    }



    // Function to get rooms by habitation ID
    fun getRoomsByHabitationId(habitationId: String) {
        viewModelScope.launch {
            roomRepository.getRoomsForHabitationFirebase(
                roomId = "", // You may not need roomId in this context
                habitationId = habitationId,
                onSuccess = { rooms ->
                    _rooms.value = rooms
                }
            ) { e ->
                // Handle failure
                Log.e("RoomViewModel", "Failed to retrieve rooms: $e")
            }
        }
    }

}
