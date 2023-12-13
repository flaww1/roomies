package pt.ipca.roomies.data.entities

import pt.ipca.roomies.data.entities.SelectedTag

data class UserProfile(
    val bio: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val location: String = "",
    val occupation: String = "",
    val profilePictureUrl: String = "",
    val userId: String = ""
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

