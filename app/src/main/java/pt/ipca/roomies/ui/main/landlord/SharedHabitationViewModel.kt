package pt.ipca.roomies.ui.main.landlord

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.ipca.roomies.data.entities.Habitation

class SharedHabitationViewModel : ViewModel() {
    private val _selectedHabitation = MutableLiveData<Habitation?>()
    val selectedHabitation: LiveData<Habitation?> get() = _selectedHabitation

    private val _documentId = MutableLiveData<String>()
    val documentId: LiveData<String> get() = _documentId

    fun selectHabitation(habitation: Habitation, documentId: String) {
        _selectedHabitation.value = habitation
        _documentId.value = documentId
    }

    fun setSelectedHabitation(habitation: Habitation, s: String) {
        _selectedHabitation.value = habitation
        _documentId.value = s

    }
}
