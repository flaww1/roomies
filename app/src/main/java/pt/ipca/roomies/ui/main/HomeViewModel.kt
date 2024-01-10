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

class HomeViewModel(
    private val cardRepository: CardRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _currentCard = MutableLiveData<Card?>()
    val currentCard: LiveData<Card?> get() = _currentCard

    private val _likes = MutableLiveData<Set<String>>()
    val likes: LiveData<Set<String>> get() = _likes

    private val _matches = MutableLiveData<Set<String>>()
    val matches: LiveData<Set<String>> get() = _matches

    private lateinit var currentUserRole: String

    private var shuffledCards: MutableList<Card> = mutableListOf()

    private val _cardList = MutableLiveData<List<Card>>()
    val cardList: LiveData<List<Card>> get() = _cardList

    private val _selectedCard = MutableLiveData<Card?>()
    val selectedCard: LiveData<Card?> get() = _selectedCard


    init {
        viewModelScope.launch {
            // Load initial data
            currentUserRole = loadUserRole()
            loadNextCard(currentUserRole)
            loadLikedAndMatched(currentUserRole)
        }
    }

    private suspend fun loadUserRole(): String {
        val currentUser = loginRepository.getCurrentUser()
        return loginRepository.fetchUserRole(currentUser).also {
            Log.d("HomeViewModel", "Loading user role: $it")
        }
    }

    fun likeCurrentCard() {
        viewModelScope.launch {
            try {
                _currentCard.value?.let { card ->
                    Log.d("HomeViewModel", "Liking current card: $card")
                    when (card) {
                        is Card.RoomCard -> {
                            Log.d("HomeViewModel", "RoomCard liked: ${card.room.roomId}")
                            cardRepository.likeRoom(card.room.roomId, "likedUserId")
                        }
                        is Card.UserCard -> {
                            Log.d("HomeViewModel", "UserCard liked: ${card.user.userId}")
                            cardRepository.likeUser(card.user.userId, "likedUserId")
                        }
                    }
                    loadNextCard(currentUserRole)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception in likeCurrentCard: $e")
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

    private suspend fun loadLikedAndMatched(userRole: String) {
        when (userRole) {
            "User" -> {
                _likes.postValue(cardRepository.getLikedUsers())
                _matches.postValue(cardRepository.getMatchedUsers())
            }
            "Landlord" -> {
                _likes.postValue(cardRepository.getLikedRooms())
                _matches.postValue(cardRepository.getMatchedRooms())
            }
        }
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

    private var isUserRoleLoaded = false

    fun loadNextCard(currentUserRole: String) {
        if (!isUserRoleLoaded) {
            viewModelScope.launch {
                // Load user role only once
                this@HomeViewModel.currentUserRole = loadUserRole()
                isUserRoleLoaded = true

                // Continue with loading the cards
                loadInitialCards(currentUserRole)
                loadNextCard(currentUserRole)
            }
        } else {
            shuffledCards.let {
                if (it.isNotEmpty()) {
                    val nextCard = it.removeAt(0)
                    _currentCard.postValue(nextCard)
                } else {
                    viewModelScope.launch {
                        loadInitialCards(currentUserRole)
                        loadNextCard(currentUserRole)
                    }
                }
            }
        }
    }


    private fun <T> List<T>.fisherYatesShuffle(): List<T> {
        val shuffledList = this.toMutableList()
        val random = java.util.Random()

        for (i in shuffledList.size - 1 downTo 1) {
            val j = random.nextInt(i + 1)
            val temp = shuffledList[i]
            shuffledList[i] = shuffledList[j]
            shuffledList[j] = temp
        }

        return shuffledList
    }

    private suspend fun loadInitialCards(userRole: String) {
        val cards = when (userRole) {
            "Landlord" -> cardRepository.getAllUsersAsCards()
            "User" -> cardRepository.getAllRoomsAsCards()
            else -> emptyList()
        }

        shuffledCards = cards.fisherYatesShuffle().toMutableList()
        _cardList.postValue(shuffledCards)
    }
    fun onCardSelected(card: Card) {
        _selectedCard.value = card
    }

    fun onCardDetailsNavigated() {
        _selectedCard.value = null
    }


}
