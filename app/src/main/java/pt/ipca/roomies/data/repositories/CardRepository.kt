package pt.ipca.roomies.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.Card
import pt.ipca.roomies.data.entities.Like
import pt.ipca.roomies.data.entities.Match
import pt.ipca.roomies.data.entities.Room
import pt.ipca.roomies.data.entities.User

class CardRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String?
        get() = auth.currentUser?.uid

    private val likedRooms = mutableSetOf<String>()
    private val likedUsers = mutableSetOf<String>()
    private val matchedRooms = mutableSetOf<String>()
    private val matchedUsers = mutableSetOf<String>()

    private val likes = mutableListOf<Like>()
    private val matches = mutableListOf<Match>()

    suspend fun getNextCardForUser(): Card? {
        val room = getNextRoomForUser()
        return room?.let { Card.RoomCard(it) }
    }

    suspend fun getNextCardForLandlord(): Card? {
        val user = getNextUserForLandlord()
        return user?.let { Card.UserCard(it) }
    }

    suspend fun likeCard(card: Card, likedUserId: String) {
        when (card) {
            is Card.RoomCard -> likeRoom(card.room.roomId!!, likedUserId)
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

    // Inside CardRepository
    private suspend fun getNextRoomForUser(): Room? {
        // Implement logic to fetch the next room for a regular user
        return db.collection("rooms")
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toObject(Room::class.java)
    }

    private suspend fun getNextUserForLandlord(): User? {
        // Implement logic to fetch the next user for a landlord
        return db.collection("users")
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toObject(User::class.java)
    }


    suspend fun likeRoom(roomId: String, likedUserId: String) {
        likedRooms.add(roomId)
        likes.add(Like(likedUserId, currentUserId ?: "", roomId, System.currentTimeMillis()))

        // Implement logic to update likes in Firestore
        db.collection("likes").add(
            mapOf(
                "likedUserId" to likedUserId,
                "likingUserId" to currentUserId,
                "roomId" to roomId,
                "timestamp" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun dislikeRoom(roomId: String) {
        likedRooms.remove(roomId)

        // Implement logic to update likes in Firestore
        db.collection("likes")
            .whereEqualTo("likingUserId", currentUserId)
            .whereEqualTo("roomId", roomId)
            .get()
            .await()
            .documents
            .forEach { it.reference.delete() }
    }

    suspend fun likeUser(userId: String, likedUserId: String) {
        likedUsers.add(userId)
        likes.add(Like(likedUserId, currentUserId ?: "", "roomId", System.currentTimeMillis()))

        // Implement logic to update likes in Firestore
        db.collection("likes").add(
            mapOf(
                "likedUserId" to likedUserId,
                "likingUserId" to currentUserId,
                "roomId" to "roomId", // replace with the actual room ID or remove it if not needed
                "timestamp" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun dislikeUser(userId: String) {
        likedUsers.remove(userId)

        // Implement logic to update likes in Firestore
        db.collection("likes")
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

            // Implement logic to update matches in Firestore
            db.collection("matches").add(
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
            matches.add(Match(targetUserId, currentUserId ?: "", "roomId", System.currentTimeMillis()))

            // Implement logic to update matches in Firestore
            db.collection("matches").add(
                mapOf(
                    "targetUserId" to targetUserId,
                    "initiatorUserId" to currentUserId,
                    "roomId" to "roomId", // replace with the actual room ID or remove it if not needed
                    "timestamp" to System.currentTimeMillis()
                )
            ).await()
        }
    }

    suspend fun getLikedRooms(): Set<String> {
        // Implement logic to fetch liked rooms from Firestore
        val snapshot = db.collection("likes")
            .whereEqualTo("likingUserId", currentUserId)
            .get()
            .await()

        likedRooms.clear()
        for (document in snapshot.documents) {
            val roomId = document.getString("roomId")
            roomId?.let { likedRooms.add(it) }
        }

        return likedRooms
    }

    suspend fun getLikedUsers(): Set<String> {
        // Implement logic to fetch liked users from Firestore
        val snapshot = db.collection("likes")
            .whereEqualTo("likingUserId", currentUserId)
            .get()
            .await()

        likedUsers.clear()
        for (document in snapshot.documents) {
            val likedUserId = document.getString("likedUserId")
            likedUserId?.let { likedUsers.add(it) }
        }

        return likedUsers
    }

    suspend fun getMatchedRooms(): Set<String> {
        // Implement logic to fetch matched rooms from Firestore
        val snapshot = db.collection("matches")
            .whereEqualTo("initiatorUserId", currentUserId)
            .get()
            .await()

        matchedRooms.clear()
        for (document in snapshot.documents) {
            val roomId = document.getString("roomId")
            roomId?.let { matchedRooms.add(it) }
        }

        return matchedRooms
    }

    suspend fun getMatchedUsers(): Set<String> {
        // Implement logic to fetch matched users from Firestore
        val snapshot = db.collection("matches")
            .whereEqualTo("initiatorUserId", currentUserId)
            .get()
            .await()

        matchedUsers.clear()
        for (document in snapshot.documents) {
            val targetUserId = document.getString("targetUserId")
            targetUserId?.let { matchedUsers.add(it) }
        }

        return matchedUsers
    }

    suspend fun getLikes(): List<Like> {
        // Implement logic to fetch likes from Firestore
        val snapshot = db.collection("likes")
            .whereEqualTo("likingUserId", currentUserId)
            .get()
            .await()

        likes.clear()
        for (document in snapshot.documents) {
            val likedUserId = document.getString("likedUserId")
            val roomId = document.getString("roomId")
            val timestamp = document.getLong("timestamp")

            if (likedUserId != null && roomId != null && timestamp != null) {
                likes.add(Like(likedUserId, currentUserId ?: "", roomId, timestamp))
            }
        }

        return likes
    }

    suspend fun getMatches(): List<Match> {
        // Implement logic to fetch matches from Firestore
        val snapshot = db.collection("matches")
            .whereEqualTo("initiatorUserId", currentUserId)
            .get()
            .await()

        matches.clear()
        for (document in snapshot.documents) {
            val targetUserId = document.getString("targetUserId")
            val roomId = document.getString("roomId")
            val timestamp = document.getLong("timestamp")

            if (targetUserId != null && roomId != null && timestamp != null) {
                matches.add(Match(targetUserId, currentUserId ?: "", roomId, timestamp))
            }
        }

        return matches
    }


    suspend fun getLikedItems(): Set<String> {
        // Implement logic to fetch liked items from Firestore
        val snapshot = db.collection("likes")
            .whereEqualTo("likingUserId", currentUserId)
            .get()
            .await()

        likedRooms.clear()
        likedUsers.clear()
        for (document in snapshot.documents) {
            val likedUserId = document.getString("likedUserId")
            val roomId = document.getString("roomId")

            if (likedUserId != null) {
                likedUsers.add(likedUserId)
            }

            if (roomId != null) {
                likedRooms.add(roomId)
            }
        }

        return likedRooms + likedUsers
    }

    suspend fun getMatchedItems(): Set<String> {
        // Implement logic to fetch matched items from Firestore
        val snapshot = db.collection("matches")
            .whereEqualTo("initiatorUserId", currentUserId)
            .get()
            .await()

        matchedRooms.clear()
        matchedUsers.clear()
        for (document in snapshot.documents) {
            val targetUserId = document.getString("targetUserId")
            val roomId = document.getString("roomId")

            if (targetUserId != null) {
                matchedUsers.add(targetUserId)
            }

            if (roomId != null) {
                matchedRooms.add(roomId)
            }
        }

        return matchedRooms + matchedUsers
    }
}
