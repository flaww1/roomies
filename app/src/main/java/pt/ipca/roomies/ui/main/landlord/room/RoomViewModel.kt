package pt.ipca.roomies.ui.main.landlord.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.repositories.RoomRepository

class RoomViewModel : ViewModel() {

    private val roomRepository = RoomRepository()

    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms

    private val _selectedRoom = MutableLiveData<Room>()
    val selectedRoom: LiveData<Room> get() = _selectedRoom

    private val _roomCreationSuccess = MutableLiveData<String?>()
    val roomCreationSuccess: LiveData<String?> get() = _roomCreationSuccess
    private val _roomDeletionSuccess = MutableLiveData<Boolean>()
    val roomDeletionSuccess: LiveData<Boolean> get() = _roomDeletionSuccess

    fun createRoom(room: Room) {
        viewModelScope.launch {
            roomRepository.createRoom(room,
                onSuccess = { documentId ->
                    _roomCreationSuccess.value = documentId.toString()
                    refreshRooms()
                },
                onFailure = { e ->
                    // Handle failure
                    _roomCreationSuccess.value = null
                    println("Failed to create room: $e")
                }
            )
        }
    }

    fun refreshRooms() {
        viewModelScope.launch {
            roomRepository.getRooms(
                onSuccess = { rooms ->
                    _rooms.value = rooms
                },
                onFailure = { e ->
                    // Handle failure
                    println("Failed to fetch rooms: $e")
                }
            )
        }
    }

    fun deleteRoom(roomId: String) {
        viewModelScope.launch {
            roomRepository.deleteRoom(
                roomId,
                onSuccess = {
                    _roomDeletionSuccess.value = true
                    refreshRooms()
                },
                onFailure = { e ->
                    // Handle failure
                    _roomDeletionSuccess.value = false
                    println("Failed to delete room: $e")
                }
            )
        }
    }

    fun selectRoom(room: Room) {
        _selectedRoom.value = room
    }
}