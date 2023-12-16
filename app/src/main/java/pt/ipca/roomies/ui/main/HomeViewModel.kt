package pt.ipca.roomies.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ipca.roomies.data.entities.Card
import pt.ipca.roomies.data.repositories.CardRepository
import pt.ipca.roomies.data.entities.User

class HomeViewModel : ViewModel() {

    private val cardRepository = CardRepository()

    private val _currentCard = MutableLiveData<Card?>()
    val currentCard: LiveData<Card?> get() = _currentCard

    private val _likes = MutableLiveData<Set<String>>()
    val likes: LiveData<Set<String>> get() = _likes

    private val _matches = MutableLiveData<Set<String>>()
    val matches: LiveData<Set<String>> get() = _matches
    private lateinit var currentUserRole: String // Declare currentUserRole


    init {
        viewModelScope.launch {
            // Set currentUserRole based on your logic
            currentUserRole = "LANDLORD" // or "USER" or however you determine it

            // Load initial data
            loadNextCard(currentUserRole)

            loadLikedItems()
            loadMatchedItems()
        }
    }
    // Inside HomeViewModel
    suspend fun loadNextCard(userRole: String) {
        _currentCard.postValue(
            (if (userRole == "LANDLORD") {
                        cardRepository.getNextCardForLandlord()
                    } else {
                        cardRepository.getNextCardForUser()
                    })
        )
    }





    fun likeCurrentCard() {
        viewModelScope.launch {
            _currentCard.value?.let { card ->
                when (card) {
                    is Card.RoomCard -> card.room.roomId?.let { cardRepository.likeRoom(it, "likedUserId") }
                    is Card.UserCard -> cardRepository.likeUser(card.user.userId, "likedUserId")
                }
                loadNextCard(currentUserRole)
            }
        }
    }

    fun dislikeCurrentCard() {
        viewModelScope.launch {
            _currentCard.value?.let { card ->
                when (card) {
                    is Card.RoomCard -> card.room.roomId?.let { cardRepository.dislikeRoom(it) }
                    is Card.UserCard -> cardRepository.dislikeUser(card.user.userId)
                }
                loadNextCard(currentUserRole)
            }
        }
    }

    private suspend fun loadLikedItems() {
        _likes.postValue(cardRepository.getLikedItems())
    }

    private suspend fun loadMatchedItems() {
        _matches.postValue(cardRepository.getMatchedItems())
    }

    fun matchWithLikedCard() {
        viewModelScope.launch {
            _currentCard.value?.let { card ->
                when (card) {
                    is Card.RoomCard -> {
                        val likedUsers = cardRepository.getLikedUsers()
                        for (likedUserId in likedUsers) {
                            card.room.roomId?.let { cardRepository.matchRoom(it, likedUserId) }
                        }
                    }
                    is Card.UserCard -> {
                        val likedRooms = cardRepository.getLikedRooms()
                        for (likedRoomId in likedRooms) {
                            cardRepository.matchUser(card.user.userId, likedRoomId)
                        }
                    }
                }
                loadNextCard(currentUserRole) // Pass currentUserRole here
            }
        }
    }

}
