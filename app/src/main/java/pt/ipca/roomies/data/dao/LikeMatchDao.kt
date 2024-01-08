package pt.ipca.roomies.data.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pt.ipca.roomies.data.entities.Like
import pt.ipca.roomies.data.entities.Match

@Dao
interface LikeMatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: Like)

    @Query("SELECT * FROM likes WHERE likeId = :likeId")
    suspend fun getLikeById(likeId: String): Like?


    @Query("SELECT * FROM matches WHERE matchId = :matchId")
    suspend fun getMatchById(matchId: String): Match?


    // Insert like for a room
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoomLike(like: Like)

    // Insert like for a user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserLike(like: Like)

    // Delete likes for a room
    @Query("DELETE FROM likes WHERE roomId = :roomId")
    suspend fun deleteRoomLikes(roomId: String)

    // Delete likes for a user
    @Query("DELETE FROM likes WHERE likedUserId = :userId")
    suspend fun deleteUserLikes(userId: String)

    // Insert match
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: Match)

    // Get liked users
    @Query("SELECT likedUserId FROM likes WHERE likingUserId = :userId")
    suspend fun getLikedUsers(userId: String): List<String>

    // Get liked rooms
    @Query("SELECT roomId FROM likes WHERE likingUserId = :userId")
    suspend fun getLikedRooms(userId: String): List<String>

    // Get matched users
    @Query("SELECT targetUserId FROM matches WHERE initiatorUserId = :userId")
    suspend fun getMatchedUsers(userId: String): List<String>

    // Get matched rooms
    @Query("SELECT roomId FROM matches WHERE initiatorUserId = :userId")
    suspend fun getMatchedRooms(userId: String): List<String>

    // Get all likes
    @Query("SELECT * FROM likes WHERE likedUserId = :userId OR likingUserId = :userId")
    suspend fun getLikesForUser(userId: String): List<Like>

    @Query("SELECT * FROM matches WHERE targetUserId = :userId OR initiatorUserId = :userId")
    suspend fun getMatchesForUser(userId: String): List<Match>

    // Delete likes
    @Query("DELETE FROM likes WHERE roomId = :roomId AND likedUserId = :likedUserId")
    suspend fun deleteLikes(roomId: String, likedUserId: String)
    @Query("SELECT targetUserId FROM matches WHERE initiatorUserId = :userId")
    suspend fun getCurrentMatchedUsers(userId: String): List<String>

    // Get current matched rooms
    @Query("SELECT roomId FROM matches WHERE initiatorUserId = :userId")
    suspend fun getCurrentMatchedRooms(userId: String): List<String>
    @Query("SELECT * FROM likes")
    suspend fun getAllLikes(): List<Like>

    @Query("SELECT * FROM matches")
    suspend fun getAllMatches(): List<Match>



}
