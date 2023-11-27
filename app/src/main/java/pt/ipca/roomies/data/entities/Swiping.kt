package pt.ipca.roomies.data.entities

data class Like(
    val likedUserId: String,
    val likingUserId: String,
    val roomId: String,
    val timestamp: Long
)

data class Match(
    val targetUserId: String,
    val initiatorUserId: String,
    val roomId: String,
    val timestamp: Long
)