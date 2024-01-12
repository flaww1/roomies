package pt.ipca.roomies.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pt.ipca.roomies.data.entities.ProfileTags
import pt.ipca.roomies.data.entities.SelectedTag
import pt.ipca.roomies.data.entities.TagType
import pt.ipca.roomies.data.entities.UserTags

@Dao
interface UserTagsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserTags(userTags: List<UserTags>)

    @Query("SELECT * FROM user_tags WHERE userId = :userId")
    fun getUserTags(userId: String): LiveData<List<UserTags>>


}
@Dao
interface ProfileTagsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfileTags(profileTags: List<ProfileTags>)

    @Query("SELECT * FROM profile_tags WHERE tagType = :tagType")
    fun getProfileTagsByType(tagType: TagType): LiveData<List<ProfileTags>>



}

@Dao
interface SelectedTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectedTags(selectedTags: List<SelectedTag>)

}