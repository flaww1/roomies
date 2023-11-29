import pt.ipca.roomies.data.entities.SelectedTag

data class UserProfile(
    val userProfileId: String,
    val userId: String, // Foreign key to link with User entity
    val location: String,
    val bio: String,
    val gender: String,
    val occupation: String,
    val selectedTags: List<SelectedTag>,
    val profilePictureUrl: String,
    val birthDate: String,

    // Add other fields as needed
)

enum class Occupation {
    STUDENT,
    STUDENT_WORKER,
    WORKER,
    OTHER;

    // You can add additional properties or methods if needed
}
enum class Gender {
    MALE,
    FEMALE,
    NON_BINARY,
    OTHER;
}

