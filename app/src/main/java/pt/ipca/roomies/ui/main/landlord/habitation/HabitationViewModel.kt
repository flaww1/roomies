package pt.ipca.roomies.ui.main.landlord.habitation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.Habitation
import pt.ipca.roomies.data.repositories.HabitationRepository

class HabitationViewModel(private val habitationRepository: HabitationRepository) : ViewModel() {



    private val _habitations = MutableLiveData<List<Habitation>>()
    val habitations: LiveData<List<Habitation>> get() = _habitations

    private val _habitationCreationSuccess = MutableLiveData<String?>()
    val habitationCreationSuccess: LiveData<String?> get() = _habitationCreationSuccess

    private val _habitationDeletionSuccess = MutableLiveData<Boolean>()
    val habitationDeletionSuccess: LiveData<Boolean> get() = _habitationDeletionSuccess

    private val _selectedHabitation = MutableLiveData<Habitation>()
    val selectedHabitation: LiveData<Habitation> get() = _selectedHabitation

    fun createHabitation(habitation: Habitation) {
        viewModelScope.launch {
            try {
                habitationRepository.createHabitation(
                    habitation,
                    onSuccess = { documentId ->
                        val updatedHabitation = habitation.copy(habitationId = documentId)
                        viewModelScope.launch {
                            habitationRepository.updateHabitation(
                                documentId,
                                updatedHabitation,
                                onSuccess = {
                                    _habitationCreationSuccess.value = documentId
                                    refreshHabitations()
                                },
                                onFailure = { e ->
                                    _habitationCreationSuccess.value = null
                                    Log.e("HabitationViewModel", "Failed to update habitationId", e)
                                }
                            )
                        }
                    },
                    onFailure = { e ->
                        _habitationCreationSuccess.value = null
                        Log.e("HabitationViewModel", "Failed to create habitation", e)
                    }
                )
            } catch (e: Exception) {
                _habitationCreationSuccess.value = null
                Log.e("HabitationViewModel", "Exception during habitation creation", e)
            }
        }
    }

    fun refreshHabitations() {
        viewModelScope.launch {
            habitationRepository.getHabitations(
                onSuccess = { habitations ->
                    _habitations.value = habitations
                    Log.d("HabitationViewModel", "Fetched habitations: $habitations")
                },
                onFailure = { e ->
                    Log.e("HabitationViewModel", "Failed to fetch habitations", e)
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
                    _habitationDeletionSuccess.value = false
                    Log.e("HabitationViewModel", "Failed to delete habitation", e)
                }
            )
        }
    }

    private suspend fun getHabitationById(habitationId: String): Habitation? {
        return try {
            habitationRepository.getHabitationById(habitationId)
        } catch (e: Exception) {
            Log.e("HabitationViewModel", "Failed to fetch habitation", e)
            null
        }
    }

    suspend fun setSelectedHabitationId(habitationId: String) {
        val habitation = getHabitationById(habitationId)
        _selectedHabitation.value =
            habitation ?: throw IllegalStateException("Habitation with ID $habitationId not found")
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
                    Log.e("HabitationViewModel", "Failed to update habitation", e)
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
                    Log.e("HabitationViewModel", "Failed to fetch habitations", e)
                }
            )
        }
    }
}
