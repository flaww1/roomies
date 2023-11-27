package pt.ipca.roomies.ui.habitations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.Habitation
import pt.ipca.roomies.data.repositories.HabitationRepository

class HabitationViewModel(private val habitationRepository: HabitationRepository) : ViewModel() {

    private val _habitations = MutableLiveData<List<Habitation>>()
    val habitations: LiveData<List<Habitation>> get() = _habitations

    private val _habitationCreationResult = MutableLiveData<DocumentReference>()
    val habitationCreationResult: LiveData<DocumentReference> get() = _habitationCreationResult

    fun getHabitations() {
        viewModelScope.launch {
            try {
                _habitations.value = habitationRepository.getHabitations()
            } catch (e: Exception) {
                // Handle the exception (e.g., show an error message)
            }
        }
    }

    fun createHabitation(habitation: Habitation) {
        viewModelScope.launch {
            try {
                _habitationCreationResult.value = habitationRepository.createHabitation(habitation)
            } catch (e: Exception) {
                // Handle the exception (e.g., show an error message)
            }
        }
    }

    // Add more ViewModel logic as needed for habitation-related operations

}