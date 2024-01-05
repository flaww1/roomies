package pt.ipca.roomies.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import pt.ipca.roomies.data.dao.LikeMatchDao
import pt.ipca.roomies.data.dao.RoomDao
import pt.ipca.roomies.data.dao.UserDao
import pt.ipca.roomies.data.entities.Card
import pt.ipca.roomies.data.entities.Like
import pt.ipca.roomies.data.entities.Match
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.entities.User

class CardRepository(
    private val likeMatchDao: LikeMatchDao,
    private val roomDao: RoomDao,
    private val userDao: UserDao
) {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val currentUserId: String?
        get() = auth.currentUser?.uid
    private val likedRooms = mutableSetOf<String>()
    private val likes = mutableListOf<Like>()
    private val likedUsers = mutableSetOf<String>()
    private val matches = mutableListOf<Match>()
    private val matchedRooms = mutableSetOf<String>()
    private val matchedUsers = mutableSetOf<String>()
}


/*
    private suspend fun getNextRoomForUser(): Room? {
        return withContext(Dispatchers.IO) {
            roomDao.getNextRoom()
        }
    }

    private suspend fun getNextUserForLanlord(): User? {
        return withContext(Dispatchers.IO) {
            userDao.getNextUser()
        }
    }

    suspend fun likeCard(card: Card, likedUserId: String) {
        when (card) {
            is Card.RoomCard -> likeRoom(card.room.roomId!!, card.user.userId)
            is Card.UserCard -> likeUser(card.user.userId, likedUserId)
        }
    }

    suspend fun dislikeCard(card: Card) {
        when (card) {
            is Card.RoomCard -> dislikeRoom(card.room.roomId!!)
            is Card.UserCard -> dislikeUser(card.user.userId)
        }
    }

    suspend fun matchCard(card: Card, targetUserId: String) {
        when (card) {
            is Card.RoomCard -> matchRoom(card.room.roomId!!, targetUserId)
            is Card.UserCard -> matchUser(card.user.userId, targetUserId)
        }
    }



    suspend fun likeRoom(roomId: String, likedUserId: String) {
        withContext(Dispatchers.IO) {
            likedRooms.add(roomId)
            likes.add(Like(likedUserId, currentUserId ?: "", roomId, System.currentTimeMillis().toString()))

            likeMatchDao.insertLike(Like(likedUserId, currentUserId ?: "", roomId, System.currentTimeMillis()))
        }

        // Create a map with the like information
        val likeMap = mapOf(
            "likedUserId" to likedUserId,
            "likingUserId" to currentUserId,
            "roomId" to roomId,
            "timestamp" to System.currentTimeMillis()
        )

        // Update likes in Firestore
        firestore.collection("likes").add(likeMap).await()
    }



    suspend fun dislikeRoom(roomId: String) {
        withContext(Dispatchers.IO) {
            likedRooms.remove(roomId)

            currentUserId?.let { likeMatchDao.deleteLikes(it, roomId) }
        }

        // Implement logic to update likes in Firestore
        firestore.collection("likes")
            .whereEqualTo("likingUserId", currentUserId)
            .whereEqualTo("roomId", roomId)
            .get()
            .await()
            .documents
            .forEach { it.reference.delete() }
    }

    suspend fun likeUser(userId: String, likedUserId: String) {
        withContext(Dispatchers.IO) {
            likedUsers.add(userId)
            likes.add(Like(likedUserId, currentUserId ?: "", userId, System.currentTimeMillis()))

            likeMatchDao.insertLike(Like(likedUserId, currentUserId ?: "", userId, System.currentTimeMillis()))
        }

        // Implement logic to update likes in Firestore
        firestore.collection("likes").add(
            mapOf(
                "likedUserId" to likedUserId,
                "likingUserId" to currentUserId,
                "userId" to userId, // replace with the actual user ID or remove it if not needed
                "timestamp" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun dislikeUser(userId: String) {
        withContext(Dispatchers.IO) {
            likedUsers.remove(userId)

            currentUserId?.let { likeMatchDao.deleteLikes(it, userId) }
        }

        // Implement logic to update likes in Firestore
        firestore.collection("likes")
            .whereEqualTo("likingUserId", currentUserId)
            .whereEqualTo("likedUserId", userId)
            .get()
            .await()
            .documents
            .forEach { it.reference.delete() }
    }

    suspend fun matchRoom(roomId: String, targetUserId: String) {
        if (likedRooms.contains(roomId)) {
            matchedRooms.add(roomId)
            matches.add(Match(targetUserId, currentUserId ?: "", roomId, System.currentTimeMillis()))

            withContext(Dispatchers.IO) {
                likeMatchDao.insertMatch(Match(targetUserId, currentUserId ?: "", roomId, System.currentTimeMillis()))
            }

            // Implement logic to update matches in Firestore
            firestore.collection("matches").add(
                mapOf(
                    "targetUserId" to targetUserId,
                    "initiatorUserId" to currentUserId,
                    "roomId" to roomId,
                    "timestamp" to System.currentTimeMillis()
                )
            ).await()
        }
    }

    suspend fun matchUser(userId: String, targetUserId: String) {
        if (likedUsers.contains(userId)) {
            matchedUsers.add(userId)
            matches.add(Match(targetUserId, currentUserId ?: "", userId, System.currentTimeMillis()))

            withContext(Dispatchers.IO) {
                likeMatchDao.insertMatch(Match(targetUserId, currentUserId ?: "", userId, System.currentTimeMillis()))
            }

            // Implement logic to update matches in Firestore
            firestore.collection("matches").add(
                mapOf(
                    "targetUserId" to targetUserId,
                    "initiatorUserId" to currentUserId,
                    "userId" to userId,
                    "timestamp" to System.currentTimeMillis()
                )
            ).await()
        }
    }






    suspend fun getLikedRooms(): Set<String> {
        // Implement logic to fetch liked rooms from local Room database
        val localLikedRooms = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getLikedRooms(it) }
        }

        likedRooms.clear()
        if (localLikedRooms != null) {
            likedRooms.addAll(listOf(localLikedRooms.map { it.roomId }.toString()))
        }

        return likedRooms
    }

    suspend fun getLikedUsers(): Set<String> {
        // Implement logic to fetch liked users from local Room database
        val localLikedUsers = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getLikedUsers(it) }
        }

        likedUsers.clear()
        if (localLikedUsers != null) {
            likedUsers.addAll(listOf(localLikedUsers.map { it.likedUserId }.toString()))
        }

        return likedUsers
    }

    private suspend fun getMatchedRooms(): Set<String> {
        // Implement logic to fetch matched rooms from local Room database
        val localMatchedRooms = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getMatchedRooms(it) }
        }

        matchedRooms.clear()
        if (localMatchedRooms != null) {
            matchedRooms.addAll(listOf(localMatchedRooms.map { it.roomId }.toString()))
        }

        return matchedRooms
    }

    private suspend fun getMatchedUsers(): Set<String> {
        // Implement logic to fetch matched users from local Room database
        val localMatchedUsers = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getMatchedUsers(it) }
        }

        matchedUsers.clear()
        if (localMatchedUsers != null) {
            matchedUsers.addAll(listOf(localMatchedUsers.map { it.targetUserId }.toString()))
        }

        return matchedUsers
    }

    private suspend fun getLikes(): List<Like> {
        // Implement logic to fetch likes from local Room database
        return withContext(Dispatchers.IO) {
            likeMatchDao.getAllLikes(currentUserId)
        }
    }

    private suspend fun getMatches(): List<Match> {
        // Implement logic to fetch matches from local Room database
        return withContext(Dispatchers.IO) {
            likeMatchDao.getAllMatches(currentUserId)
        }
    }

    suspend fun getLikedItems(): Set<String> {
        // Implement logic to fetch liked items from local Room database
        val localLikedRooms = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getLikedRooms(it) }
        }

        val localLikedUsers = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getLikedUsers(it) }
        }

        likedRooms.clear()
        if (localLikedRooms != null) {
            likedRooms.addAll(listOf(localLikedRooms.map { it.roomId }.toString()))
        }

        likedUsers.clear()
        if (localLikedUsers != null) {
            likedUsers.addAll(listOf(localLikedUsers.map { it.likedUserId }.toString()))
        }

        return likedRooms + likedUsers
    }

    suspend fun getMatchedItems(): Set<String> {
        // Implement logic to fetch matched items from local Room database
        val localMatchedRooms = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getMatchedRooms(it) }
        }

        val localMatchedUsers = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getMatchedUsers(it) }
        }

        matchedRooms.clear()
        if (localMatchedRooms != null) {
            matchedRooms.addAll(listOf(localMatchedRooms.map { it.roomId }.toString()))
        }

        matchedUsers.clear()
        if (localMatchedUsers != null) {
            matchedUsers.addAll(listOf(localMatchedUsers.map { it.targetUserId }.toString()))
        }

        return matchedRooms + matchedUsers
    }
}
*/