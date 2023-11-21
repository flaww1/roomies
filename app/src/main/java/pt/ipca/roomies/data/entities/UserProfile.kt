package pt.ipca.roomies.data.entities

import androidx.room.PrimaryKey
import androidx.room.Entity
import androidx.room.ForeignKey

import androidx.room.ForeignKey.Companion.CASCADE


@Entity(
    tableName = "user_profiles",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = CASCADE
    )]
)
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val userProfileId: Long = 0,
    val userId: Long, // Foreign key to link with User entity
    val profilePictureUrl: String,
    val location: String,
    val bio: String,
    val pronouns: Pronouns,
    val gender: Gender,
    val occupation: Occupation,
    // Add other fields as needed
)
// Gender.kt
enum class Gender {
    MALE,
    FEMALE,
    NON_BINARY,
    OTHER;

    // You can add additional properties or methods if needed
}

// Occupation.kt
enum class Occupation {
    STUDENT,
    STUDENT_WORKER,
    WORKER,
    OTHER;

    // You can add additional properties or methods if needed
}

// Pronouns.kt
enum class Pronouns {
    HE_HIM,
    SHE_HER,
    THEY_THEM,
    OTHER;

    // You can add additional properties or methods if needed
}
