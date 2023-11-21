package pt.ipca.roomies.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import pt.ipca.roomies.data.entities.User
import pt.ipca.roomies.data.entities.UserProfile

@Dao
interface UserRegistrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)

    // Add other DAO methods as needed
}