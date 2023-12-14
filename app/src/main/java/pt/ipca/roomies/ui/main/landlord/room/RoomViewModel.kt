package pt.ipca.roomies.ui.main.landlord.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.Habitation
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.repositories.HabitationRepository
import pt.ipca.roomies.data.repositories.RoomRepository
import pt.ipca.roomies.ui.main.landlord.habitation.HabitationViewModel

class RoomViewModel : ViewModel() {

    private val roomRepository = RoomRepository()

    // LiveData for the list of rooms
    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms

    // LiveData for the currently selected room
    private val _selectedRoom = MutableLiveData<Room>()
    val selectedRoom: LiveData<Room> get() = _selectedRoom

    // LiveData for room creation success (contains the document ID)
    private val _roomCreationSuccess = MutableLiveData<String?>()
    val roomCreationSuccess: LiveData<String?> get() = _roomCreationSuccess

    // LiveData for room deletion success
    private val _roomDeletionSuccess = MutableLiveData<Boolean>()
    val roomDeletionSuccess: LiveData<Boolean> get() = _roomDeletionSuccess
    private val _selectedHabitation = MutableLiveData<Habitation?>()

    private val habitationRepository = HabitationRepository()
    private val habitationViewModel: HabitationViewModel = HabitationViewModel()

    init {
        // Observe the selectedHabitation from HabitationViewModel
        habitationViewModel.selectedHabitation.observeForever {
            _selectedHabitation.value = it
            // Whenever selectedHabitation changes, fetch rooms
            it?.habitationId?.let { habitationId ->
                getRoomsByHabitationId(habitationId)
            }
        }
    }

    // Function to create a room
    fun createRoom(habitationId: String, room: Room) {
        viewModelScope.launch {
            roomRepository.createRoom(
                habitationId,
                room,
                onSuccess = { documentId ->
                    _roomCreationSuccess.value = documentId.toString()
                },
                onFailure = { e ->
                    // Handle failure
                    println("Failed to create room: $e")
                }

            )

        }
    }
    fun getRoomsByHabitationId(habitationId: String) {
        viewModelScope.launch {
            habitationRepository.getRoomsByHabitationId(
                habitationId,
                onSuccess = { rooms ->
                    _rooms.value = rooms
                },
                onFailure = { e ->
                    // Handle failure
                    Log.e("RoomViewModel", "Failed to fetch rooms: $e")
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
                    println("Failed to retrieve rooms: $e")
                }
            )
        }
    }

    // Function to delete a room
    fun deleteRoom(habitationId: String, roomId: String) {
        viewModelScope.launch {
            roomRepository.deleteRoom(
                habitationId,
                roomId,
                onSuccess = {
                    _roomDeletionSuccess.value = true
                },
                onFailure = { e ->
                    // Handle failure
                    println("Failed to delete room: $e")
                }
            )
        }
    }


    // Function to select a room
    fun selectRoom(room: Room) {
        _selectedRoom.value = room
    }
}
