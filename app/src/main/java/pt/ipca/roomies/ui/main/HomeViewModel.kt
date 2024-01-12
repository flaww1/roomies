package pt.ipca.roomies.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipca.roomies.data.entities.Card
import pt.ipca.roomies.data.entities.Habitation
import pt.ipca.roomies.data.repositories.CardRepository
import pt.ipca.roomies.data.repositories.LoginRepository

class HomeViewModel(private val cardRepository: CardRepository, private val loginRepository: LoginRepository) : ViewModel() {

    private val _currentCard = MutableLiveData<Card?>()
    val currentCard: LiveData<Card?> = _currentCard

    private val _likes = MutableLiveData<Set<String>>()
    val likes: LiveData<Set<String>> = _likes

    private val _matches = MutableLiveData<Set<String>>()
    val matches: LiveData<Set<String>> = _matches

    private val _nextCard = MutableLiveData<Card?>()
    val nextCard: LiveData<Card?> = _nextCard

    private val _cardProcessed = MutableLiveData<Boolean>()
    val cardProcessed: LiveData<Boolean> = _cardProcessed

    // This will hold the current user role
    private var currentUserRole = ""

    init {
        viewModelScope.launch {
            currentUserRole = loadUserRole()
            loadInitialCards()
        }
    }

    private suspend fun loadUserRole(): String {
        val currentUser = loginRepository.getCurrentUser()
        return loginRepository.fetchUserRole(currentUser).also {
            Log.d("HomeViewModel", "Loading user role: $it")
        }
    }

    private suspend fun loadInitialCards() {
        loadNextCard(currentUserRole)
        loadLikedUsers()
        loadMatchedUsers()
        loadLikedRooms()
        loadMatchedRooms()
    }

    suspend fun loadNextCard(userRole: String) {
        Log.d("HomeViewModel", "Loading next card for user role: $userRole")
        _currentCard.postValue(
            when (userRole) {
                "Landlord" -> {
                    val nextUser = cardRepository.getNextUserForLanlord()
                    nextUser?.let { Card.UserCard(it) }
                }
                "User" -> {
                    val nextRoom = cardRepository.getNextRoomForUser()
                    nextRoom?.let { Card.RoomCard(it) }
                }
                else -> null
            }
        )
    }

    fun resetCardProcessed() {
        _cardProcessed.value = false
    }

    suspend fun getHabitationByRoomId(roomId: String) {
        return withContext(Dispatchers.IO) {
            cardRepository.getHabitationByRoomId(roomId)
        }
    }
    fun likeCurrentCard() {
        viewModelScope.launch {
            _currentCard.value?.let { card ->
                Log.d("HomeViewModel", "Liking current card: $card")
                when (card) {
                    is Card.RoomCard -> {
                        val roomId = card.room.roomId
                        val room = cardRepository.getRoomById(roomId) // Assuming you have a method to fetch a room by ID
                        val habitation = room.let { cardRepository.getHabitationByRoomId(roomId) }

                        // Now you have the Habitation, and you can get the landlordId
                        val landlordId = habitation?.landlordId

                        // Proceed with your like logic, using landlordId as needed
                        if (landlordId != null) {
                            cardRepository.likeRoom(roomId, landlordId)
                        }
                    }
                    is Card.UserCard -> cardRepository.likeUser(card.user.userId)
                }
                _cardProcessed.postValue(true)
                loadNextCard(currentUserRole)
            }
        }
    }





    fun dislikeCurrentCard() {
        viewModelScope.launch {
            _currentCard.value?.let { card ->
                Log.d("HomeViewModel", "Disliking current card: $card")
                when (card) {
                    is Card.RoomCard -> cardRepository.dislikeRoom(card.room.roomId)
                    is Card.UserCard -> cardRepository.dislikeUser(card.user.userId)
                }
                _cardProcessed.postValue(true)
                loadNextCard(currentUserRole)
            }
        }
    }

    suspend fun loadLikedUsers() {
        _likes.postValue(cardRepository.getLikedUsers())
    }

    suspend fun loadMatchedUsers() {
        _matches.postValue(cardRepository.getMatchedUsers())
    }

    suspend fun loadLikedRooms() {
        _likes.postValue(cardRepository.getLikedRooms())
    }

    suspend fun loadMatchedRooms() {
        _matches.postValue(cardRepository.getMatchedRooms())
    }

    fun loadNextRoomCard() {
        viewModelScope.launch {
            val nextRoom = cardRepository.getNextRoomForUser()
            _nextCard.postValue(nextRoom?.let { Card.RoomCard(it) })
        }
    }

    fun loadNextUserCard() {
        viewModelScope.launch {
            val nextUser = cardRepository.getNextUserForLanlord()
            _nextCard.postValue(nextUser?.let { Card.UserCard(it) })
        }
    }

    // Additional ViewModel methods can be added here as needed.
}
