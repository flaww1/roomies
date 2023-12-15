package pt.ipca.roomies.ui.main.landlord.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.repositories.RoomRepository

class RoomViewModel : ViewModel() {

    private val roomRepository = RoomRepository()

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
        refreshRooms()
    }

    // Function to create a room
    // In RoomViewModel
    fun createRoom(room: Room) {
        viewModelScope.launch {
            try {
                val documentReference = roomRepository.createRoom(room)
                // Notify observers about successful creation with document ID
                _roomCreationSuccess.value = documentReference.id
                // Refresh the list of rooms
                refreshRooms()
            } catch (e: Exception) {
                // Handle failure
                Log.e("RoomViewModel", "Failed to create room: $e")
            }
        }
    }


    // Function to update a room
    fun updateRoom(room: Room) {
        viewModelScope.launch {
            roomRepository.updateRoom(
                room,
                onSuccess = {
                    // Notify observers about successful update
                    _roomUpdateSuccess.value = true
                    // Refresh the list of rooms
                    refreshRooms()
                },
                onFailure = { e ->
                    // Handle failure
                    Log.e("RoomViewModel", "Failed to update room: $e")
                }
            )
        }
    }

    // Function to delete a room
    fun deleteRoom(roomId: String, roomId1: String) {
        viewModelScope.launch {
            roomRepository.deleteRoom(
                roomId,
                onSuccess = {
                    // Notify observers about successful deletion
                    _roomDeletionSuccess.value = true
                    // Refresh the list of rooms
                    refreshRooms()
                },
                onFailure = { e ->
                    // Handle failure
                    Log.e("RoomViewModel", "Failed to delete room: $e")
                }
            )
        }
    }

    // Function to refresh the list of rooms
    fun refreshRooms() {
        viewModelScope.launch {
            roomRepository.getAllRooms(
                onSuccess = { rooms ->
                    _rooms.value = rooms
                },
                onFailure = { e ->
                    // Handle failure
                    Log.e("RoomViewModel", "Failed to retrieve rooms: $e")
                }
            )
        }
    }

    // Function to select a room
    fun selectRoom(room: Room) {
        _selectedRoom.value = room
    }

    fun getRoomsByHabitationId(habitationId: String) {
        viewModelScope.launch {
            roomRepository.getRoomsForHabitation(
                habitationId,
                onSuccess = { rooms ->
                    _rooms.value = rooms
                },
                onFailure = { e ->
                    // Handle failure
                    Log.e("RoomViewModel", "Failed to retrieve rooms: $e")
                }
            )
        }

    }
}
