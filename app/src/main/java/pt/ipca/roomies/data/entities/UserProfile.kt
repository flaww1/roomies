package pt.ipca.roomies.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import pt.ipca.roomies.data.entities.SelectedTag
import pt.ipca.roomies.data.entities.Converters.*



@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val userId: String,
    val bio: String,
    val birthDate: String,
    val gender: String,
    val location: String,
    val occupation: String,
    val profilePictureUrl: String,
    var documentId: String? = null



)

@TypeConverters(OccupationConverter::class)
enum class Occupation {
    STUDENT,
    STUDENT_WORKER,
    WORKER,
    OTHER;

}

@TypeConverters(GenderConverter::class)
enum class Gender {
    MALE,
    FEMALE,
    NON_BINARY,
    OTHER;
}

