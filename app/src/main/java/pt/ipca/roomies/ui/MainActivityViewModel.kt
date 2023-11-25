// MainViewModel.kt
package pt.ipca.roomies.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel : ViewModel() {

    private val _welcomeMessage = MutableLiveData<String>()
    val welcomeMessage: LiveData<String> get() = _welcomeMessage

    init {
        // Set up initial welcome message
        _welcomeMessage.value = "Welcome to Roomies"
    }

    fun updateWelcomeMessage(newMessage: String) {
        _welcomeMessage.value = newMessage
    }
}
