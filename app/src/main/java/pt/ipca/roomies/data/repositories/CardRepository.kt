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


    suspend fun getNextRoomForUser(): Room? {
        // Implement logic to fetch next room for user from Firestore
        val rooms = firestore.collection("rooms").get().await().toObjects(Room::class.java)

        // Remove the local database-related logic
        // val localLikedRooms = withContext(Dispatchers.IO) {
        //     currentUserId?.let { likeMatchDao.getLikedRooms(it) }
        // }
        //
        // val localMatchedRooms = withContext(Dispatchers.IO) {
        //     currentUserId?.let { likeMatchDao.getMatchedRooms(it) }
        // }

        // Simpler filtering without local database checks
        return rooms.firstOrNull()
    }


    suspend fun getNextUserForLanlord(): User? {

        val users = firestore.collection("users").get().await().toObjects(User::class.java)

        // Remove the local database-related logic
        // val localLikedUsers = withContext(Dispatchers.IO) {
        //     currentUserId?.let { likeMatchDao.getLikedUsers(it) }
        // }
        //
        // val localMatchedUsers = withContext(Dispatchers.IO) {
        //     currentUserId?.let { likeMatchDao.getMatchedUsers(it) }
        // }

        // Simpler filtering without local database checks
        return users.firstOrNull()

    }

    suspend fun likeCard(card: Card, likedUserId: String) {
        when (card) {
            is Card.RoomCard -> likeRoom(card.room.roomId, likedUserId)
            is Card.UserCard -> likeUser(card.user.userId, likedUserId)

        }
    }

    suspend fun dislikeCard(card: Card) {
        when (card) {
            is Card.RoomCard -> dislikeRoom(card.room.roomId)
            is Card.UserCard -> dislikeUser(card.user.userId)
        }
    }

    suspend fun matchCard(card: Card, targetUserId: String) {
        when (card) {
            is Card.RoomCard -> matchRoom(card.room.roomId, targetUserId)
            is Card.UserCard -> matchUser(card.user.userId, targetUserId)
        }
    }


    suspend fun likeRoom(roomId: String, likedUserId: String) {
        withContext(Dispatchers.IO) {
            val roomIdString = roomId.toString()
            likedRooms.add(roomIdString)
            likes.add(
                Like(
                    "",
                    likedUserId,
                    currentUserId ?: "",
                    roomIdString,
                    System.currentTimeMillis()
                )
            )

            likeMatchDao.insertLike(
                Like(
                    "",
                    likedUserId,
                    currentUserId ?: "",
                    roomIdString,
                    System.currentTimeMillis()
                )
            )
        }

        // Implement logic to update likes in Firestore
        firestore.collection("likes").add(
            mapOf(
                "likedUserId" to likedUserId,
                "likingUserId" to currentUserId,
                "roomId" to roomId.toString(), // Convert roomId to String
                "timestamp" to System.currentTimeMillis()
            )
        ).await()
    }


    suspend fun dislikeRoom(roomId: String) {
        withContext(Dispatchers.IO) {
            likedRooms.remove(roomId.toString())

            currentUserId?.let { likeMatchDao.deleteLikes(it, roomId.toString()) }
        }

        // Implement logic to update likes in Firestore
        firestore.collection("likes")
            .whereEqualTo("likingUserId", currentUserId)
            .whereEqualTo("roomId", roomId.toString())
            .get()
            .await()
            .documents
            .forEach { it.reference.delete() }
    }


    suspend fun likeUser(userId: String, likedUserId: String) {
        withContext(Dispatchers.IO) {
            likedUsers.add(userId)
            likes.add(
                Like(
                    "",
                    likedUserId,
                    currentUserId ?: "",
                    userId,
                    System.currentTimeMillis()
                )
            )

            likeMatchDao.insertLike(
                Like(
                    "",
                    likedUserId,
                    currentUserId ?: "",
                    userId,
                    System.currentTimeMillis()
                )
            )
        }

        // Implement logic to update likes in Firestore
        firestore.collection("likes").add(
            mapOf(
                "likedUserId" to likedUserId,
                "likingUserId" to currentUserId,
                "userId" to userId,
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
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .documents
            .forEach { it.reference.delete() }
    }

    suspend fun matchRoom(roomId: String, targetUserId: String) {
        if (likedRooms.contains(roomId)) {
            matchedRooms.add(roomId)
            matches.add(
                Match(
                    "",
                    targetUserId,
                    currentUserId ?: "",
                    roomId,
                    System.currentTimeMillis()
                )
            )

            withContext(Dispatchers.IO) {
                likeMatchDao.insertMatch(
                    Match(
                        "",
                        targetUserId,
                        currentUserId ?: "",
                        roomId,
                        System.currentTimeMillis()
                    )
                )
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
            matches.add(
                Match(
                    "",
                    targetUserId,
                    currentUserId ?: "",
                    userId,
                    System.currentTimeMillis()
                )
            )

            withContext(Dispatchers.IO) {
                likeMatchDao.insertMatch(
                    Match(
                        "",
                        targetUserId,
                        currentUserId ?: "",
                        userId,
                        System.currentTimeMillis()
                    )
                )
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
        val likedRooms = mutableSetOf<String>()

        // Fetch liked rooms from Firestore
        firestore.collection("likes")
            .whereEqualTo("likingUserId", currentUserId)
            .get()
            .await()
            .documents
            .forEach { document ->
                document.getString("roomId")?.let {
                    likedRooms.add(it)
                }
            }
        /*
            val localLikedRooms = withContext(Dispatchers.IO) {
                currentUserId?.let { likeMatchDao.getLikedRooms(it) }
            }

            likedRooms.clear()
            if (localLikedRooms != null) {
                likedRooms.addAll(listOf(localLikedRooms.map { it.roomId }.toString()))
            }
        */

        return likedRooms
    }

    suspend fun getMatchedRooms(): Set<String> {
        val matchedRooms = mutableSetOf<String>()

        // Fetch matched rooms from Firestore
        firestore.collection("matches")
            .whereEqualTo("initiatorUserId", currentUserId)
            .get()
            .await()
            .documents
            .forEach { document ->
                document.getString("roomId")?.let {
                    matchedRooms.add(it)
                }
            }

        /*
        val localMatchedRooms = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getMatchedRooms(it) }
        }

        matchedRooms.clear()
        if (localMatchedRooms != null) {
            matchedRooms.addAll(listOf(localMatchedRooms.map { it.roomId }.toString()))
        } */
        return matchedRooms
    }

    suspend fun getLikedUsers(): Set<String> {
        val likedUsers = mutableSetOf<String>()

        // Fetch liked users from Firestore
        firestore.collection("likes")
            .whereEqualTo("likingUserId", currentUserId)
            .get()
            .await()
            .documents
            .forEach { document ->
                document.getString("userId")?.let {
                    likedUsers.add(it)
                }
            }

        /*
        val localLikedUsers = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getLikedUsers(it) }
        }

        likedUsers.clear()
        if (localLikedUsers != null) {
            likedUsers.addAll(listOf(localLikedUsers.map { it.likedUserId }.toString()))
        } */
        return likedUsers
    }

    suspend fun getMatchedUsers(): Set<String> {
        val matchedUsers = mutableSetOf<String>()

        // Fetch matched users from Firestore
        firestore.collection("matches")
            .whereEqualTo("initiatorUserId", currentUserId)
            .get()
            .await()
            .documents
            .forEach { document ->
                document.getString("userId")?.let {
                    matchedUsers.add(it)
                }
            }

        /*
        val localMatchedUsers = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getMatchedUsers(it) }
        }

        matchedUsers.clear()
        if (localMatchedUsers != null) {
            matchedUsers.addAll(listOf(localMatchedUsers.map { it.targetUserId }.toString()))
        } */
        return matchedUsers
    }


    suspend fun getLikesAndMatches(): Pair<List<Like>, List<Match>> {
        // Implement logic to fetch likes and matches from local Room database
        val likes = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getLikesForUser(it) }
        }

        val matches = withContext(Dispatchers.IO) {
            currentUserId?.let { likeMatchDao.getMatchesForUser(it) }
        }

        return Pair(likes ?: emptyList(), matches ?: emptyList())
    }

    /*
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
*/
    suspend fun unmatchRoom(roomId: String, matchedUserId: String) {
        matchedRooms.remove(roomId)

        firestore.collection("matches")
            .whereEqualTo("initiatorUserId", currentUserId)
            .whereEqualTo("roomId", roomId)
            .whereEqualTo("targetUserId", matchedUserId)
            .get()
            .await()
            .documents
            .forEach { it.reference.delete() }

    }

    suspend fun unmatchUser(userId: String, matchedUserId: String) {
        matchedUsers.remove(userId)

        firestore.collection("matches")
            .whereEqualTo("initiatorUserId", currentUserId)
            .whereEqualTo("userId", userId)
            .whereEqualTo("targetUserId", matchedUserId)
            .get()
            .await()
            .documents
            .forEach { it.reference.delete() }
    }


}

