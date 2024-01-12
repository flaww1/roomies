package pt.ipca.roomies.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import pt.ipca.roomies.data.entities.Match
import pt.ipca.roomies.data.entities.Message

class MessagesRepository {

    private val matches = mutableSetOf<Match>()
    private val messageMap = mutableMapOf<String, MutableList<Message>>()
    private val db = FirebaseFirestore.getInstance()

    fun getMatches(): Set<Match> {
        // Implement logic to fetch matches from Firestore or other storage
        return matches
    }

    fun getMessagesForMatch(match: Match): List<Message> {
        // Implement logic to fetch messages for a match from Firestore or other storage
        return messageMap[match.matchId] ?: emptyList()
    }

    fun sendMessage(message: Message) {
        val matchId = message.matchId // Assuming matchId is a property in your Message class
        if (messageMap.containsKey(matchId)) {
            messageMap[matchId]?.add(message)
        } else {
            messageMap[matchId] = mutableListOf(message)
        }

        // Now you can implement the logic to send the message to Firebase or other storage
    }


    fun removeMatch(match: Match) {
        // Implement logic to remove a match from Firestore or other storage
        matches.remove(match)
        // Also remove associated messages
        messageMap.remove(match.matchId)
    }

    fun getChatMessages(targetUserId: String): LiveData<List<Message>> {
        val messagesLiveData = MutableLiveData<List<Message>>()
        var registration: ListenerRegistration?

        // Reference to the "messages" collection in Firestore
        val messagesRef = db.collection("messages")
            .document(getChatId(targetUserId))
            .collection("messages")

        // Listen for real-time updates
        registration = messagesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle errors, e.g., log or notify the user
                return@addSnapshotListener
            }

            val messages = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) } ?: emptyList()
            messagesLiveData.value = messages
        }

        // Remove the listener when the LiveData becomes inactive (e.g., fragment is destroyed)
        messagesLiveData.observeForever {
            // This callback is triggered when the LiveData becomes inactive
            // Remove the listener here
            registration.remove()
        }

        return messagesLiveData
    }


    // Helper function to generate a chat ID based on user IDs
    private fun getChatId(targetUserId: String): String {
        val currentUserId = Firebase.auth.currentUser?.uid ?: ""
        return if (currentUserId < targetUserId) {
            "$currentUserId-$targetUserId"
        } else {
            "$targetUserId-$currentUserId"
        }
    }
}
