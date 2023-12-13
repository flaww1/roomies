package pt.ipca.roomies.ui.main.landlord.habitation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.Habitation
import pt.ipca.roomies.data.repositories.HabitationRepository

class HabitationViewModel : ViewModel() {

    private val habitationRepository = HabitationRepository()

    private val _habitations = MutableLiveData<List<Habitation>>()
    val habitations: LiveData<List<Habitation>> get() = _habitations

    private val _selectedHabitation = MutableLiveData<Habitation>()

    private val _habitationCreationSuccess = MutableLiveData<String?>()
    val habitationCreationSuccess: LiveData<String?> get() = _habitationCreationSuccess


    private val _habitationDeletionSuccess = MutableLiveData<Boolean>()
    val habitationDeletionSuccess: LiveData<Boolean> get() = _habitationDeletionSuccess

    private val _habitationDocumentId = MutableLiveData<String?>()
    val habitationDocumentId: LiveData<String?> get() = _habitationDocumentId


    private val habitationsRepository = HabitationRepository()
    fun setHabitationDocumentId(documentId: String) {
        _habitationDocumentId.value = documentId
    }
    fun createHabitation(habitation: Habitation) {
        viewModelScope.launch {
            habitationRepository.createHabitation(habitation,
                onSuccess = { documentId ->
                    // Set the document ID in LiveData for further use
                    _habitationDocumentId.value = documentId

                    // Continue with your existing logic or refresh habitations
                    viewModelScope.launch {
                        // Refresh habitations or perform any other action
                        refreshHabitations()
                    }
                },
                onFailure = { e ->
                    // Handle failure
                    _habitationDocumentId.value = null
                    println("Failed to create habitation: $e")
                }
            )
        }
    }


    fun refreshHabitations() {
        viewModelScope.launch {
            habitationRepository.getHabitations(
                onSuccess = { habitations ->
                    _habitations.value = habitations
                    println("Fetched habitations: $habitations") // Add this line
                },
                onFailure = { e ->
                    // Handle failure
                    println("Failed to fetch habitations: $e")
                }
            )
        }
    }

    fun deleteHabitation(habitationId: String) {
        viewModelScope.launch {
            habitationRepository.deleteHabitation(
                habitationId,
                onSuccess = {
                    _habitationDeletionSuccess.value = true
                    refreshHabitations()
                },
                onFailure = { e ->
                    // Handle failure
                    _habitationDeletionSuccess.value = false
                    println("Failed to delete habitation: $e")
                }
            )
        }
    }

    fun selectHabitation(habitation: Habitation) {
        _selectedHabitation.value = habitation
    }

    fun updateHabitation(habitationId: String, updatedHabitation: Habitation) {
        viewModelScope.launch {
            habitationRepository.updateHabitation(
                habitationId,
                updatedHabitation,
                onSuccess = {
                    refreshHabitations()
                },
                onFailure = { e ->
                    // Handle failure
                    println("Failed to update habitation: $e")
                }
            )
        }
    }

    fun getHabitationsByLandlordId(landlordId: String) {
        viewModelScope.launch {
            habitationRepository.getHabitationsByLandlordId(
                landlordId,
                onSuccess = { habitations ->
                    _habitations.value = habitations
                },
                onFailure = { e ->
                    // Handle failure
                    println("Failed to fetch habitations: $e")
                }
            )
        }
    }

}
