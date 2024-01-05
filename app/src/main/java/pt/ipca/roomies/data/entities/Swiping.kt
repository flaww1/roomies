package pt.ipca.roomies.data.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "likes")
data class Like(
    @PrimaryKey
    val likeId: String,
    val likedUserId: String,
    val likingUserId: String,
    val roomId: String,
    val timestamp: Long
)

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey
    val matchId: String,
    val targetUserId: String,
    val initiatorUserId: String,
    val roomId: String,
    val timestamp: Long
)

