package pt.ipca.roomies.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "profile_tags")
data class ProfileTags(
    @PrimaryKey
    @ColumnInfo(name = "tagId")
    val tagId: String = "",
    @ColumnInfo(name = "tagName")
    val tagName: String = "",
    @ColumnInfo(name = "tagType")
    val tagType: TagType = TagType.Interest,
    @ColumnInfo(name = "isSelected")
    val isSelected: Boolean = false
)



@Entity(tableName = "user_tags")
data class UserTags(
    @PrimaryKey
    @ColumnInfo(name = "userId")
    val userId: String,
    @ColumnInfo(name = "tagId")
    val tagId: String,
    @ColumnInfo(name = "tagType")
    val tagType: TagType,
    @ColumnInfo(name = "isSelected")
    var isSelected: Boolean,
    @ColumnInfo(name = "tagName")
    val tagName: String = ""
)

enum class TagType {
    Language, Interest, Personality
}

@Entity(tableName = "selected_tags")
data class SelectedTag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "userId")
    val userId: String,
    @ColumnInfo(name = "tagId")
    val tagId: String,
    @ColumnInfo(name = "tagType")
    val tagType: TagType,
    @ColumnInfo(name = "isSelected")
    val isSelected: Boolean
)



@Entity(tableName = "user_profiles_with_tags")
data class UserProfileWithTags(
    @Embedded val userProfile: UserProfile,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val userTags: List<UserTags>,
    @Relation(
        parentColumn = "tagId",
        entityColumn = "tagId"
    )
    val profileTags: List<ProfileTags>,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val selectedTags: List<SelectedTag>
)