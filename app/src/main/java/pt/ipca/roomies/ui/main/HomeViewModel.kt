package pt.ipca.roomies.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.Card
import pt.ipca.roomies.data.repositories.CardRepository
import pt.ipca.roomies.data.repositories.LoginRepository

class HomeViewModel(private val cardRepository: CardRepository, private val loginRepository: LoginRepository) : ViewModel() {

    private val _currentCard = MutableLiveData<Card?>()
    val currentCard: LiveData<Card?> get() = _currentCard

    private val _likes = MutableLiveData<Set<String>>()
    val likes: LiveData<Set<String>> get() = _likes

    private val _matches = MutableLiveData<Set<String>>()
    val matches: LiveData<Set<String>> get() = _matches

    private lateinit var currentUserRole: String

    init {
        viewModelScope.launch {
            // Load initial data
            currentUserRole = loadUserRole()
            loadNextCard(currentUserRole)
            loadLikedUsers()
            loadMatchedUsers()
            loadLikedRooms()
            loadMatchedRooms()
        }
    }
    // Change the loadUserRole function to return a String instead of setting the value
    private suspend fun loadUserRole(): String {
        val currentUser = loginRepository.getCurrentUser()
        return loginRepository.fetchUserRole(currentUser)
        Log.d("HomeViewModel", "Loading user role: $currentUserRole")
    }

    suspend fun loadNextCard(userRole: String) {
        Log.d("HomeViewModel", "Loading next card for user role: $userRole")
        _currentCard.postValue(
            when (userRole) {
                "Landlord" -> cardRepository.getNextUserForLanlord()
                "User" -> cardRepository.getNextRoomForUser()
                else -> null
            } as Card?
        )
    }

    fun likeCurrentCard() {
        viewModelScope.launch {
            _currentCard.value?.let { card ->
                Log.d("HomeViewModel", "Liking current card: $card")
                when (card) {
                    is Card.RoomCard -> {
                        card.room.roomId.let { roomId ->
                            cardRepository.likeRoom(roomId, "likedUserId")
                        }
                    }
                    is Card.UserCard -> {
                        card.user.userId.let { userId ->
                            cardRepository.likeUser(userId, "likedUserId")
                        }
                    }
                }
                loadNextCard(currentUserRole)
            }
        }
    }

    fun dislikeCurrentCard() {
        viewModelScope.launch {
            _currentCard.value?.let { card ->
                Log.d("HomeViewModel", "Disliking current card: $card")
                when (card) {
                    is Card.RoomCard -> {
                        card.room.roomId.let { roomId ->
                            cardRepository.dislikeRoom(roomId)
                        }
                    }
                    is Card.UserCard -> {
                        card.user.userId.let { userId ->
                            cardRepository.dislikeUser(userId)
                        }
                    }
                }
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

    fun matchWithLikedCard() {
        viewModelScope.launch {
            _currentCard.value?.let { card ->
                when (card) {
                    is Card.RoomCard -> {
                        val likedUsers = cardRepository.getLikedUsers()
                        for (likedUserId in likedUsers) {
                            card.room.roomId.let { roomId ->
                                cardRepository.matchRoom(roomId, likedUserId)
                            }
                        }
                    }
                    is Card.UserCard -> {
                        val likedRooms = cardRepository.getLikedRooms()
                        for (likedRoomId in likedRooms) {
                            card.user.userId.let { userId ->
                                cardRepository.matchUser(userId, likedRoomId)
                            }
                        }
                    }
                }
                loadNextCard(currentUserRole)
            }
        }
    }

    fun unmatchWithMatchedCard() {
        viewModelScope.launch {
            _currentCard.value?.let { card ->
                when (card) {
                    is Card.RoomCard -> {
                        val matchedUsers = cardRepository.getMatchedUsers()
                        for (matchedUserId in matchedUsers) {
                            card.room.roomId.let { roomId ->
                                cardRepository.unmatchRoom(roomId, matchedUserId)
                            }
                        }
                    }
                    is Card.UserCard -> {
                        val matchedRooms = cardRepository.getMatchedRooms()
                        for (matchedRoomId in matchedRooms) {
                            card.user.userId.let { userId ->
                                cardRepository.unmatchUser(userId, matchedRoomId)
                            }
                        }
                    }
                }
                loadNextCard(currentUserRole)
            }
        }
    }

    fun loadNextRoomCard() {
        viewModelScope.launch {
            val nextRoom = cardRepository.getNextRoomForUser()
            val nextCard = nextRoom?.let { room -> Card.RoomCard(room) }
            _currentCard.postValue(nextCard)
        }
    }


    fun loadNextUserCard() {
        viewModelScope.launch {
            val nextUser = cardRepository.getNextUserForLanlord()
            val nextCard = nextUser?.let { user -> Card.UserCard(user) }
            _currentCard.postValue(nextCard)
        }
    }


}
