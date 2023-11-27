package pt.ipca.roomies.ui.habitations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.repositories.RoomRepository

class RoomViewModel(private val roomRepository: RoomRepository) : ViewModel() {

    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms

    private val _roomCreationResult = MutableLiveData<DocumentReference>()
    val roomCreationResult: LiveData<DocumentReference> get() = _roomCreationResult

    fun getRooms(habitationId: String) {
        viewModelScope.launch {
            try {
                _rooms.value = roomRepository.getRooms(habitationId)
            } catch (e: Exception) {
                // Handle the exception (e.g., show an error message)
            }
        }
    }

    fun createRoom(habitationId: String, room: Room) {
        viewModelScope.launch {
            try {
                _roomCreationResult.value = roomRepository.createRoom(habitationId, room)
            } catch (e: Exception) {
                // Handle the exception (e.g., show an error message)
            }
        }
    }

    // Add more ViewModel logic as needed for room-related operations

}