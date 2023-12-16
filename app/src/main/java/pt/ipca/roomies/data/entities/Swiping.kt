package pt.ipca.roomies.data.entities

data class Like(
    val likeId: String,
    val likedUserId: String,
    val likingUserId: String,
    val roomId: String,
    val timestamp: Long
)

data class Match(
    val matchId: String,
    val targetUserId: String,
    val initiatorUserId: String,
    val roomId: String,
    val timestamp: Long
)