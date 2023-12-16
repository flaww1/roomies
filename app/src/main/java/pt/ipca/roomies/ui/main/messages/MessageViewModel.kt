package pt.ipca.roomies.ui.main.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.ipca.roomies.data.entities.Match
import pt.ipca.roomies.data.entities.Message
import pt.ipca.roomies.data.repositories.MessagesRepository

class MessagesViewModel : ViewModel() {

    val matchedUsers: LiveData<Set<Match>>
        get() {
            loadMatches()
            return matches
        }
    private val messagesRepository = MessagesRepository()

    private val _matches = MutableLiveData<Set<Match>>()
    val matches: LiveData<Set<Match>> get() = _matches

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private fun loadMatches() {
        _matches.value = messagesRepository.getMatches()
    }

    private fun loadMessagesForMatch(match: Match) {
        _messages.value = messagesRepository.getMessagesForMatch(match)
    }

    fun sendMessage(message: Message) {
        messagesRepository.sendMessage(message)
        // You may want to update the LiveData or trigger other actions
    }

    fun removeMatch(match: Match) {
        messagesRepository.removeMatch(match)
        loadMatches() // Refresh matches after removing one
    }

    fun getChatMessages(targetUserId: String): LiveData<List<Message>> {
        return messagesRepository.getChatMessages(targetUserId)

    }
}
