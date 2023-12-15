package pt.ipca.roomies.ui.main.landlord.habitation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.Habitation
import pt.ipca.roomies.data.repositories.HabitationRepository

class HabitationViewModel : ViewModel() {

    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val habitationRepository = HabitationRepository()

    private val _habitations = MutableLiveData<List<Habitation>>()
    val habitations: LiveData<List<Habitation>> get() = _habitations


    private val _habitationCreationSuccess = MutableLiveData<String?>()
    val habitationCreationSuccess: LiveData<String?> get() = _habitationCreationSuccess


    private val _habitationDeletionSuccess = MutableLiveData<Boolean>()
    val habitationDeletionSuccess: LiveData<Boolean> get() = _habitationDeletionSuccess

    private val _selectedHabitation = MutableLiveData<Habitation>()
    val selectedHabitation: LiveData<Habitation>
        get() = _selectedHabitation

    private val habitationsRepository = HabitationRepository()

    fun createHabitation(habitation: Habitation) {
        viewModelScope.launch {
            try {
                // Create the habitation in Firestore
                habitationRepository.createHabitation(
                    habitation,
                    onSuccess = { documentId ->
                        // Update the local habitation with the generated document ID
                        val updatedHabitation = habitation.copy(habitationId = documentId)

                        // Update the habitation in Firestore with the correct document ID
                        viewModelScope.launch {  // Wrap the lambda in another coroutine
                            habitationRepository.updateHabitation(
                                documentId,
                                updatedHabitation,
                                onSuccess = {
                                    // Notify observers about successful creation
                                    _habitationCreationSuccess.value = documentId

                                    // Refresh the list of habitations (if needed)
                                    refreshHabitations()
                                },
                                onFailure = { e ->
                                    // Handle failure to update habitation
                                    _habitationCreationSuccess.value = null
                                    println("Failed to update habitationId: $e")
                                }
                            )
                        }
                    },
                    onFailure = { e ->
                        // Handle failure to create habitation
                        _habitationCreationSuccess.value = null
                        println("Failed to create habitation: $e")
                    }
                )
            } catch (e: Exception) {
                // Handle other exceptions
                _habitationCreationSuccess.value = null
                println("Exception during habitation creation: $e")
            }
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

    private suspend fun getHabitationById(habitationId: String): Habitation? {
        return try {
            val snapshot = firestore.collection("habitations")
                .document(habitationId)
                .get()
                .await()

            val habitation = snapshot.toObject(Habitation::class.java)
            Log.d("HabitationViewModel", "Fetched habitation: $habitation")
            habitation
        } catch (e: Exception) {
            // Handle the exception as needed
            Log.e("HabitationViewModel", "Failed to fetch habitation: $e")
            null
        }
    }


    suspend fun setSelectedHabitationId(habitationId: String) {
        // Fetch the habitation with the given ID from your database
        val habitation = getHabitationById(habitationId)

        // Set the selectedHabitation value
        _selectedHabitation.value = habitation ?: throw IllegalStateException("Habitation with ID $habitationId not found")
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